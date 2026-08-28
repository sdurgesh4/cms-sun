package com.inturn.suncomputer.payment.service;

import com.inturn.suncomputer.common.exception.BusinessRuleException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;

import com.inturn.suncomputer.enrollment.entity.Enrollment;
import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;
import com.inturn.suncomputer.enrollment.repository.EnrollmentRepository;

import com.inturn.suncomputer.installment.entity.Installment;
import com.inturn.suncomputer.installment.entity.InstallmentStatus;
import com.inturn.suncomputer.installment.repository.InstallmentRepository;

import com.inturn.suncomputer.payment.dto.CreatePaymentRequest;
import com.inturn.suncomputer.payment.dto.PaymentResponse;
import com.inturn.suncomputer.payment.dto.PaymentSummaryResponse;

import com.inturn.suncomputer.payment.entity.Payment;
import com.inturn.suncomputer.payment.entity.PaymentStatus;
import com.inturn.suncomputer.payment.repository.PaymentRepository;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final InstallmentRepository installmentRepository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "installment_id"
    )
    private Installment installment;



    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            EnrollmentRepository enrollmentRepository,
            InstallmentRepository installmentRepository
    ) {

        this.paymentRepository =
                paymentRepository;

        this.enrollmentRepository =
                enrollmentRepository;

        this.installmentRepository =
                installmentRepository;
    }

    @Override
    public PaymentResponse createPayment(
            CreatePaymentRequest request
    ) {

        /*
         * STEP 1
         * Find the enrollment.
         */

        Enrollment enrollment =
                enrollmentRepository
                        .findById(
                                request.enrollmentId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Enrollment not found with id: "
                                                + request.enrollmentId()
                                )
                        );


        /*
         * STEP 2
         * Payment is allowed only for
         * an active enrollment.
         */

        if (
                enrollment.getStatus()
                        != EnrollmentStatus.ACTIVE
        ) {

            throw new BusinessRuleException(
                    "Payment can only be added to an active enrollment"
            );
        }


        /*
         * STEP 3
         * Payment date cannot be in future.
         */

        if (
                request.paymentDate()
                        .isAfter(
                                LocalDate.now()
                        )
        ) {

            throw new BusinessRuleException(
                    "Payment date cannot be in the future"
            );
        }


        /*
         * STEP 4
         * Find installment if installmentId
         * was provided.
         *
         * installmentId is optional because
         * old payments might not have an
         * installment associated with them.
         */

        Installment installment = null;

        if (
                request.installmentId() != null
        ) {

            installment =
                    installmentRepository
                            .findById(
                                    request.installmentId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Installment not found with id: "
                                                    + request.installmentId()
                                    )
                            );


            /*
             * STEP 5
             * Make sure the installment belongs
             * to the same enrollment.
             *
             * Example:
             *
             * Payment enrollment = 1
             * Installment enrollment = 2
             *
             * This must be rejected.
             */

            if (
                    !installment
                            .getEnrollment()
                            .getId()
                            .equals(
                                    enrollment.getId()
                            )
            ) {

                throw new BusinessRuleException(
                        "Installment does not belong to this enrollment"
                );
            }


            /*
             * STEP 6
             * Cancelled installments cannot
             * receive payments.
             */

            if (
                    installment.getStatus()
                            == InstallmentStatus.CANCELLED
            ) {

                throw new BusinessRuleException(
                        "Cannot make payment against a cancelled installment"
                );
            }


            /*
             * STEP 7
             * Calculate how much has already
             * been paid against this installment.
             */

            BigDecimal installmentPaid =
                    paymentRepository
                            .getTotalPaidForInstallment(
                                    installment.getId(),
                                    PaymentStatus.SUCCESS
                            );


            /*
             * STEP 8
             * Calculate remaining amount
             * for this installment.
             *
             * Example:
             *
             * Installment = ₹10,000
             * Already paid = ₹6,000
             *
             * Outstanding = ₹4,000
             */

            BigDecimal installmentOutstanding =
                    installment.getAmount()
                            .subtract(
                                    installmentPaid
                            );


            /*
             * STEP 9
             * Prevent overpayment of installment.
             */

            if (
                    request.amount()
                            .compareTo(
                                    installmentOutstanding
                            ) > 0
            ) {

                throw new BusinessRuleException(
                        "Payment exceeds installment outstanding amount. "
                                + "Outstanding: ₹"
                                + installmentOutstanding
                );
            }
        }


        /*
         * STEP 10
         * Calculate total amount already
         * paid against the enrollment.
         */

        BigDecimal totalPaid =
                paymentRepository
                        .getTotalPaid(
                                enrollment.getId(),
                                PaymentStatus.SUCCESS
                        );


        /*
         * STEP 11
         * Calculate enrollment outstanding.
         *
         * Example:
         *
         * Final Fee = ₹45,000
         * Paid      = ₹25,000
         *
         * Outstanding = ₹20,000
         */

        BigDecimal outstanding =
                enrollment.getFinalFee()
                        .subtract(
                                totalPaid
                        );


        /*
         * STEP 12
         * Prevent payment above the
         * enrollment's total outstanding.
         */

        if (
                request.amount()
                        .compareTo(
                                outstanding
                        ) > 0
        ) {

            throw new BusinessRuleException(
                    "Payment amount exceeds outstanding amount. "
                            + "Outstanding: ₹"
                            + outstanding
            );
        }


        /*
         * STEP 13
         * Create Payment entity.
         */

        Payment payment =
                new Payment();


        /*
         * Link payment to enrollment.
         */

        payment.setEnrollment(
                enrollment
        );


        /*
         * Link payment to installment.
         *
         * This will be null when the payment
         * does not have an installment.
         */

        payment.setInstallment(
                installment
        );


        /*
         * STEP 14
         * Generate receipt number.
         */

        payment.setReceiptNumber(
                generateReceiptNumber()
        );


        /*
         * STEP 15
         * Set payment details.
         */

        payment.setPaymentDate(
                request.paymentDate()
        );

        payment.setAmount(
                request.amount()
        );

        payment.setPaymentMethod(
                request.paymentMethod()
        );

        payment.setTransactionReference(
                request.transactionReference()
        );


        /*
         * For now every manually created payment
         * is considered SUCCESS.
         *
         * Later, online UPI payments will start
         * as PENDING and become SUCCESS only
         * after gateway verification.
         */

        payment.setStatus(
                PaymentStatus.SUCCESS
        );

        payment.setNotes(
                request.notes()
        );


        /*
         * STEP 16
         * Save payment.
         */

        Payment savedPayment =
                paymentRepository.save(
                        payment
                );


        /*
         * STEP 17
         * Return response.
         */

        return mapToResponse(
                savedPayment
        );
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(
            Long id
    ) {

        Payment payment =
                findPayment(id);

        return mapToResponse(
                payment
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse>
    getAllPayments() {

        return paymentRepository
                .findAll()
                .stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse>
    getPaymentsByEnrollment(
            Long enrollmentId
    ) {

        /*
         * Check whether enrollment exists.
         */

        if (
                !enrollmentRepository
                        .existsById(
                                enrollmentId
                        )
        ) {

            throw new ResourceNotFoundException(
                    "Enrollment not found with id: "
                            + enrollmentId
            );
        }


        return paymentRepository
                .findByEnrollmentId(
                        enrollmentId
                )
                .stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public PaymentSummaryResponse
    getPaymentSummary(
            Long enrollmentId
    ) {

        /*
         * Find enrollment.
         */

        Enrollment enrollment =
                enrollmentRepository
                        .findById(
                                enrollmentId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Enrollment not found with id: "
                                                + enrollmentId
                                )
                        );


        /*
         * Calculate total successful payments.
         */

        BigDecimal totalPaid =
                paymentRepository
                        .getTotalPaid(
                                enrollmentId,
                                PaymentStatus.SUCCESS
                        );


        /*
         * Get final course fee.
         */

        BigDecimal totalFee =
                enrollment.getFinalFee();


        /*
         * Calculate outstanding amount.
         */

        BigDecimal outstanding =
                totalFee.subtract(
                        totalPaid
                );


        /*
         * Return summary.
         */

        return new PaymentSummaryResponse(

                enrollmentId,

                totalFee,

                totalPaid,

                outstanding,

                outstanding.compareTo(
                        BigDecimal.ZERO
                ) == 0
        );
    }


    /*
     * Find payment by ID.
     */

    private Payment findPayment(
            Long id
    ) {

        return paymentRepository
                .findById(
                        id
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: "
                                        + id
                        )
                );
    }


    /*
     * Generate receipt number.
     *
     * Example:
     * SC-20260826-1756181234567
     *
     * We will improve this later with a
     * proper database sequence.
     */

    private String generateReceiptNumber() {

        return "SC-"
                + LocalDate.now()
                .toString()
                .replace(
                        "-",
                        ""
                )
                + "-"
                + System.currentTimeMillis();
    }


    /*
     * Convert Payment entity into
     * PaymentResponse DTO.
     */

    private PaymentResponse mapToResponse(
            Payment payment
    ) {

        Enrollment enrollment =
                payment.getEnrollment();

        var student =
                enrollment.getStudent();

        var user =
                student.getUser();

        var batch =
                enrollment.getBatch();

        var course =
                batch.getCourse();


        String studentName =
                user.getFirstName()
                        + " "
                        + user.getLastName();


        return new PaymentResponse(

                payment.getId(),

                enrollment.getId(),

                student.getId(),

                student.getStudentCode(),

                studentName,

                batch.getBatchCode(),

                course.getName(),

                payment.getReceiptNumber(),

                payment.getPaymentDate(),

                payment.getAmount(),

                payment.getPaymentMethod(),

                payment.getTransactionReference(),

                payment.getStatus(),

                payment.getNotes(),

                payment.getCreatedAt()
        );
    }
}
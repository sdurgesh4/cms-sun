package com.inturn.suncomputer.installment.service;

import com.inturn.suncomputer.common.exception.BusinessRuleException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;

import com.inturn.suncomputer.enrollment.entity.Enrollment;
import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;
import com.inturn.suncomputer.enrollment.repository.EnrollmentRepository;

import com.inturn.suncomputer.installment.dto.CreateInstallmentRequest;
import com.inturn.suncomputer.installment.dto.InstallmentResponse;
import com.inturn.suncomputer.installment.dto.InstallmentSummaryResponse;

import com.inturn.suncomputer.installment.entity.Installment;
import com.inturn.suncomputer.installment.entity.InstallmentStatus;

import com.inturn.suncomputer.installment.repository.InstallmentRepository;

import com.inturn.suncomputer.payment.entity.PaymentStatus;
import com.inturn.suncomputer.payment.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class InstallmentServiceImpl
        implements InstallmentService {

    private final InstallmentRepository installmentRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final PaymentRepository paymentRepository;

    public InstallmentServiceImpl(
            InstallmentRepository installmentRepository,
            EnrollmentRepository enrollmentRepository,
            PaymentRepository paymentRepository
    ) {

        this.installmentRepository =
                installmentRepository;

        this.enrollmentRepository =
                enrollmentRepository;

        this.paymentRepository =
                paymentRepository;
    }


    // =========================================================
    // CREATE INSTALLMENT
    // =========================================================

    @Override
    public InstallmentResponse createInstallment(
            CreateInstallmentRequest request
    ) {

        /*
         * Find enrollment.
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
         * Do not allow installments for
         * cancelled enrollments.
         */

        if (
                enrollment.getStatus()
                        == EnrollmentStatus.CANCELLED
        ) {

            throw new BusinessRuleException(
                    "Cannot create installment for cancelled enrollment"
            );
        }


        /*
         * Check duplicate installment number.
         *
         * Example:
         *
         * Enrollment 1
         * Installment 1 already exists
         *
         * Another Installment 1 should not be created.
         */

        if (
                installmentRepository
                        .existsByEnrollmentIdAndInstallmentNumber(
                                request.enrollmentId(),
                                request.installmentNumber()
                        )
        ) {

            throw new BusinessRuleException(
                    "Installment number already exists for this enrollment"
            );
        }


        /*
         * Calculate existing installment total.
         */

        BigDecimal existingTotal =
                installmentRepository
                        .getTotalInstallmentAmount(
                                enrollment.getId()
                        );


        /*
         * Calculate total after adding
         * the new installment.
         */

        BigDecimal newTotal =
                existingTotal.add(
                        request.amount()
                );


        /*
         * Installments must not exceed
         * final enrollment fee.
         */

        if (
                newTotal.compareTo(
                        enrollment.getFinalFee()
                ) > 0
        ) {

            BigDecimal remainingAmount =
                    enrollment.getFinalFee()
                            .subtract(
                                    existingTotal
                            );

            throw new BusinessRuleException(
                    "Total installments cannot exceed final fee. "
                            + "Remaining available amount: ₹"
                            + remainingAmount
            );
        }


        /*
         * Due date cannot be before
         * enrollment date.
         */

        if (
                request.dueDate()
                        .isBefore(
                                enrollment.getEnrollmentDate()
                        )
        ) {

            throw new BusinessRuleException(
                    "Installment due date cannot be before enrollment date"
            );
        }


        /*
         * Create installment.
         */

        Installment installment =
                new Installment();

        installment.setEnrollment(
                enrollment
        );

        installment.setInstallmentNumber(
                request.installmentNumber()
        );

        installment.setDueDate(
                request.dueDate()
        );

        installment.setAmount(
                request.amount()
        );

        installment.setStatus(
                InstallmentStatus.PENDING
        );

        installment.setNotes(
                request.notes()
        );


        /*
         * Save.
         */

        Installment saved =
                installmentRepository.save(
                        installment
                );


        /*
         * Return response.
         *
         * mapToResponse() automatically calculates
         * the current status.
         */

        return mapToResponse(
                saved
        );
    }


    // =========================================================
    // GET INSTALLMENT BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public InstallmentResponse getInstallmentById(
            Long id
    ) {

        Installment installment =
                findInstallment(id);

        return mapToResponse(
                installment
        );
    }


    // =========================================================
    // GET INSTALLMENTS BY ENROLLMENT
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<InstallmentResponse>
    getInstallmentsByEnrollment(
            Long enrollmentId
    ) {

        /*
         * Check enrollment exists.
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


        /*
         * Get installments in order.
         */

        return installmentRepository
                .findByEnrollmentIdOrderByInstallmentNumberAsc(
                        enrollmentId
                )
                .stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }


    // =========================================================
    // GET INSTALLMENT SUMMARY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public InstallmentSummaryResponse
    getInstallmentSummary(
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
         * Get all installments.
         */

        List<Installment> installments =
                installmentRepository
                        .findByEnrollmentIdOrderByInstallmentNumberAsc(
                                enrollmentId
                        );


        BigDecimal totalInstallmentAmount =
                BigDecimal.ZERO;

        BigDecimal totalPaid =
                BigDecimal.ZERO;

        long totalInstallments = 0;

        long paidInstallments = 0;

        long pendingInstallments = 0;

        long overdueInstallments = 0;


        /*
         * Process every installment.
         */

        for (
                Installment installment :
                installments
        ) {

            /*
             * Ignore cancelled installments.
             */

            if (
                    installment.getStatus()
                            == InstallmentStatus.CANCELLED
            ) {

                continue;
            }


            totalInstallments++;


            /*
             * Add installment amount.
             */

            totalInstallmentAmount =
                    totalInstallmentAmount.add(
                            installment.getAmount()
                    );


            /*
             * Calculate amount paid
             * against this installment.
             */

            BigDecimal paidAmount =
                    getPaidAmount(
                            installment
                    );


            totalPaid =
                    totalPaid.add(
                            paidAmount
                    );


            /*
             * Calculate current status.
             */

            InstallmentStatus status =
                    calculateStatus(
                            installment,
                            paidAmount
                    );


            /*
             * Count installments
             * according to status.
             */

            if (
                    status
                            == InstallmentStatus.PAID
            ) {

                paidInstallments++;

            } else if (
                    status
                            == InstallmentStatus.OVERDUE
            ) {

                overdueInstallments++;

            } else {

                pendingInstallments++;
            }
        }


        /*
         * Calculate total outstanding.
         */

        BigDecimal totalOutstanding =
                totalInstallmentAmount
                        .subtract(
                                totalPaid
                        );


        /*
         * Return summary.
         */

        return new InstallmentSummaryResponse(

                enrollment.getId(),

                totalInstallmentAmount,

                totalPaid,

                totalOutstanding,

                totalInstallments,

                paidInstallments,

                pendingInstallments,

                overdueInstallments
        );
    }


    // =========================================================
    // UPDATE OVERDUE INSTALLMENTS
    // =========================================================

    @Override
    public void updateOverdueInstallments() {

        List<Installment> installments =
                installmentRepository
                        .findByDueDateBeforeAndStatusIn(
                                LocalDate.now(),
                                List.of(
                                        InstallmentStatus.PENDING,
                                        InstallmentStatus.PARTIALLY_PAID
                                )
                        );

        for (
                Installment installment :
                installments
        ) {

            installment.setStatus(
                    InstallmentStatus.OVERDUE
            );
        }
    }


    // =========================================================
    // CALCULATE PAID AMOUNT
    // =========================================================

    private BigDecimal getPaidAmount(
            Installment installment
    ) {

        /*
         * Add only SUCCESS payments.
         *
         * PENDING payments are ignored.
         * FAILED payments are ignored.
         * REFUNDED payments are ignored.
         */

        return paymentRepository
                .getTotalPaidForInstallment(
                        installment.getId(),
                        PaymentStatus.SUCCESS
                );
    }


    // =========================================================
    // AUTOMATIC STATUS CALCULATION
    // =========================================================

    private InstallmentStatus calculateStatus(
            Installment installment,
            BigDecimal paidAmount
    ) {

        /*
         * CANCELLED always remains CANCELLED.
         */

        if (
                installment.getStatus()
                        == InstallmentStatus.CANCELLED
        ) {

            return InstallmentStatus.CANCELLED;
        }


        /*
         * If paid amount is equal to or
         * greater than installment amount,
         * installment is PAID.
         *
         * Example:
         *
         * Installment = ₹10,000
         * Paid        = ₹10,000
         *
         * Status = PAID
         */

        if (
                paidAmount.compareTo(
                        installment.getAmount()
                ) >= 0
        ) {

            return InstallmentStatus.PAID;
        }


        /*
         * If some amount is paid but
         * full amount is not paid:
         *
         * Example:
         *
         * Installment = ₹10,000
         * Paid        = ₹4,000
         *
         * Status = PARTIALLY_PAID
         */

        if (
                paidAmount.compareTo(
                        BigDecimal.ZERO
                ) > 0
        ) {

            /*
             * If due date has passed,
             * treat it as OVERDUE.
             */

            if (
                    installment.getDueDate()
                            .isBefore(
                                    LocalDate.now()
                            )
            ) {

                return InstallmentStatus.OVERDUE;
            }


            return InstallmentStatus.PARTIALLY_PAID;
        }


        /*
         * Nothing has been paid.
         *
         * If due date has passed:
         *
         * Status = OVERDUE
         */

        if (
                installment.getDueDate()
                        .isBefore(
                                LocalDate.now()
                        )
        ) {

            return InstallmentStatus.OVERDUE;
        }


        /*
         * Nothing paid and due date
         * has not passed.
         *
         * Status = PENDING
         */

        return InstallmentStatus.PENDING;
    }


    // =========================================================
    // FIND INSTALLMENT
    // =========================================================

    private Installment findInstallment(
            Long id
    ) {

        return installmentRepository
                .findById(
                        id
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Installment not found with id: "
                                        + id
                        )
                );
    }


    // =========================================================
    // MAP ENTITY TO RESPONSE
    // =========================================================

    private InstallmentResponse mapToResponse(
            Installment installment
    ) {

        /*
         * Calculate actual paid amount.
         */

        BigDecimal paidAmount =
                getPaidAmount(
                        installment
                );


        /*
         * Calculate outstanding amount.
         */

        BigDecimal outstandingAmount =
                installment.getAmount()
                        .subtract(
                                paidAmount
                        );


        /*
         * Calculate current status.
         */

        InstallmentStatus status =
                calculateStatus(
                        installment,
                        paidAmount
                );


        /*
         * Return response.
         */

        return new InstallmentResponse(

                installment.getId(),

                installment.getEnrollment()
                        .getId(),

                installment.getInstallmentNumber(),

                installment.getDueDate(),

                installment.getAmount(),

                paidAmount,

                outstandingAmount,

                status,

                installment.getNotes(),

                installment.getCreatedAt()
        );
    }
}
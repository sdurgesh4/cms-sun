package com.inturn.suncomputer.payment.repository;

import com.inturn.suncomputer.payment.entity.Payment;
import com.inturn.suncomputer.payment.entity.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByEnrollmentId(
            Long enrollmentId
    );

    List<Payment> findByInstallmentId(
            Long installmentId
    );

    List<Payment> findByEnrollmentIdAndStatus(
            Long enrollmentId,
            PaymentStatus status
    );

    List<Payment> findByStatus(
            PaymentStatus status
    );

    boolean existsByReceiptNumber(
            String receiptNumber
    );

    long countByStatus(
            PaymentStatus status
    );

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.enrollment.id = :enrollmentId
        AND p.status = :status
    """)
    BigDecimal getTotalPaid(
            Long enrollmentId,
            PaymentStatus status
    );

    @Query("""
        SELECT COALESCE(SUM(p.amount), 0)
        FROM Payment p
        WHERE p.installment.id = :installmentId
        AND p.status = :status
    """)
    BigDecimal getTotalPaidForInstallment(
            Long installmentId,
            PaymentStatus status
    );
}
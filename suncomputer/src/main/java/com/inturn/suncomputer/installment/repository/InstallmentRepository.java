package com.inturn.suncomputer.installment.repository;

import com.inturn.suncomputer.installment.entity.Installment;
import com.inturn.suncomputer.installment.entity.InstallmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InstallmentRepository
        extends JpaRepository<Installment, Long> {


    List<Installment>
    findByEnrollmentIdOrderByInstallmentNumberAsc(
            Long enrollmentId
    );


    List<Installment> findByStatus(
            InstallmentStatus status
    );


    List<Installment>
    findByDueDateBeforeAndStatusIn(
            LocalDate date,
            List<InstallmentStatus> statuses
    );


    boolean
    existsByEnrollmentIdAndInstallmentNumber(
            Long enrollmentId,
            Integer installmentNumber
    );


    @Query("""
        SELECT COALESCE(SUM(i.amount), 0)
        FROM Installment i
        WHERE i.enrollment.id = :enrollmentId
        AND i.status <> 'CANCELLED'
    """)
    BigDecimal getTotalInstallmentAmount(
            Long enrollmentId
    );
}
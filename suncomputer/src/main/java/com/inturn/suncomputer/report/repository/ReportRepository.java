package com.inturn.suncomputer.report.repository;

import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;
import com.inturn.suncomputer.payment.entity.PaymentStatus;
import com.inturn.suncomputer.student.entity.StudentStatus;

import com.inturn.suncomputer.report.dto.BatchStudentReportResponse;
import com.inturn.suncomputer.report.dto.FeeCollectionReportResponse;
import com.inturn.suncomputer.report.dto.PendingFeeReportResponse;
import com.inturn.suncomputer.report.dto.StudentReportResponse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.inturn.suncomputer.student.entity.Student;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository
        extends JpaRepository<Student, Long> {


    // =========================================================
    // STUDENT REPORT
    // =========================================================

    @Query("""
        SELECT new com.inturn.suncomputer.report.dto.StudentReportResponse(

            s.id,

            s.studentCode,

            CONCAT(
                u.firstName,
                ' ',
                COALESCE(u.lastName, '')
            ),

            u.email,

            u.phone,

            s.admissionDate,

            s.status,

            COUNT(
                CASE
                    WHEN e.status =
                        com.inturn.suncomputer.enrollment.entity.EnrollmentStatus.ACTIVE
                    THEN e.id
                END
            )

        )

        FROM Student s

        JOIN s.user u

        LEFT JOIN com.inturn.suncomputer.enrollment.entity.Enrollment e
            ON e.student.id = s.id

        WHERE
            (:status IS NULL OR s.status = :status)

        GROUP BY
            s.id,
            s.studentCode,
            u.firstName,
            u.lastName,
            u.email,
            u.phone,
            s.admissionDate,
            s.status

        ORDER BY
            s.studentCode
    """)
    List<StudentReportResponse> getStudentReport(

            @Param("status")
            StudentStatus status
    );


    // =========================================================
    // BATCH STUDENT REPORT
    // =========================================================

    @Query("""
        SELECT new com.inturn.suncomputer.report.dto.BatchStudentReportResponse(

            b.id,

            b.batchCode,

            s.id,

            s.studentCode,

            CONCAT(
                u.firstName,
                ' ',
                COALESCE(u.lastName, '')
            ),

            u.phone,

            e.enrollmentDate,

            e.finalFee,

            e.status

        )

        FROM Enrollment e

        JOIN e.batch b

        JOIN e.student s

        JOIN s.user u

        WHERE
            b.id = :batchId

        ORDER BY
            s.studentCode
    """)
    List<BatchStudentReportResponse> getBatchStudentReport(

            @Param("batchId")
            Long batchId
    );


    // =========================================================
    // FEE COLLECTION REPORT
    // =========================================================

    @Query("""
        SELECT new com.inturn.suncomputer.report.dto.FeeCollectionReportResponse(

            p.id,

            p.receiptNumber,

            p.paymentDate,

            p.amount,

            p.paymentMethod,

            p.status,

            s.id,

            s.studentCode,

            CONCAT(
                u.firstName,
                ' ',
                COALESCE(u.lastName, '')
            ),

            b.id,

            b.batchCode

        )

        FROM Payment p

        JOIN p.enrollment e

        JOIN e.student s

        JOIN s.user u

        JOIN e.batch b

        WHERE
            p.paymentDate BETWEEN :startDate AND :endDate

        ORDER BY
            p.paymentDate DESC,
            p.id DESC
    """)
    List<FeeCollectionReportResponse>
    getFeeCollectionReport(

            @Param("startDate")
            LocalDate startDate,

            @Param("endDate")
            LocalDate endDate
    );


    // =========================================================
    // PENDING FEE REPORT
    // =========================================================

    @Query("""
        SELECT new com.inturn.suncomputer.report.dto.PendingFeeReportResponse(

            e.id,

            s.id,

            s.studentCode,

            CONCAT(
                u.firstName,
                ' ',
                COALESCE(u.lastName, '')
            ),

            b.id,

            b.batchCode,

            e.finalFee,

            COALESCE(
                SUM(
                    CASE
                        WHEN p.status =
                            com.inturn.suncomputer.payment.entity.PaymentStatus.SUCCESS
                        THEN p.amount
                        ELSE 0
                    END
                ),
                0
            ),

            e.finalFee -

            COALESCE(
                SUM(
                    CASE
                        WHEN p.status =
                            com.inturn.suncomputer.payment.entity.PaymentStatus.SUCCESS
                        THEN p.amount
                        ELSE 0
                    END
                ),
                0
            )

        )

        FROM Enrollment e

        JOIN e.student s

        JOIN s.user u

        JOIN e.batch b

        LEFT JOIN Payment p
            ON p.enrollment.id = e.id

        WHERE
            e.status =
                com.inturn.suncomputer.enrollment.entity.EnrollmentStatus.ACTIVE

        GROUP BY
            e.id,
            s.id,
            s.studentCode,
            u.firstName,
            u.lastName,
            b.id,
            b.batchCode,
            e.finalFee

        HAVING
            e.finalFee -

            COALESCE(
                SUM(
                    CASE
                        WHEN p.status =
                            com.inturn.suncomputer.payment.entity.PaymentStatus.SUCCESS
                        THEN p.amount
                        ELSE 0
                    END
                ),
                0
            ) > 0

        ORDER BY
            s.studentCode
    """)
    List<PendingFeeReportResponse>
    getPendingFeeReport();
}
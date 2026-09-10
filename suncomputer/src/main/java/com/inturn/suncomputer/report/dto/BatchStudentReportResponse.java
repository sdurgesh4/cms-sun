package com.inturn.suncomputer.report.dto;

import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BatchStudentReportResponse(

        Long batchId,

        String batchCode,

        Long studentId,

        String studentCode,

        String studentName,

        String phone,

        LocalDate enrollmentDate,

        BigDecimal finalFee,

        EnrollmentStatus enrollmentStatus

) {
}
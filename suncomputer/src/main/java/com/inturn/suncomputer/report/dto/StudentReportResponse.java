package com.inturn.suncomputer.report.dto;

import com.inturn.suncomputer.student.entity.StudentStatus;

import java.time.LocalDate;

public record StudentReportResponse(

        Long studentId,

        String studentCode,

        String studentName,

        String email,

        String phone,

        LocalDate admissionDate,

        StudentStatus status,

        long activeEnrollments

) {
}
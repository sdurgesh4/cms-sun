package com.inturn.suncomputer.enrollment.dto;

import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EnrollmentResponse(

        Long id,

        Long studentId,

        String studentCode,

        String studentName,

        Long batchId,

        String batchCode,

        Long courseId,

        String courseCode,

        String courseName,

        Long teacherId,

        String teacherName,

        LocalDate enrollmentDate,

        BigDecimal agreedFee,

        BigDecimal discount,

        BigDecimal finalFee,

        EnrollmentStatus status,

        String notes,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
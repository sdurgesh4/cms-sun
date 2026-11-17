package com.inturn.suncomputer.enrollment.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEnrollmentRequest(

        @NotNull(
                message = "Student ID is required"
        )
        Long studentId,

        @NotNull(
                message = "Batch ID is required"
        )
        Long batchId,

        @NotNull(
                message = "Enrollment date is required"
        )
        LocalDate enrollmentDate,

        @NotNull(
                message = "Agreed fee is required"
        )
        @DecimalMin(
                value = "0.00",
                message = "Agreed fee cannot be negative"
        )
        BigDecimal agreedFee,

        @NotNull(
                message = "Discount is required"
        )
        @DecimalMin(
                value = "0.00",
                message = "Discount cannot be negative"
        )
        BigDecimal discount,

        @Size(
                max = 1000,
                message = "Notes cannot exceed 1000 characters"
        )
        String notes

) {
}
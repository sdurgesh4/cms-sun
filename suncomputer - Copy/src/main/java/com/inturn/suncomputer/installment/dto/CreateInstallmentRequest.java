package com.inturn.suncomputer.installment.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInstallmentRequest(

        @NotNull(
                message = "Enrollment ID is required"
        )
        Long enrollmentId,

        @NotNull(
                message = "Installment number is required"
        )
        @Min(
                value = 1,
                message = "Installment number must be at least 1"
        )
        Integer installmentNumber,

        @NotNull(
                message = "Due date is required"
        )
        LocalDate dueDate,

        @NotNull(
                message = "Installment amount is required"
        )
        @DecimalMin(
                value = "0.01",
                message = "Amount must be greater than zero"
        )
        BigDecimal amount,

        @Size(
                max = 1000,
                message = "Notes cannot exceed 1000 characters"
        )
        String notes

) {
}
package com.inturn.suncomputer.enquiry.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConvertEnquiryRequest(

        @NotNull(message = "Batch ID is required")
        Long batchId,

        @NotNull(message = "Agreed fee is required")
        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Agreed fee cannot be negative"
        )
        BigDecimal agreedFee,

        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Discount cannot be negative"
        )
        BigDecimal discount,

        LocalDate admissionDate,

        LocalDate enrollmentDate,

        @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
        String notes,

        @Size(max = 50, message = "Student code cannot exceed 50 characters")
        String studentCode

) {
}
package com.inturn.suncomputer.payment.dto;

import com.inturn.suncomputer.payment.entity.PaymentMethod;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePaymentRequest(

        @NotNull(
                message = "Enrollment ID is required"
        )
        Long enrollmentId,

        Long installmentId,

        @NotNull(
                message = "Payment date is required"
        )
        LocalDate paymentDate,

        @NotNull(
                message = "Payment amount is required"
        )
        @DecimalMin(
                value = "0.01",
                message = "Payment amount must be greater than zero"
        )
        BigDecimal amount,

        @NotNull(
                message = "Payment method is required"
        )
        PaymentMethod paymentMethod,

        @Size(
                max = 150,
                message = "Transaction reference cannot exceed 150 characters"
        )
        String transactionReference,

        @Size(
                max = 1000,
                message = "Notes cannot exceed 1000 characters"
        )
        String notes

) {
}
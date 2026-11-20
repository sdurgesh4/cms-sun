package com.inturn.suncomputer.payment.dto;

import com.inturn.suncomputer.payment.entity.PaymentMethod;
import com.inturn.suncomputer.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentResponse(

        Long id,

        Long enrollmentId,

        Long studentId,

        String studentCode,

        String studentName,

        String batchCode,

        String courseName,

        String receiptNumber,

        LocalDate paymentDate,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        String transactionReference,

        PaymentStatus status,

        String notes,

        LocalDateTime createdAt

) {
}
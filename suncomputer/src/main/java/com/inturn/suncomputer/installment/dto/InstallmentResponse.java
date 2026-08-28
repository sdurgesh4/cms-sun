package com.inturn.suncomputer.installment.dto;

import com.inturn.suncomputer.installment.entity.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InstallmentResponse(

        Long id,

        Long enrollmentId,

        Integer installmentNumber,

        LocalDate dueDate,

        BigDecimal amount,

        BigDecimal paidAmount,

        BigDecimal outstandingAmount,

        InstallmentStatus status,

        String notes,

        LocalDateTime createdAt

) {
}
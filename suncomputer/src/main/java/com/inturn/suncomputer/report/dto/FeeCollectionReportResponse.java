package com.inturn.suncomputer.report.dto;

import com.inturn.suncomputer.payment.entity.PaymentMethod;
import com.inturn.suncomputer.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FeeCollectionReportResponse(

        Long paymentId,

        String receiptNumber,

        LocalDate paymentDate,

        BigDecimal amount,

        PaymentMethod paymentMethod,

        PaymentStatus status,

        Long studentId,

        String studentCode,

        String studentName,

        Long batchId,

        String batchCode

) {
}
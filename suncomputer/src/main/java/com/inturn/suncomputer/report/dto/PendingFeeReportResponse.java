package com.inturn.suncomputer.report.dto;

import java.math.BigDecimal;

public record PendingFeeReportResponse(

        Long enrollmentId,

        Long studentId,

        String studentCode,

        String studentName,

        Long batchId,

        String batchCode,

        BigDecimal finalFee,

        BigDecimal paidAmount,

        BigDecimal pendingAmount

) {
}
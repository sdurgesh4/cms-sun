package com.inturn.suncomputer.installment.dto;

import java.math.BigDecimal;

public record InstallmentSummaryResponse(

        Long enrollmentId,

        BigDecimal totalInstallmentAmount,

        BigDecimal totalPaid,

        BigDecimal totalOutstanding,

        long totalInstallments,

        long paidInstallments,

        long pendingInstallments,

        long overdueInstallments

) {
}
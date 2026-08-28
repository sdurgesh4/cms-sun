package com.inturn.suncomputer.payment.dto;

import java.math.BigDecimal;

public record PaymentSummaryResponse(

        Long enrollmentId,

        BigDecimal totalFee,

        BigDecimal totalPaid,

        BigDecimal outstanding,

        boolean fullyPaid

) {
}
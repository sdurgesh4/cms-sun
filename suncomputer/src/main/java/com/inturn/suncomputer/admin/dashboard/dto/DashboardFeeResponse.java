package com.inturn.suncomputer.admin.dashboard.dto;

import java.math.BigDecimal;

public record DashboardFeeResponse(

        BigDecimal totalFees,

        BigDecimal collectedFees,

        BigDecimal pendingFees,

        BigDecimal todayCollection,

        BigDecimal overdueInstallmentAmount,

        long overdueInstallmentCount

) {
}
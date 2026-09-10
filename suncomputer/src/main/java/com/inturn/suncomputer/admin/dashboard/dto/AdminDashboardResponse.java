package com.inturn.suncomputer.admin.dashboard.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminDashboardResponse(

        LocalDateTime generatedAt,

        LocalDate dashboardDate,

        DashboardSummaryResponse summary,

        DashboardFeeResponse fees,

        DashboardAttendanceResponse attendance,

        DashboardEnquiryResponse enquiries,

        long unreadNotifications

) {
}
package com.inturn.suncomputer.admin.dashboard.dto;

public record DashboardEnquiryResponse(

        long totalEnquiries,

        long pendingEnquiries,

        long convertedEnquiries,

        long todayFollowUps

) {
}
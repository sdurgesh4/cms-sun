package com.inturn.suncomputer.admin.dashboard.service;

import com.inturn.suncomputer.admin.dashboard.dto.AdminDashboardResponse;

public interface AdminDashboardService {

    AdminDashboardResponse getDashboard(
            String username
    );
}
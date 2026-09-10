package com.inturn.suncomputer.admin.dashboard.dto;

import java.math.BigDecimal;

public record DashboardAttendanceResponse(

        long totalMarked,

        long present,

        long absent,

        long late,

        long leave,

        BigDecimal attendancePercentage

) {
}
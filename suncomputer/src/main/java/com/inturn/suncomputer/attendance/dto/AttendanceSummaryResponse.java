package com.inturn.suncomputer.attendance.dto;

public record AttendanceSummaryResponse(

        Long enrollmentId,

        long totalDays,

        long presentDays,

        long absentDays,

        long lateDays,

        long leaveDays,

        double attendancePercentage

) {
}
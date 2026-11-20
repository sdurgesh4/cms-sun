package com.inturn.suncomputer.attendance.dto;

public record BatchAttendanceDashboardResponse(

        Long batchId,

        String batchCode,

        long totalStudents,

        long totalPresent,

        long totalAbsent,

        long totalLate,

        long totalLeave,

        double averageAttendancePercentage

) {
}
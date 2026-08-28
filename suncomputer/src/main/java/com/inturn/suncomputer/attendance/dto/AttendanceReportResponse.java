package com.inturn.suncomputer.attendance.dto;

public record AttendanceReportResponse(

        Long enrollmentId,

        Long studentId,

        String studentCode,

        String studentName,

        Long batchId,

        String batchCode,

        long totalDays,

        long presentDays,

        long absentDays,

        long lateDays,

        long leaveDays,

        long attendedDays,

        double attendancePercentage

) {
}
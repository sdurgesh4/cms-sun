package com.inturn.suncomputer.attendance.dto;

public record LowAttendanceStudentResponse(

        Long enrollmentId,

        Long studentId,

        String studentCode,

        String studentName,

        Long batchId,

        String batchCode,

        long totalDays,

        long attendedDays,

        double attendancePercentage

) {
}
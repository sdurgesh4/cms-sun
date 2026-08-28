package com.inturn.suncomputer.attendance.dto;

import com.inturn.suncomputer.attendance.entity.AttendanceStatus;

import java.time.LocalDate;

public record BatchAttendanceResponse(

        Long enrollmentId,

        Long studentId,

        String studentCode,

        String studentName,

        Long batchId,

        String batchCode,

        LocalDate attendanceDate,

        AttendanceStatus status,

        String remarks,

        boolean marked

) {
}
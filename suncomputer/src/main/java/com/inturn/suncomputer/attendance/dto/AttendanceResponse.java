package com.inturn.suncomputer.attendance.dto;

import com.inturn.suncomputer.attendance.entity.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(

        Long id,

        Long enrollmentId,

        Long studentId,

        String studentCode,

        String studentName,

        String batchCode,

        String courseName,

        LocalDate attendanceDate,

        AttendanceStatus status,

        String remarks,

        LocalDateTime createdAt

) {
}
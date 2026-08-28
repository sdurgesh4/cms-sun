package com.inturn.suncomputer.attendance.dto;

import com.inturn.suncomputer.attendance.entity.AttendanceStatus;

import java.time.LocalDate;

public record DailyAttendanceReportResponse(

        LocalDate attendanceDate,

        AttendanceStatus status,

        long studentCount

) {
}
package com.inturn.suncomputer.attendance.dto;

import com.inturn.suncomputer.attendance.entity.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateAttendanceRequest(

        @NotNull
        Long enrollmentId,

        @NotNull
        LocalDate attendanceDate,

        @NotNull
        AttendanceStatus status,

        @Size(max = 500)
        String remarks

) {
}
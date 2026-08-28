package com.inturn.suncomputer.attendance.dto;

import com.inturn.suncomputer.attendance.entity.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BatchAttendanceRecordRequest(

        @NotNull
        Long enrollmentId,

        @NotNull
        AttendanceStatus status,

        @Size(max = 500)
        String remarks

) {
}
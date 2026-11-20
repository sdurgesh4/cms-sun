package com.inturn.suncomputer.attendance.repository.projection;

import com.inturn.suncomputer.attendance.entity.AttendanceStatus;

import java.time.LocalDate;

public interface DailyAttendanceProjection {

    LocalDate getAttendanceDate();

    AttendanceStatus getStatus();

    Long getStudentCount();
}
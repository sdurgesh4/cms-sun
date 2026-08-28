package com.inturn.suncomputer.attendance.repository.projection;

public interface AttendanceReportProjection {

    Long getEnrollmentId();

    Long getStudentId();

    String getStudentCode();

    String getStudentName();

    Long getBatchId();

    String getBatchCode();

    Long getTotalDays();

    Long getPresentDays();

    Long getAbsentDays();

    Long getLateDays();

    Long getLeaveDays();
}
package com.inturn.suncomputer.attendance.service;

import com.inturn.suncomputer.attendance.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceResponse createAttendance(
            CreateAttendanceRequest request
    );

    List<DailyAttendanceReportResponse> getDailyAttendanceReport(
            Long batchId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<AttendanceResponse> createBatchAttendance(
            BatchAttendanceRequest request
    );

    AttendanceResponse getAttendanceById(
            Long id
    );

    List<AttendanceResponse> getAttendanceByEnrollment(
            Long enrollmentId
    );

    List<AttendanceResponse> getAttendanceBetweenDates(
            Long enrollmentId,
            LocalDate startDate,
            LocalDate endDate
    );

    AttendanceSummaryResponse getAttendanceSummary(
            Long enrollmentId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<BatchAttendanceResponse> getBatchAttendance(
            Long batchId,
            LocalDate attendanceDate
    );


    List<AttendanceReportResponse> getBatchAttendanceReport(
            Long batchId,
            LocalDate startDate,
            LocalDate endDate
    );


    StudentAttendanceDashboardResponse
    getStudentAttendanceDashboard(
            Long enrollmentId,
            LocalDate startDate,
            LocalDate endDate
    );

    BatchAttendanceDashboardResponse
    getBatchAttendanceDashboard(
            Long batchId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<LowAttendanceStudentResponse>
    getLowAttendanceStudents(
            Long batchId,
            LocalDate startDate,
            LocalDate endDate,
            double threshold
    );
}
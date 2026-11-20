package com.inturn.suncomputer.teacher.portal.service;

import com.inturn.suncomputer.attendance.dto.*;
import com.inturn.suncomputer.batch.dto.BatchResponse;
import com.inturn.suncomputer.teacher.portal.dto.TeacherProfileResponse;
import com.inturn.suncomputer.teacher.portal.dto.TeacherStudentResponse;
import java.time.LocalDate;
import java.util.List;

public interface TeacherPortalService {
    TeacherProfileResponse getMyProfile(String username);
    List<BatchResponse> getMyBatches(String username);
    List<TeacherStudentResponse> getStudentsInBatch(String username, Long batchId);
    List<BatchAttendanceResponse> getBatchAttendance(String username, Long batchId, LocalDate date);
    List<AttendanceResponse> saveBatchAttendance(String username, BatchAttendanceRequest request);
    BatchAttendanceDashboardResponse getAttendanceDashboard(String username, Long batchId, LocalDate startDate, LocalDate endDate);
    List<AttendanceReportResponse> getAttendanceReport(String username, Long batchId, LocalDate startDate, LocalDate endDate);
    List<LowAttendanceStudentResponse> getLowAttendance(String username, Long batchId, LocalDate startDate, LocalDate endDate, double threshold);
    List<DailyAttendanceReportResponse> getDailyAttendanceReport(
            String username,
            Long batchId,
            LocalDate startDate,
            LocalDate endDate
    );
}

package com.inturn.suncomputer.teacher.portal.controller;

import com.inturn.suncomputer.attendance.dto.*;
import com.inturn.suncomputer.batch.dto.BatchResponse;
import com.inturn.suncomputer.teacher.portal.dto.TeacherProfileResponse;
import com.inturn.suncomputer.teacher.portal.dto.TeacherStudentResponse;
import com.inturn.suncomputer.teacher.portal.service.TeacherPortalService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherPortalController {
    private final TeacherPortalService teacherPortalService;
    public TeacherPortalController(TeacherPortalService teacherPortalService) { this.teacherPortalService = teacherPortalService; }

    @GetMapping("/me")
    public TeacherProfileResponse getMe(@AuthenticationPrincipal UserDetails user) {
        return teacherPortalService.getMyProfile(user.getUsername());
    }

    @GetMapping("/batches")
    public List<BatchResponse> getMyBatches(@AuthenticationPrincipal UserDetails user) {
        return teacherPortalService.getMyBatches(user.getUsername());
    }

    @GetMapping("/batches/{batchId}/students")
    public List<TeacherStudentResponse> getStudents(@AuthenticationPrincipal UserDetails user, @PathVariable Long batchId) {
        return teacherPortalService.getStudentsInBatch(user.getUsername(), batchId);
    }

    @GetMapping("/batches/{batchId}/attendance")
    public List<BatchAttendanceResponse> getAttendance(@AuthenticationPrincipal UserDetails user,
            @PathVariable Long batchId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate attendanceDate) {
        return teacherPortalService.getBatchAttendance(user.getUsername(), batchId, attendanceDate);
    }

    @PostMapping("/attendance/batch")
    public ResponseEntity<List<AttendanceResponse>> saveAttendance(@AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody BatchAttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherPortalService.saveBatchAttendance(user.getUsername(), request));
    }

    @GetMapping("/batches/{batchId}/attendance/dashboard")
    public BatchAttendanceDashboardResponse getAttendanceDashboard(@AuthenticationPrincipal UserDetails user,
            @PathVariable Long batchId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return teacherPortalService.getAttendanceDashboard(user.getUsername(), batchId, startDate, endDate);
    }

    @GetMapping("/batches/{batchId}/attendance/report")
    public List<AttendanceReportResponse> getAttendanceReport(@AuthenticationPrincipal UserDetails user,
            @PathVariable Long batchId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return teacherPortalService.getAttendanceReport(user.getUsername(), batchId, startDate, endDate);
    }

    @GetMapping("/batches/{batchId}/attendance/low-attendance")
    public List<LowAttendanceStudentResponse> getLowAttendance(@AuthenticationPrincipal UserDetails user,
            @PathVariable Long batchId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "75") double threshold) {
        return teacherPortalService.getLowAttendance(user.getUsername(), batchId, startDate, endDate, threshold);
    }

    @GetMapping("/batches/{batchId}/attendance/daily-report")
    public List<DailyAttendanceReportResponse> getDailyAttendanceReport(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long batchId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return teacherPortalService.getDailyAttendanceReport(
                user.getUsername(),
                batchId,
                startDate,
                endDate
        );
    }
}

package com.inturn.suncomputer.attendance.controller;

import com.inturn.suncomputer.attendance.dto.*;
import com.inturn.suncomputer.attendance.service.AttendanceService;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;


    public AttendanceController(
            AttendanceService attendanceService
    ) {

        this.attendanceService =
                attendanceService;
    }







    // =========================================================
// STUDENT ATTENDANCE DASHBOARD
// =========================================================

    @GetMapping("/enrollment/{enrollmentId}/dashboard")
    public ResponseEntity<StudentAttendanceDashboardResponse>
    getStudentAttendanceDashboard(

            @PathVariable
            Long enrollmentId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate

    ) {

        return ResponseEntity.ok(
                attendanceService
                        .getStudentAttendanceDashboard(
                                enrollmentId,
                                startDate,
                                endDate
                        )
        );
    }


    // =========================================================
// BATCH ATTENDANCE DASHBOARD
// =========================================================

    @GetMapping("/batch/{batchId}/dashboard")
    public ResponseEntity<BatchAttendanceDashboardResponse>
    getBatchAttendanceDashboard(

            @PathVariable
            Long batchId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate

    ) {

        return ResponseEntity.ok(
                attendanceService
                        .getBatchAttendanceDashboard(
                                batchId,
                                startDate,
                                endDate
                        )
        );
    }




    // =========================================================
// LOW ATTENDANCE STUDENTS
// =========================================================

    @GetMapping("/batch/{batchId}/low-attendance")
    public ResponseEntity<List<LowAttendanceStudentResponse>>
    getLowAttendanceStudents(

            @PathVariable
            Long batchId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate,

            @RequestParam(
                    defaultValue = "75"
            )
            double threshold

    ) {

        return ResponseEntity.ok(
                attendanceService
                        .getLowAttendanceStudents(
                                batchId,
                                startDate,
                                endDate,
                                threshold
                        )
        );
    }




    // =========================================================
// BATCH ATTENDANCE REPORT
// =========================================================

    @GetMapping("/batch/{batchId}/report")
    public ResponseEntity<List<AttendanceReportResponse>>
    getBatchAttendanceReport(

            @PathVariable
            Long batchId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate

    ) {

        return ResponseEntity.ok(
                attendanceService
                        .getBatchAttendanceReport(
                                batchId,
                                startDate,
                                endDate
                        )
        );
    }




    // =========================================================
// DAILY ATTENDANCE REPORT
// =========================================================

    @GetMapping("/batch/{batchId}/daily-report")
    public ResponseEntity<List<DailyAttendanceReportResponse>>
    getDailyAttendanceReport(

            @PathVariable
            Long batchId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate

    ) {

        return ResponseEntity.ok(
                attendanceService
                        .getDailyAttendanceReport(
                                batchId,
                                startDate,
                                endDate
                        )
        );
    }





    // =========================================================
    // CREATE ATTENDANCE
    // =========================================================

    @PostMapping
    public ResponseEntity<AttendanceResponse>
    createAttendance(
            @Valid
            @RequestBody
            CreateAttendanceRequest request
    ) {

        AttendanceResponse response =
                attendanceService
                        .createAttendance(
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET ATTENDANCE BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse>
    getAttendanceById(
            @PathVariable Long id
    ) {

        AttendanceResponse response =
                attendanceService
                        .getAttendanceById(
                                id
                        );

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // GET ATTENDANCE BY ENROLLMENT
    // =========================================================

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<List<AttendanceResponse>>
    getAttendanceByEnrollment(
            @PathVariable Long enrollmentId
    ) {

        List<AttendanceResponse> response =
                attendanceService
                        .getAttendanceByEnrollment(
                                enrollmentId
                        );

        return ResponseEntity.ok(
                response
        );
    }


    // =========================================================
    // GET ATTENDANCE BETWEEN DATES
    // =========================================================

    @GetMapping("/enrollment/{enrollmentId}/range")
    public ResponseEntity<List<AttendanceResponse>>
    getAttendanceBetweenDates(

            @PathVariable
            Long enrollmentId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate

    ) {

        List<AttendanceResponse> response =
                attendanceService
                        .getAttendanceBetweenDates(
                                enrollmentId,
                                startDate,
                                endDate
                        );

        return ResponseEntity.ok(
                response
        );
    }

    // =========================================================
    // BATCH ATTENDANCE
    // =========================================================

        @PostMapping("/batch")
        public ResponseEntity<List<AttendanceResponse>>
        createBatchAttendance(
                @Valid
                @RequestBody
                BatchAttendanceRequest request
        ) {

            List<AttendanceResponse> response =
                    attendanceService
                            .createBatchAttendance(
                                    request
                            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }

    // =========================================================
    // ATTENDANCE SUMMARY
    // =========================================================

    @GetMapping("/enrollment/{enrollmentId}/summary")
    public ResponseEntity<AttendanceSummaryResponse>
    getAttendanceSummary(

            @PathVariable
            Long enrollmentId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate endDate

    ) {

        AttendanceSummaryResponse response =
                attendanceService
                        .getAttendanceSummary(
                                enrollmentId,
                                startDate,
                                endDate
                        );

        return ResponseEntity.ok(
                response
        );
    }
    // =========================================================
// GET BATCH ATTENDANCE SHEET
// =========================================================

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<BatchAttendanceResponse>>
    getBatchAttendance(

            @PathVariable
            Long batchId,

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate attendanceDate

    ) {

        List<BatchAttendanceResponse> response =
                attendanceService
                        .getBatchAttendance(
                                batchId,
                                attendanceDate
                        );

        return ResponseEntity.ok(
                response
        );
    }

}

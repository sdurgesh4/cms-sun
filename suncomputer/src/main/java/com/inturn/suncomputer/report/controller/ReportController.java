package com.inturn.suncomputer.report.controller;

import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;

import com.inturn.suncomputer.report.dto.BatchStudentReportResponse;
import com.inturn.suncomputer.report.dto.EnquiryReportResponse;
import com.inturn.suncomputer.report.dto.FeeCollectionReportResponse;
import com.inturn.suncomputer.report.dto.PendingFeeReportResponse;
import com.inturn.suncomputer.report.dto.StudentReportResponse;

import com.inturn.suncomputer.report.service.ReportService;

import com.inturn.suncomputer.student.entity.StudentStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {


    private final ReportService reportService;


    // =========================================================
    // STUDENT REPORT
    // =========================================================

    @GetMapping("/students")
    public ResponseEntity<List<StudentReportResponse>>
    getStudentReport(

            @RequestParam(
                    required = false
            )
            StudentStatus status

    ) {

        return ResponseEntity.ok(
                reportService.getStudentReport(
                        status
                )
        );
    }


    // =========================================================
    // BATCH STUDENT REPORT
    // =========================================================

    @GetMapping("/batches/{batchId}/students")
    public ResponseEntity<
            List<BatchStudentReportResponse>
            >
    getBatchStudentReport(

            @PathVariable
            Long batchId

    ) {

        return ResponseEntity.ok(
                reportService
                        .getBatchStudentReport(
                                batchId
                        )
        );
    }


    // =========================================================
    // FEE COLLECTION REPORT
    // =========================================================

    @GetMapping("/fees/collection")
    public ResponseEntity<
            List<FeeCollectionReportResponse>
            >
    getFeeCollectionReport(

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
                reportService
                        .getFeeCollectionReport(
                                startDate,
                                endDate
                        )
        );
    }


    // =========================================================
    // PENDING FEE REPORT
    // =========================================================

    @GetMapping("/fees/pending")
    public ResponseEntity<
            List<PendingFeeReportResponse>
            >
    getPendingFeeReport() {

        return ResponseEntity.ok(
                reportService
                        .getPendingFeeReport()
        );
    }


    // =========================================================
    // ENQUIRY REPORT
    // =========================================================

    @GetMapping("/enquiries")
    public ResponseEntity<
            List<EnquiryReportResponse>
            >
    getEnquiryReport(

            @RequestParam(
                    required = false
            )
            EnquiryStatus status

    ) {

        return ResponseEntity.ok(
                reportService
                        .getEnquiryReport(
                                status
                        )
        );
    }
}
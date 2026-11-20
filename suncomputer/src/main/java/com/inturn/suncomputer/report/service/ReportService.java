package com.inturn.suncomputer.report.service;

import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;
import com.inturn.suncomputer.report.dto.BatchStudentReportResponse;
import com.inturn.suncomputer.report.dto.EnquiryReportResponse;
import com.inturn.suncomputer.report.dto.FeeCollectionReportResponse;
import com.inturn.suncomputer.report.dto.PendingFeeReportResponse;
import com.inturn.suncomputer.report.dto.StudentReportResponse;
import com.inturn.suncomputer.student.entity.StudentStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<StudentReportResponse> getStudentReport(
            StudentStatus status
    );

    List<BatchStudentReportResponse> getBatchStudentReport(
            Long batchId
    );

    List<FeeCollectionReportResponse> getFeeCollectionReport(
            LocalDate startDate,
            LocalDate endDate
    );

    List<PendingFeeReportResponse> getPendingFeeReport();

    List<EnquiryReportResponse> getEnquiryReport(
            EnquiryStatus status
    );
}
package com.inturn.suncomputer.report.service;

import com.inturn.suncomputer.common.exception.ResourceNotFoundException;

import com.inturn.suncomputer.enquiry.entity.Enquiry;
import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;
import com.inturn.suncomputer.enquiry.repository.EnquiryRepository;

import com.inturn.suncomputer.report.dto.BatchStudentReportResponse;
import com.inturn.suncomputer.report.dto.EnquiryReportResponse;
import com.inturn.suncomputer.report.dto.FeeCollectionReportResponse;
import com.inturn.suncomputer.report.dto.PendingFeeReportResponse;
import com.inturn.suncomputer.report.dto.StudentReportResponse;
import com.inturn.suncomputer.report.repository.ReportRepository;

import com.inturn.suncomputer.student.entity.StudentStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl
        implements ReportService {

    private final ReportRepository reportRepository;

    private final EnquiryRepository enquiryRepository;


    public ReportServiceImpl(
            ReportRepository reportRepository,
            EnquiryRepository enquiryRepository
    ) {

        this.reportRepository =
                reportRepository;

        this.enquiryRepository =
                enquiryRepository;
    }


    @Override
    public List<StudentReportResponse>
    getStudentReport(
            StudentStatus status
    ) {

        return reportRepository
                .getStudentReport(status);
    }


    @Override
    public List<BatchStudentReportResponse>
    getBatchStudentReport(
            Long batchId
    ) {

        return reportRepository
                .getBatchStudentReport(
                        batchId
                );
    }


    @Override
    public List<FeeCollectionReportResponse>
    getFeeCollectionReport(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (
                startDate == null
                        || endDate == null
        ) {

            throw new IllegalArgumentException(
                    "Start date and end date are required"
            );
        }


        if (
                endDate.isBefore(startDate)
        ) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }


        return reportRepository
                .getFeeCollectionReport(
                        startDate,
                        endDate
                );
    }


    @Override
    public List<PendingFeeReportResponse>
    getPendingFeeReport() {

        return reportRepository
                .getPendingFeeReport();
    }


    @Override
    public List<EnquiryReportResponse>
    getEnquiryReport(
            EnquiryStatus status
    ) {

        List<Enquiry> enquiries;


        if (status != null) {

            enquiries =
                    enquiryRepository
                            .findByStatusOrderByCreatedAtDesc(
                                    status
                            );

        } else {

            enquiries =
                    enquiryRepository
                            .findAll(
                                    org.springframework.data.domain.Sort
                                            .by(
                                                    org.springframework.data.domain.Sort.Direction.DESC,
                                                    "createdAt"
                                            )
                            );
        }


        return enquiries
                .stream()
                .map(this::mapEnquiry)
                .toList();
    }


    private EnquiryReportResponse mapEnquiry(
            Enquiry enquiry
    ) {

        return new EnquiryReportResponse(

                enquiry.getId(),

                enquiry.getFullName(),

                enquiry.getMobile(),

                enquiry.getEmail(),

                enquiry.getInterestedCourse(),

                enquiry.getStatus(),

                enquiry.getSource(),

                enquiry.getNextFollowUpDate(),

                enquiry.getConvertedStudentId(),

                enquiry.getCreatedAt()
        );
    }
}
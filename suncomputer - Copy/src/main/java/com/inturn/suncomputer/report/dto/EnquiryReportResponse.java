package com.inturn.suncomputer.report.dto;

import com.inturn.suncomputer.enquiry.entity.EnquirySource;
import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EnquiryReportResponse(

        Long enquiryId,

        String fullName,

        String mobile,

        String email,

        String interestedCourse,

        EnquiryStatus status,

        EnquirySource source,

        LocalDate nextFollowUpDate,

        Long convertedStudentId,

        LocalDateTime createdAt

) {
}
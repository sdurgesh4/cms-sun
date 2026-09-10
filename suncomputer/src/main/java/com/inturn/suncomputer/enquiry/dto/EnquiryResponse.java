package com.inturn.suncomputer.enquiry.dto;

import com.inturn.suncomputer.enquiry.entity.EnquirySource;
import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EnquiryResponse(

        Long id,

        String fullName,

        String mobile,

        String email,

        String address,

        String interestedCourse,

        EnquiryStatus status,

        EnquirySource source,

        LocalDate nextFollowUpDate,

        String remarks,

        Long assignedTo,

        Long convertedStudentId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
package com.inturn.suncomputer.enquiry.dto;

import com.inturn.suncomputer.enquiry.entity.FollowUpType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FollowUpResponse(

        Long id,

        Long enquiryId,

        FollowUpType followUpType,

        LocalDate followUpDate,

        String notes,

        LocalDate nextFollowUpDate,

        Long createdBy,

        LocalDateTime createdAt

) {
}
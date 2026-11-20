package com.inturn.suncomputer.enquiry.dto;

import com.inturn.suncomputer.enquiry.entity.FollowUpType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateFollowUpRequest(

        @NotNull(
                message = "Follow-up type is required"
        )
        FollowUpType followUpType,


        @NotNull(
                message = "Follow-up date is required"
        )
        LocalDate followUpDate,


        @Size(
                max = 2000,
                message = "Notes cannot exceed 2000 characters"
        )
        String notes,


        LocalDate nextFollowUpDate,


        Long createdBy

) {
}
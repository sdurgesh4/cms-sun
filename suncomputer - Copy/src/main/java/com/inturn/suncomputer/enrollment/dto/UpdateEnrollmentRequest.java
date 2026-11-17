package com.inturn.suncomputer.enrollment.dto;

import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;

import jakarta.validation.constraints.Size;

public record UpdateEnrollmentRequest(

        EnrollmentStatus status,

        @Size(
                max = 1000,
                message = "Notes cannot exceed 1000 characters"
        )
        String notes

) {
}
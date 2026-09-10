package com.inturn.suncomputer.enquiry.dto;

import com.inturn.suncomputer.enquiry.entity.EnquirySource;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateEnquiryRequest(

        @NotBlank(
                message = "Name is required"
        )
        @Size(
                max = 100,
                message = "Name cannot exceed 100 characters"
        )
        String fullName,


        @NotBlank(
                message = "Mobile number is required"
        )
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Mobile number must contain 10 digits"
        )
        String mobile,


        @Email(
                message = "Invalid email address"
        )
        @Size(max = 150)
        String email,


        @Size(max = 255)
        String address,


        @Size(max = 150)
        String interestedCourse,


        EnquirySource source,


        LocalDate nextFollowUpDate,


        @Size(max = 2000)
        String remarks,


        Long assignedTo

) {
}
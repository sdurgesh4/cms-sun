package com.inturn.suncomputer.student.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateStudentRequest(

        @NotNull(
                message = "User ID is required"
        )
        Long userId,

        @NotBlank(
                message = "Student code is required"
        )
        @Size(
                max = 50,
                message = "Student code cannot exceed 50 characters"
        )
        String studentCode,

        @Past(
                message = "Date of birth must be in the past"
        )
        LocalDate dateOfBirth,

        @Size(max = 20)
        String gender,

        @Size(max = 500)
        String address,

        @Size(max = 100)
        String city,

        @Size(max = 100)
        String state,

        @Size(max = 10)
        String pincode,

        @Size(max = 200)
        String parentName,

        @Size(max = 20)
        String parentPhone,

        @NotNull(
                message = "Admission date is required"
        )
        LocalDate admissionDate

) {
}
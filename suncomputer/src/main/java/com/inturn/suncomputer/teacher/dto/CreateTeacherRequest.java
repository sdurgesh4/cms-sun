package com.inturn.suncomputer.teacher.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateTeacherRequest(

        @NotNull(
                message = "User ID is required"
        )
        Long userId,

        @NotBlank(
                message = "Employee code is required"
        )
        @Size(max = 50)
        String employeeCode,

        @Size(max = 200)
        String specialization,

        @Size(max = 200)
        String qualification,

        @Min(
                value = 0,
                message = "Experience cannot be negative"
        )
        Integer experienceYears,

        LocalDate joiningDate

) {
}
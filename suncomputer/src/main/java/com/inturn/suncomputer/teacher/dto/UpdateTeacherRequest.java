package com.inturn.suncomputer.teacher.dto;

import com.inturn.suncomputer.teacher.entity.TeacherStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTeacherRequest(

        @Size(max = 200)
        String specialization,

        @Size(max = 200)
        String qualification,

        @Min(
                value = 0,
                message = "Experience cannot be negative"
        )
        Integer experienceYears,

        LocalDate joiningDate,

        TeacherStatus status

) {
}
package com.inturn.suncomputer.course.dto;

import com.inturn.suncomputer.course.entity.CourseLevel;
import com.inturn.suncomputer.course.entity.DurationUnit;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateCourseRequest(

        @NotBlank(
                message = "Course code is required"
        )
        @Size(
                max = 50,
                message = "Course code cannot exceed 50 characters"
        )
        String code,

        @NotBlank(
                message = "Course name is required"
        )
        @Size(
                max = 150,
                message = "Course name cannot exceed 150 characters"
        )
        String name,

        String description,

        @NotNull(
                message = "Duration is required"
        )
        @Positive(
                message = "Duration must be greater than zero"
        )
        Integer duration,

        @NotNull(
                message = "Duration unit is required"
        )
        DurationUnit durationUnit,

        @NotNull(
                message = "Course fee is required"
        )
        @DecimalMin(
                value = "0.0",
                inclusive = true,
                message = "Fee cannot be negative"
        )
        BigDecimal fee,

        @NotNull(
                message = "Course level is required"
        )
        CourseLevel level

) {
}
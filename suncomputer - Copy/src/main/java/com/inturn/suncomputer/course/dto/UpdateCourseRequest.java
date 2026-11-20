package com.inturn.suncomputer.course.dto;

import com.inturn.suncomputer.course.entity.CourseLevel;
import com.inturn.suncomputer.course.entity.DurationUnit;
import com.inturn.suncomputer.course.entity.CourseStatus;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateCourseRequest(

        @Size(max = 150)
        String name,

        String description,

        @Positive
        Integer duration,

        DurationUnit durationUnit,

        @DecimalMin(
                value = "0.0",
                inclusive = true
        )
        BigDecimal fee,

        CourseLevel level,

        CourseStatus status

) {
}
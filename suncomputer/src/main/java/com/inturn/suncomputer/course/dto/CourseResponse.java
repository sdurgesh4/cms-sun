package com.inturn.suncomputer.course.dto;

import com.inturn.suncomputer.course.entity.CourseLevel;
import com.inturn.suncomputer.course.entity.CourseStatus;
import com.inturn.suncomputer.course.entity.DurationUnit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseResponse(

        Long id,

        String code,

        String name,

        String description,

        Integer duration,

        DurationUnit durationUnit,

        BigDecimal fee,

        CourseLevel level,

        CourseStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
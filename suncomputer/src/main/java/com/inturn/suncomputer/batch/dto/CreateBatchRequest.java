package com.inturn.suncomputer.batch.dto;

import com.inturn.suncomputer.batch.entity.BatchDay;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record CreateBatchRequest(

        @NotBlank(
                message = "Batch code is required"
        )
        @Size(max = 50)
        String batchCode,

        @NotNull(
                message = "Course ID is required"
        )
        Long courseId,

        @NotNull(
                message = "Teacher ID is required"
        )
        Long teacherId,

        @NotNull(
                message = "Start date is required"
        )
        LocalDate startDate,

        LocalDate endDate,

        @NotNull(
                message = "Start time is required"
        )
        LocalTime startTime,

        @NotNull(
                message = "End time is required"
        )
        LocalTime endTime,

        @NotEmpty(
                message = "At least one batch day is required"
        )
        Set<BatchDay> days,

        @Size(max = 100)
        String room,

        @NotNull(
                message = "Capacity is required"
        )
        @Min(
                value = 1,
                message = "Capacity must be at least 1"
        )
        Integer capacity

) {
}
package com.inturn.suncomputer.batch.dto;

import com.inturn.suncomputer.batch.entity.BatchDay;
import com.inturn.suncomputer.batch.entity.BatchStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record UpdateBatchRequest(

        LocalDate startDate,

        LocalDate endDate,

        LocalTime startTime,

        LocalTime endTime,

        Set<BatchDay> days,

        @Size(max = 100)
        String room,

        @Min(1)
        Integer capacity,

        BatchStatus status

) {
}
package com.inturn.suncomputer.batch.dto;

import com.inturn.suncomputer.batch.entity.BatchDay;
import com.inturn.suncomputer.batch.entity.BatchStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record BatchResponse(

        Long id,

        String batchCode,

        Long courseId,

        String courseCode,

        String courseName,

        Long teacherId,

        String employeeCode,

        String teacherName,

        LocalDate startDate,

        LocalDate endDate,

        LocalTime startTime,

        LocalTime endTime,

        Set<BatchDay> days,

        String room,

        Integer capacity,

        BatchStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
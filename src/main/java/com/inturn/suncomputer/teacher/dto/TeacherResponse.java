package com.inturn.suncomputer.teacher.dto;

import com.inturn.suncomputer.teacher.entity.TeacherStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TeacherResponse(

        Long id,

        Long userId,

        String username,

        String firstName,

        String lastName,

        String email,

        String phone,

        String employeeCode,

        String specialization,

        String qualification,

        Integer experienceYears,

        LocalDate joiningDate,

        TeacherStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
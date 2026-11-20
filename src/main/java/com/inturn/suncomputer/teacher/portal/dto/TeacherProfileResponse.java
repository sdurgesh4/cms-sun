package com.inturn.suncomputer.teacher.portal.dto;

import com.inturn.suncomputer.teacher.entity.TeacherStatus;
import java.time.LocalDate;

public record TeacherProfileResponse(
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
        TeacherStatus status
) {}

package com.inturn.suncomputer.student.dto;

import com.inturn.suncomputer.student.entity.StudentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentResponse(

        Long id,

        Long userId,

        String username,

        String firstName,

        String lastName,

        String email,

        String phone,

        String studentCode,

        LocalDate dateOfBirth,

        String gender,

        String address,

        String city,

        String state,

        String pincode,

        String parentName,

        String parentPhone,

        LocalDate admissionDate,

        StudentStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
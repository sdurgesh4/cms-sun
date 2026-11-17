package com.inturn.suncomputer.student.dto;

import com.inturn.suncomputer.student.entity.StudentStatus;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateStudentRequest(

        @Past(
                message = "Date of birth must be in the past"
        )
        LocalDate dateOfBirth,

        @Size(max = 20)
        String gender,

        @Size(max = 500)
        String address,

        @Size(max = 100)
        String city,

        @Size(max = 100)
        String state,

        @Size(max = 10)
        String pincode,

        @Size(max = 200)
        String parentName,

        @Size(max = 20)
        String parentPhone,

        StudentStatus status

) {
}
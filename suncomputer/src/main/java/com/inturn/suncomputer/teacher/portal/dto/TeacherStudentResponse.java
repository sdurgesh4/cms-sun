package com.inturn.suncomputer.teacher.portal.dto;

import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;
import com.inturn.suncomputer.student.entity.StudentStatus;
import java.time.LocalDate;

public record TeacherStudentResponse(
        Long enrollmentId,
        Long studentId,
        String studentCode,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        EnrollmentStatus enrollmentStatus,
        StudentStatus studentStatus,
        LocalDate enrollmentDate
) {}

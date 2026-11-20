package com.inturn.suncomputer.enrollment.service;

import com.inturn.suncomputer.enrollment.dto.CreateEnrollmentRequest;
import com.inturn.suncomputer.enrollment.dto.EnrollmentResponse;
import com.inturn.suncomputer.enrollment.dto.UpdateEnrollmentRequest;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse createEnrollment(
            CreateEnrollmentRequest request
    );

    EnrollmentResponse getEnrollmentById(
            Long id
    );

    List<EnrollmentResponse> getAllEnrollments();

    List<EnrollmentResponse> getEnrollmentsByStudent(
            Long studentId
    );

    List<EnrollmentResponse> getEnrollmentsByBatch(
            Long batchId
    );

    EnrollmentResponse updateEnrollment(
            Long id,
            UpdateEnrollmentRequest request
    );

    void cancelEnrollment(
            Long id
    );
}
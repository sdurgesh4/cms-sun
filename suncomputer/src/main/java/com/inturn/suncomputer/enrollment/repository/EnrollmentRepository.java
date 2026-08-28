package com.inturn.suncomputer.enrollment.repository;

import com.inturn.suncomputer.enrollment.entity.Enrollment;
import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(
            Long studentId
    );

    List<Enrollment> findByBatchId(
            Long batchId
    );

    List<Enrollment> findByStatus(
            EnrollmentStatus status
    );

    List<Enrollment> findByStudentIdAndStatus(
            Long studentId,
            EnrollmentStatus status
    );

    List<Enrollment> findByBatchIdAndStatus(
            Long batchId,
            EnrollmentStatus status
    );

    long countByBatchIdAndStatus(
            Long batchId,
            EnrollmentStatus status
    );

}
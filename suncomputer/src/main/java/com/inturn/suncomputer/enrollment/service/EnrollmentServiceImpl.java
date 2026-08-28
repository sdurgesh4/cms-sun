package com.inturn.suncomputer.enrollment.service;

import com.inturn.suncomputer.batch.entity.Batch;
import com.inturn.suncomputer.batch.entity.BatchStatus;

import com.inturn.suncomputer.batch.repository.BatchRepository;

import com.inturn.suncomputer.common.exception.BusinessRuleException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;

import com.inturn.suncomputer.enrollment.dto.CreateEnrollmentRequest;
import com.inturn.suncomputer.enrollment.dto.EnrollmentResponse;
import com.inturn.suncomputer.enrollment.dto.UpdateEnrollmentRequest;

import com.inturn.suncomputer.enrollment.entity.Enrollment;
import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;

import com.inturn.suncomputer.enrollment.repository.EnrollmentRepository;

import com.inturn.suncomputer.student.entity.Student;
import com.inturn.suncomputer.student.entity.StudentStatus;

import com.inturn.suncomputer.student.repository.StudentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class EnrollmentServiceImpl
        implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final StudentRepository studentRepository;

    private final BatchRepository batchRepository;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            StudentRepository studentRepository,
            BatchRepository batchRepository
    ) {

        this.enrollmentRepository =
                enrollmentRepository;

        this.studentRepository =
                studentRepository;

        this.batchRepository =
                batchRepository;
    }

    @Override
    public EnrollmentResponse createEnrollment(
            CreateEnrollmentRequest request
    ) {

        validateFees(
                request.agreedFee(),
                request.discount()
        );

        Student student =
                studentRepository
                        .findById(request.studentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found with id: "
                                                + request.studentId()
                                )
                        );

        if (
                student.getStatus()
                        != StudentStatus.ACTIVE
        ) {

            throw new BusinessRuleException(
                    "Only active students can be enrolled"
            );
        }

        Batch batch =
                batchRepository
                        .findById(request.batchId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Batch not found with id: "
                                                + request.batchId()
                                )
                        );

        if (
                batch.getStatus()
                        == BatchStatus.CANCELLED
        ) {

            throw new BusinessRuleException(
                    "Cannot enroll student in a cancelled batch"
            );
        }

        if (
                batch.getStatus()
                        == BatchStatus.COMPLETED
        ) {

            throw new BusinessRuleException(
                    "Cannot enroll student in a completed batch"
            );
        }

        if (
                request.enrollmentDate()
                        .isBefore(
                                batch.getStartDate()
                        )
        ) {

            throw new BusinessRuleException(
                    "Enrollment date cannot be before batch start date"
            );
        }

        long activeEnrollments =
                enrollmentRepository
                        .countByBatchIdAndStatus(
                                batch.getId(),
                                EnrollmentStatus.ACTIVE
                        );

        if (
                activeEnrollments
                        >= batch.getCapacity()
        ) {

            throw new BusinessRuleException(
                    "Batch is full. Capacity: "
                            + batch.getCapacity()
            );
        }

        boolean alreadyEnrolled =
                enrollmentRepository
                        .findByStudentIdAndStatus(
                                student.getId(),
                                EnrollmentStatus.ACTIVE
                        )
                        .stream()
                        .anyMatch(
                                enrollment ->
                                        enrollment
                                                .getBatch()
                                                .getId()
                                                .equals(
                                                        batch.getId()
                                                )
                        );

        if (alreadyEnrolled) {

            throw new BusinessRuleException(
                    "Student is already actively enrolled in this batch"
            );
        }

        BigDecimal finalFee =
                request.agreedFee()
                        .subtract(
                                request.discount()
                        );

        Enrollment enrollment =
                new Enrollment();

        enrollment.setStudent(student);

        enrollment.setBatch(batch);

        enrollment.setEnrollmentDate(
                request.enrollmentDate()
        );

        enrollment.setAgreedFee(
                request.agreedFee()
        );

        enrollment.setDiscount(
                request.discount()
        );

        enrollment.setFinalFee(
                finalFee
        );

        enrollment.setStatus(
                EnrollmentStatus.ACTIVE
        );

        enrollment.setNotes(
                request.notes()
        );

        Enrollment saved =
                enrollmentRepository.save(
                        enrollment
                );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(
            Long id
    ) {

        return mapToResponse(
                findEnrollment(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse>
    getAllEnrollments() {

        return enrollmentRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse>
    getEnrollmentsByStudent(
            Long studentId
    ) {

        return enrollmentRepository
                .findByStudentId(studentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse>
    getEnrollmentsByBatch(
            Long batchId
    ) {

        return enrollmentRepository
                .findByBatchId(batchId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EnrollmentResponse updateEnrollment(
            Long id,
            UpdateEnrollmentRequest request
    ) {

        Enrollment enrollment =
                findEnrollment(id);

        if (request.status() != null) {

            validateStatusChange(
                    enrollment.getStatus(),
                    request.status()
            );

            enrollment.setStatus(
                    request.status()
            );
        }

        if (request.notes() != null) {

            enrollment.setNotes(
                    request.notes()
            );
        }

        return mapToResponse(enrollment);
    }

    @Override
    public void cancelEnrollment(
            Long id
    ) {

        Enrollment enrollment =
                findEnrollment(id);

        if (
                enrollment.getStatus()
                        != EnrollmentStatus.ACTIVE
        ) {

            throw new BusinessRuleException(
                    "Only active enrollments can be cancelled"
            );
        }

        enrollment.setStatus(
                EnrollmentStatus.CANCELLED
        );
    }

    private Enrollment findEnrollment(
            Long id
    ) {

        return enrollmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Enrollment not found with id: "
                                        + id
                        )
                );
    }

    private void validateFees(
            BigDecimal agreedFee,
            BigDecimal discount
    ) {

        if (
                agreedFee == null
                        ||
                        agreedFee.compareTo(
                                BigDecimal.ZERO
                        ) < 0
        ) {

            throw new BusinessRuleException(
                    "Agreed fee cannot be negative"
            );
        }

        if (
                discount == null
                        ||
                        discount.compareTo(
                                BigDecimal.ZERO
                        ) < 0
        ) {

            throw new BusinessRuleException(
                    "Discount cannot be negative"
            );
        }

        if (
                discount.compareTo(
                        agreedFee
                ) > 0
        ) {

            throw new BusinessRuleException(
                    "Discount cannot be greater than agreed fee"
            );
        }
    }

    private void validateStatusChange(
            EnrollmentStatus currentStatus,
            EnrollmentStatus newStatus
    ) {

        if (
                currentStatus
                        == EnrollmentStatus.CANCELLED
                        &&
                        newStatus
                                != EnrollmentStatus.CANCELLED
        ) {

            throw new BusinessRuleException(
                    "Cancelled enrollment cannot be reactivated"
            );
        }

        if (
                currentStatus
                        == EnrollmentStatus.COMPLETED
                        &&
                        newStatus
                                != EnrollmentStatus.COMPLETED
        ) {

            throw new BusinessRuleException(
                    "Completed enrollment cannot change status"
            );
        }
    }

    private EnrollmentResponse mapToResponse(
            Enrollment enrollment
    ) {

        Student student =
                enrollment.getStudent();

        Batch batch =
                enrollment.getBatch();

        var user =
                student.getUser();

        var course =
                batch.getCourse();

        var teacher =
                batch.getTeacher();

        String studentName =
                user.getFirstName()
                        + " "
                        + user.getLastName();

        String teacherName =
                teacher.getUser().getFirstName()
                        + " "
                        + teacher.getUser().getLastName();

        return new EnrollmentResponse(

                enrollment.getId(),

                student.getId(),

                student.getStudentCode(),

                studentName,

                batch.getId(),

                batch.getBatchCode(),

                course.getId(),

                course.getCode(),

                course.getName(),

                teacher.getId(),

                teacherName,

                enrollment.getEnrollmentDate(),

                enrollment.getAgreedFee(),

                enrollment.getDiscount(),

                enrollment.getFinalFee(),

                enrollment.getStatus(),

                enrollment.getNotes(),

                enrollment.getCreatedAt(),

                enrollment.getUpdatedAt()
        );
    }
}
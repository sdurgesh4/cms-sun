package com.inturn.suncomputer.batch.service;

import com.inturn.suncomputer.batch.dto.BatchResponse;
import com.inturn.suncomputer.batch.dto.CreateBatchRequest;
import com.inturn.suncomputer.batch.dto.UpdateBatchRequest;

import com.inturn.suncomputer.batch.entity.Batch;
import com.inturn.suncomputer.batch.entity.BatchStatus;

import com.inturn.suncomputer.batch.repository.BatchRepository;

import com.inturn.suncomputer.common.exception.BusinessRuleException;
import com.inturn.suncomputer.common.exception.DuplicateResourceException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;

import com.inturn.suncomputer.course.entity.Course;
import com.inturn.suncomputer.course.entity.CourseStatus;
import com.inturn.suncomputer.course.repository.CourseRepository;

import com.inturn.suncomputer.teacher.entity.Teacher;
import com.inturn.suncomputer.teacher.entity.TeacherStatus;
import com.inturn.suncomputer.teacher.repository.TeacherRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class BatchServiceImpl
        implements BatchService {

    private final BatchRepository batchRepository;

    private final CourseRepository courseRepository;

    private final TeacherRepository teacherRepository;

    public BatchServiceImpl(
            BatchRepository batchRepository,
            CourseRepository courseRepository,
            TeacherRepository teacherRepository
    ) {

        this.batchRepository =
                batchRepository;

        this.courseRepository =
                courseRepository;

        this.teacherRepository =
                teacherRepository;
    }

    @Override
    public BatchResponse createBatch(
            CreateBatchRequest request
    ) {

        validateDatesAndTimes(request);

        if (
                batchRepository.existsByBatchCode(
                        request.batchCode()
                )
        ) {

            throw new DuplicateResourceException(
                    "Batch code already exists: "
                            + request.batchCode()
            );
        }

        Course course =
                courseRepository
                        .findById(request.courseId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Course not found with id: "
                                                + request.courseId()
                                )
                        );

        if (
                course.getStatus()
                        != CourseStatus.ACTIVE
        ) {

            throw new BusinessRuleException(
                    "Cannot create batch for inactive course"
            );
        }

        Teacher teacher =
                teacherRepository
                        .findById(request.teacherId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Teacher not found with id: "
                                                + request.teacherId()
                                )
                        );

        if (
                teacher.getStatus()
                        != TeacherStatus.ACTIVE
        ) {

            throw new BusinessRuleException(
                    "Cannot assign inactive teacher to batch"
            );
        }

        Batch batch =
                new Batch();

        batch.setBatchCode(
                request.batchCode()
                        .trim()
                        .toUpperCase()
        );

        batch.setCourse(course);

        batch.setTeacher(teacher);

        batch.setStartDate(
                request.startDate()
        );

        batch.setEndDate(
                request.endDate()
        );

        batch.setStartTime(
                request.startTime()
        );

        batch.setEndTime(
                request.endTime()
        );

        batch.setDays(
                new HashSet<>(
                        request.days()
                )
        );

        batch.setRoom(
                request.room()
        );

        batch.setCapacity(
                request.capacity()
        );

        batch.setStatus(
                BatchStatus.PLANNED
        );

        Batch savedBatch =
                batchRepository.save(batch);

        return mapToResponse(savedBatch);
    }

    @Override
    @Transactional(readOnly = true)
    public BatchResponse getBatchById(
            Long id
    ) {

        return mapToResponse(
                findBatch(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponse> getAllBatches() {

        return batchRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponse> getBatchesByCourse(
            Long courseId
    ) {

        return batchRepository
                .findByCourseId(courseId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatchResponse> getBatchesByTeacher(
            Long teacherId
    ) {

        return batchRepository
                .findByTeacherId(teacherId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BatchResponse updateBatch(
            Long id,
            UpdateBatchRequest request
    ) {

        Batch batch =
                findBatch(id);

        if (request.startDate() != null) {

            batch.setStartDate(
                    request.startDate()
            );
        }

        if (request.endDate() != null) {

            batch.setEndDate(
                    request.endDate()
            );
        }

        if (request.startTime() != null) {

            batch.setStartTime(
                    request.startTime()
            );
        }

        if (request.endTime() != null) {

            batch.setEndTime(
                    request.endTime()
            );
        }

        if (request.days() != null) {

            batch.setDays(
                    new HashSet<>(
                            request.days()
                    )
            );
        }

        if (request.room() != null) {

            batch.setRoom(
                    request.room()
            );
        }

        if (request.capacity() != null) {

            batch.setCapacity(
                    request.capacity()
            );
        }

        if (request.status() != null) {

            batch.setStatus(
                    request.status()
            );
        }

        validateBatch(batch);

        return mapToResponse(batch);
    }

    @Override
    public void cancelBatch(
            Long id
    ) {

        Batch batch =
                findBatch(id);

        batch.setStatus(
                BatchStatus.CANCELLED
        );
    }

    private Batch findBatch(
            Long id
    ) {

        return batchRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch not found with id: "
                                        + id
                        )
                );
    }

    private void validateDatesAndTimes(
            CreateBatchRequest request
    ) {

        if (
                request.endDate() != null
                        &&
                        request.endDate()
                                .isBefore(
                                        request.startDate()
                                )
        ) {

            throw new BusinessRuleException(
                    "End date cannot be before start date"
            );
        }

        if (
                !request.endTime()
                        .isAfter(
                                request.startTime()
                        )
        ) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }
    }

    private void validateBatch(
            Batch batch
    ) {

        if (
                batch.getEndDate() != null
                        &&
                        batch.getEndDate()
                                .isBefore(
                                        batch.getStartDate()
                                )
        ) {

            throw new IllegalArgumentException(
                    "End date cannot be before start date"
            );
        }

        if (
                !batch.getEndTime()
                        .isAfter(
                                batch.getStartTime()
                        )
        ) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        if (
                batch.getCapacity() == null
                        ||
                        batch.getCapacity() < 1
        ) {

            throw new IllegalArgumentException(
                    "Batch capacity must be at least 1"
            );
        }
    }

    private BatchResponse mapToResponse(
            Batch batch
    ) {

        Course course =
                batch.getCourse();

        Teacher teacher =
                batch.getTeacher();

        String teacherName =
                teacher.getUser().getFirstName()
                        + " "
                        + teacher.getUser().getLastName();

        return new BatchResponse(

                batch.getId(),

                batch.getBatchCode(),

                course.getId(),

                course.getCode(),

                course.getName(),

                teacher.getId(),

                teacher.getEmployeeCode(),

                teacherName,

                batch.getStartDate(),

                batch.getEndDate(),

                batch.getStartTime(),

                batch.getEndTime(),

                batch.getDays(),

                batch.getRoom(),

                batch.getCapacity(),

                batch.getStatus(),

                batch.getCreatedAt(),

                batch.getUpdatedAt()
        );
    }
}
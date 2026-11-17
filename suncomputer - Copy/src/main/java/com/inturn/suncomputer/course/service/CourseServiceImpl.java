package com.inturn.suncomputer.course.service;

import com.inturn.suncomputer.common.exception.DuplicateResourceException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;

import com.inturn.suncomputer.course.dto.CourseResponse;
import com.inturn.suncomputer.course.dto.CreateCourseRequest;
import com.inturn.suncomputer.course.dto.UpdateCourseRequest;

import com.inturn.suncomputer.course.entity.Course;
import com.inturn.suncomputer.course.entity.CourseStatus;

import com.inturn.suncomputer.course.repository.CourseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl
        implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(
            CourseRepository courseRepository
    ) {

        this.courseRepository =
                courseRepository;
    }

    @Override
    public CourseResponse createCourse(
            CreateCourseRequest request
    ) {

        if (
                courseRepository.existsByCode(
                        request.code()
                )
        ) {

            throw new DuplicateResourceException(
                    "Course code already exists: "
                            + request.code()
            );
        }

        Course course =
                new Course();

        course.setCode(
                request.code()
                        .trim()
                        .toUpperCase()
        );

        course.setName(
                request.name().trim()
        );

        course.setDescription(
                request.description()
        );

        course.setDuration(
                request.duration()
        );

        course.setDurationUnit(
                request.durationUnit()
        );

        course.setFee(
                request.fee()
        );

        course.setLevel(
                request.level()
        );

        course.setStatus(
                CourseStatus.ACTIVE
        );

        Course savedCourse =
                courseRepository.save(course);

        return mapToResponse(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(
            Long id
    ) {

        Course course =
                findCourse(id);

        return mapToResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {

        return courseRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> searchCourses(
            String name
    ) {

        return courseRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CourseResponse updateCourse(
            Long id,
            UpdateCourseRequest request
    ) {

        Course course =
                findCourse(id);

        if (request.name() != null) {

            course.setName(
                    request.name().trim()
            );
        }

        if (request.description() != null) {

            course.setDescription(
                    request.description()
            );
        }

        if (request.duration() != null) {

            course.setDuration(
                    request.duration()
            );
        }

        if (request.durationUnit() != null) {

            course.setDurationUnit(
                    request.durationUnit()
            );
        }

        if (request.fee() != null) {

            course.setFee(
                    request.fee()
            );
        }

        if (request.level() != null) {

            course.setLevel(
                    request.level()
            );
        }

        if (request.status() != null) {

            course.setStatus(
                    request.status()
            );
        }

        return mapToResponse(course);
    }

    @Override
    public void deactivateCourse(
            Long id
    ) {

        Course course =
                findCourse(id);

        course.setStatus(
                CourseStatus.INACTIVE
        );
    }

    private Course findCourse(
            Long id
    ) {

        return courseRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found with id: "
                                        + id
                        )
                );
    }

    private CourseResponse mapToResponse(
            Course course
    ) {

        return new CourseResponse(

                course.getId(),

                course.getCode(),

                course.getName(),

                course.getDescription(),

                course.getDuration(),

                course.getDurationUnit(),

                course.getFee(),

                course.getLevel(),

                course.getStatus(),

                course.getCreatedAt(),

                course.getUpdatedAt()
        );
    }
}
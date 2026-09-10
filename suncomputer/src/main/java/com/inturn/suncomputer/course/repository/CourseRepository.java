package com.inturn.suncomputer.course.repository;

import com.inturn.suncomputer.course.entity.Course;
import com.inturn.suncomputer.course.entity.CourseStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);

    boolean existsByCode(String code);

    List<Course> findByStatus(
            CourseStatus status
    );

    List<Course> findByNameContainingIgnoreCase(
            String name
    );

    long countByStatus(
            CourseStatus status
    );
}
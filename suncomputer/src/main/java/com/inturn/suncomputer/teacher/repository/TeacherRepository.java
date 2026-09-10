package com.inturn.suncomputer.teacher.repository;

import com.inturn.suncomputer.teacher.entity.Teacher;
import com.inturn.suncomputer.teacher.entity.TeacherStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository
        extends JpaRepository<Teacher, Long> {

    Optional<Teacher> findByEmployeeCode(
            String employeeCode
    );

    Optional<Teacher> findByUserId(
            Long userId
    );

    boolean existsByEmployeeCode(
            String employeeCode
    );

    boolean existsByUserId(
            Long userId
    );

    List<Teacher> findByStatus(
            TeacherStatus status
    );

    long countByStatus(
            TeacherStatus status
    );
}
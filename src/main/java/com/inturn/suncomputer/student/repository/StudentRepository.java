package com.inturn.suncomputer.student.repository;

import com.inturn.suncomputer.student.entity.Student;
import com.inturn.suncomputer.student.entity.StudentStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository
        extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentCode(
            String studentCode
    );

    Optional<Student> findByUserId(
            Long userId
    );

    boolean existsByStudentCode(
            String studentCode
    );

    boolean existsByUserId(
            Long userId
    );

    List<Student> findByStatus(
            StudentStatus status
    );

    long countByStatus(
            StudentStatus status
    );
}
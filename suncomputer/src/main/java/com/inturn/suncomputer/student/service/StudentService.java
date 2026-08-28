package com.inturn.suncomputer.student.service;

import com.inturn.suncomputer.student.dto.CreateStudentRequest;
import com.inturn.suncomputer.student.dto.StudentResponse;
import com.inturn.suncomputer.student.dto.UpdateStudentRequest;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(
            CreateStudentRequest request
    );

    StudentResponse getStudentById(
            Long id
    );

    List<StudentResponse> getAllStudents();

    List<StudentResponse> getActiveStudents();

    StudentResponse updateStudent(
            Long id,
            UpdateStudentRequest request
    );

    void deactivateStudent(
            Long id
    );
}
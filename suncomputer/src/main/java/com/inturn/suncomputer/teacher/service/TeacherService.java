package com.inturn.suncomputer.teacher.service;

import com.inturn.suncomputer.teacher.dto.CreateTeacherRequest;
import com.inturn.suncomputer.teacher.dto.TeacherResponse;
import com.inturn.suncomputer.teacher.dto.UpdateTeacherRequest;

import java.util.List;

public interface TeacherService {

    TeacherResponse createTeacher(
            CreateTeacherRequest request
    );

    TeacherResponse getTeacherById(
            Long id
    );

    List<TeacherResponse> getAllTeachers();

    List<TeacherResponse> getActiveTeachers();

    TeacherResponse updateTeacher(
            Long id,
            UpdateTeacherRequest request
    );

    void deactivateTeacher(
            Long id
    );
}
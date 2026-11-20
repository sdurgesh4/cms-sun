package com.inturn.suncomputer.course.service;

import com.inturn.suncomputer.course.dto.CourseResponse;
import com.inturn.suncomputer.course.dto.CreateCourseRequest;
import com.inturn.suncomputer.course.dto.UpdateCourseRequest;

import java.util.List;

public interface CourseService {

    CourseResponse createCourse(
            CreateCourseRequest request
    );

    CourseResponse getCourseById(
            Long id
    );

    List<CourseResponse> getAllCourses();

    List<CourseResponse> searchCourses(
            String name
    );

    CourseResponse updateCourse(
            Long id,
            UpdateCourseRequest request
    );

    void deactivateCourse(
            Long id
    );
}
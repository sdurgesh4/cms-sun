package com.inturn.suncomputer.course.controller;

import com.inturn.suncomputer.course.dto.CourseResponse;
import com.inturn.suncomputer.course.dto.CreateCourseRequest;
import com.inturn.suncomputer.course.dto.UpdateCourseRequest;
import com.inturn.suncomputer.course.service.CourseService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(
            CourseService courseService
    ) {

        this.courseService =
                courseService;
    }

    @PostMapping
    public ResponseEntity<CourseResponse>
    createCourse(
            @Valid
            @RequestBody
            CreateCourseRequest request
    ) {

        CourseResponse response =
                courseService.createCourse(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>>
    getAllCourses(
            @RequestParam(
                    required = false
            )
            String search
    ) {

        if (
                search != null &&
                        !search.isBlank()
        ) {

            return ResponseEntity.ok(
                    courseService.searchCourses(
                            search
                    )
            );
        }

        return ResponseEntity.ok(
                courseService.getAllCourses()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse>
    getCourseById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                courseService.getCourseById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseResponse>
    updateCourse(
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateCourseRequest request
    ) {

        return ResponseEntity.ok(
                courseService.updateCourse(
                        id,
                        request
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void>
    deactivateCourse(
            @PathVariable Long id
    ) {

        courseService.deactivateCourse(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
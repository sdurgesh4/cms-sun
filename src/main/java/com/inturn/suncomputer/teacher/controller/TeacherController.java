package com.inturn.suncomputer.teacher.controller;

import com.inturn.suncomputer.teacher.dto.CreateTeacherRequest;
import com.inturn.suncomputer.teacher.dto.TeacherResponse;
import com.inturn.suncomputer.teacher.dto.UpdateTeacherRequest;
import com.inturn.suncomputer.teacher.service.TeacherService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(
            TeacherService teacherService
    ) {

        this.teacherService =
                teacherService;
    }

    @PostMapping
    public ResponseEntity<TeacherResponse>
    createTeacher(
            @Valid
            @RequestBody
            CreateTeacherRequest request
    ) {

        TeacherResponse response =
                teacherService.createTeacher(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TeacherResponse>>
    getTeachers(
            @RequestParam(
                    required = false,
                    defaultValue = "false"
            )
            boolean activeOnly
    ) {

        if (activeOnly) {

            return ResponseEntity.ok(
                    teacherService
                            .getActiveTeachers()
            );
        }

        return ResponseEntity.ok(
                teacherService.getAllTeachers()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse>
    getTeacher(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                teacherService
                        .getTeacherById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse>
    updateTeacher(
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateTeacherRequest request
    ) {

        return ResponseEntity.ok(
                teacherService.updateTeacher(
                        id,
                        request
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void>
    deactivateTeacher(
            @PathVariable Long id
    ) {

        teacherService
                .deactivateTeacher(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
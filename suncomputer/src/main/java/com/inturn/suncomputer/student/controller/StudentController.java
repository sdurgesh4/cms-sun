package com.inturn.suncomputer.student.controller;

import com.inturn.suncomputer.student.dto.CreateStudentRequest;
import com.inturn.suncomputer.student.dto.StudentResponse;
import com.inturn.suncomputer.student.dto.UpdateStudentRequest;
import com.inturn.suncomputer.student.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(
            StudentService studentService
    ) {

        this.studentService =
                studentService;
    }

    @PostMapping
    public ResponseEntity<StudentResponse>
    createStudent(
            @Valid
            @RequestBody
            CreateStudentRequest request
    ) {

        StudentResponse response =
                studentService.createStudent(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>>
    getStudents(
            @RequestParam(
                    required = false,
                    defaultValue = "false"
            )
            boolean activeOnly
    ) {

        if (activeOnly) {

            return ResponseEntity.ok(
                    studentService
                            .getActiveStudents()
            );
        }

        return ResponseEntity.ok(
                studentService.getAllStudents()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse>
    getStudent(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                studentService
                        .getStudentById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse>
    updateStudent(
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateStudentRequest request
    ) {

        return ResponseEntity.ok(
                studentService.updateStudent(
                        id,
                        request
                )
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void>
    deactivateStudent(
            @PathVariable Long id
    ) {

        studentService
                .deactivateStudent(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
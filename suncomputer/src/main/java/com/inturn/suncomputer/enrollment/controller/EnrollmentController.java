package com.inturn.suncomputer.enrollment.controller;

import com.inturn.suncomputer.enrollment.dto.CreateEnrollmentRequest;
import com.inturn.suncomputer.enrollment.dto.EnrollmentResponse;
import com.inturn.suncomputer.enrollment.dto.UpdateEnrollmentRequest;
import com.inturn.suncomputer.enrollment.service.EnrollmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(
            EnrollmentService enrollmentService
    ) {

        this.enrollmentService =
                enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse>
    createEnrollment(
            @Valid
            @RequestBody
            CreateEnrollmentRequest request
    ) {

        EnrollmentResponse response =
                enrollmentService
                        .createEnrollment(
                                request
                        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>>
    getAllEnrollments() {

        return ResponseEntity.ok(
                enrollmentService
                        .getAllEnrollments()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse>
    getEnrollment(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                enrollmentService
                        .getEnrollmentById(id)
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<EnrollmentResponse>>
    getByStudent(
            @PathVariable Long studentId
    ) {

        return ResponseEntity.ok(
                enrollmentService
                        .getEnrollmentsByStudent(
                                studentId
                        )
        );
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<EnrollmentResponse>>
    getByBatch(
            @PathVariable Long batchId
    ) {

        return ResponseEntity.ok(
                enrollmentService
                        .getEnrollmentsByBatch(
                                batchId
                        )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponse>
    updateEnrollment(
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateEnrollmentRequest request
    ) {

        return ResponseEntity.ok(
                enrollmentService
                        .updateEnrollment(
                                id,
                                request
                        )
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void>
    cancelEnrollment(
            @PathVariable Long id
    ) {

        enrollmentService
                .cancelEnrollment(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
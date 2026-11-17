package com.inturn.suncomputer.installment.controller;

import com.inturn.suncomputer.installment.dto.CreateInstallmentRequest;
import com.inturn.suncomputer.installment.dto.InstallmentResponse;
import com.inturn.suncomputer.installment.dto.InstallmentSummaryResponse;
import com.inturn.suncomputer.installment.service.InstallmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/installments")
public class InstallmentController {

    private final InstallmentService installmentService;

    public InstallmentController(
            InstallmentService installmentService
    ) {

        this.installmentService =
                installmentService;
    }

    @PostMapping
    public ResponseEntity<InstallmentResponse>
    createInstallment(
            @Valid
            @RequestBody
            CreateInstallmentRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        installmentService
                                .createInstallment(
                                        request
                                )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstallmentResponse>
    getInstallment(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                installmentService
                        .getInstallmentById(id)
        );
    }

    @GetMapping(
            "/enrollment/{enrollmentId}"
    )
    public ResponseEntity<List<InstallmentResponse>>
    getByEnrollment(
            @PathVariable Long enrollmentId
    ) {

        return ResponseEntity.ok(
                installmentService
                        .getInstallmentsByEnrollment(
                                enrollmentId
                        )
        );
    }

    @GetMapping(
            "/enrollment/{enrollmentId}/summary"
    )
    public ResponseEntity<InstallmentSummaryResponse>
    getSummary(
            @PathVariable Long enrollmentId
    ) {

        return ResponseEntity.ok(
                installmentService
                        .getInstallmentSummary(
                                enrollmentId
                        )
        );
    }
}
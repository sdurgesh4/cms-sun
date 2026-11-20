package com.inturn.suncomputer.payment.controller;

import com.inturn.suncomputer.payment.dto.CreatePaymentRequest;
import com.inturn.suncomputer.payment.dto.PaymentResponse;
import com.inturn.suncomputer.payment.dto.PaymentSummaryResponse;
import com.inturn.suncomputer.payment.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService
    ) {

        this.paymentService =
                paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse>
    createPayment(
            @Valid
            @RequestBody
            CreatePaymentRequest request
    ) {

        PaymentResponse response =
                paymentService.createPayment(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>>
    getAllPayments() {

        return ResponseEntity.ok(
                paymentService
                        .getAllPayments()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse>
    getPayment(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                paymentService
                        .getPaymentById(id)
        );
    }

    @GetMapping(
            "/enrollment/{enrollmentId}"
    )
    public ResponseEntity<List<PaymentResponse>>
    getPaymentsByEnrollment(
            @PathVariable Long enrollmentId
    ) {

        return ResponseEntity.ok(
                paymentService
                        .getPaymentsByEnrollment(
                                enrollmentId
                        )
        );
    }

    @GetMapping(
            "/enrollment/{enrollmentId}/summary"
    )
    public ResponseEntity<PaymentSummaryResponse>
    getPaymentSummary(
            @PathVariable Long enrollmentId
    ) {

        return ResponseEntity.ok(
                paymentService
                        .getPaymentSummary(
                                enrollmentId
                        )
        );
    }
}
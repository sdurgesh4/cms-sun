package com.inturn.suncomputer.payment.service;

import com.inturn.suncomputer.payment.dto.CreatePaymentRequest;
import com.inturn.suncomputer.payment.dto.PaymentResponse;
import com.inturn.suncomputer.payment.dto.PaymentSummaryResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(
            CreatePaymentRequest request
    );

    PaymentResponse getPaymentById(
            Long id
    );

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsByEnrollment(
            Long enrollmentId
    );

    PaymentSummaryResponse getPaymentSummary(
            Long enrollmentId
    );
}
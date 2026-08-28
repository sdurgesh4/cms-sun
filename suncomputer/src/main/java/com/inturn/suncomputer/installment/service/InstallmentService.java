package com.inturn.suncomputer.installment.service;

import com.inturn.suncomputer.installment.dto.CreateInstallmentRequest;
import com.inturn.suncomputer.installment.dto.InstallmentResponse;
import com.inturn.suncomputer.installment.dto.InstallmentSummaryResponse;

import java.util.List;

public interface InstallmentService {

    InstallmentResponse createInstallment(
            CreateInstallmentRequest request
    );

    InstallmentResponse getInstallmentById(
            Long id
    );

    List<InstallmentResponse>
    getInstallmentsByEnrollment(
            Long enrollmentId
    );

    InstallmentSummaryResponse
    getInstallmentSummary(
            Long enrollmentId
    );

    void updateOverdueInstallments();
}
package com.inturn.suncomputer.batch.service;

import com.inturn.suncomputer.batch.dto.BatchResponse;
import com.inturn.suncomputer.batch.dto.CreateBatchRequest;
import com.inturn.suncomputer.batch.dto.UpdateBatchRequest;

import java.util.List;

public interface BatchService {

    BatchResponse createBatch(
            CreateBatchRequest request
    );

    BatchResponse getBatchById(
            Long id
    );

    List<BatchResponse> getAllBatches();

    List<BatchResponse> getBatchesByCourse(
            Long courseId
    );

    List<BatchResponse> getBatchesByTeacher(
            Long teacherId
    );

    BatchResponse updateBatch(
            Long id,
            UpdateBatchRequest request
    );

    void cancelBatch(
            Long id
    );
}
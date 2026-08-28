package com.inturn.suncomputer.batch.controller;

import com.inturn.suncomputer.batch.dto.BatchResponse;
import com.inturn.suncomputer.batch.dto.CreateBatchRequest;
import com.inturn.suncomputer.batch.dto.UpdateBatchRequest;
import com.inturn.suncomputer.batch.service.BatchService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(
            BatchService batchService
    ) {

        this.batchService =
                batchService;
    }

    @PostMapping
    public ResponseEntity<BatchResponse>
    createBatch(
            @Valid
            @RequestBody
            CreateBatchRequest request
    ) {

        BatchResponse response =
                batchService.createBatch(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<BatchResponse>>
    getAllBatches() {

        return ResponseEntity.ok(
                batchService.getAllBatches()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchResponse>
    getBatch(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                batchService.getBatchById(id)
        );
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<BatchResponse>>
    getByCourse(
            @PathVariable Long courseId
    ) {

        return ResponseEntity.ok(
                batchService
                        .getBatchesByCourse(
                                courseId
                        )
        );
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<BatchResponse>>
    getByTeacher(
            @PathVariable Long teacherId
    ) {

        return ResponseEntity.ok(
                batchService
                        .getBatchesByTeacher(
                                teacherId
                        )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchResponse>
    updateBatch(
            @PathVariable Long id,

            @Valid
            @RequestBody
            UpdateBatchRequest request
    ) {

        return ResponseEntity.ok(
                batchService.updateBatch(
                        id,
                        request
                )
        );
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void>
    cancelBatch(
            @PathVariable Long id
    ) {

        batchService.cancelBatch(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
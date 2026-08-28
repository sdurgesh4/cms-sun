package com.inturn.suncomputer.batch.repository;

import com.inturn.suncomputer.batch.entity.Batch;
import com.inturn.suncomputer.batch.entity.BatchStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BatchRepository
        extends JpaRepository<Batch, Long> {

    Optional<Batch> findByBatchCode(
            String batchCode
    );

    boolean existsByBatchCode(
            String batchCode
    );

    List<Batch> findByStatus(
            BatchStatus status
    );

    List<Batch> findByCourseId(
            Long courseId
    );

    List<Batch> findByTeacherId(
            Long teacherId
    );
}
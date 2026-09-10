package com.inturn.suncomputer.enquiry.repository;

import com.inturn.suncomputer.enquiry.entity.Enquiry;
import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EnquiryRepository
        extends JpaRepository<Enquiry, Long> {


    Optional<Enquiry>
    findFirstByMobileOrderByCreatedAtDesc(
            String mobile
    );


    List<Enquiry>
    findByStatusOrderByCreatedAtDesc(
            EnquiryStatus status
    );


    List<Enquiry>
    findByNextFollowUpDateOrderByCreatedAtDesc(
            LocalDate date
    );


    List<Enquiry>
    findByAssignedToOrderByCreatedAtDesc(
            Long assignedTo
    );

    long countByStatus(
            EnquiryStatus status
    );

    long countByStatusIn(
            List<EnquiryStatus> statuses
    );

    long countByNextFollowUpDate(
            LocalDate date
    );
}
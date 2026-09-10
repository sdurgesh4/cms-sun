package com.inturn.suncomputer.enquiry.repository;

import com.inturn.suncomputer.enquiry.entity.EnquiryFollowUp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EnquiryFollowUpRepository
        extends JpaRepository<EnquiryFollowUp, Long> {


    List<EnquiryFollowUp>
    findByEnquiryIdOrderByFollowUpDateDescCreatedAtDesc(
            Long enquiryId
    );


    List<EnquiryFollowUp>
    findByFollowUpDateOrderByCreatedAtDesc(
            LocalDate date
    );


    List<EnquiryFollowUp>
    findByNextFollowUpDateOrderByCreatedAtDesc(
            LocalDate date
    );
}
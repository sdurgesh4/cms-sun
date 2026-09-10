package com.inturn.suncomputer.enquiry.service;

import com.inturn.suncomputer.enquiry.dto.CreateEnquiryRequest;
import com.inturn.suncomputer.enquiry.dto.EnquiryResponse;
import com.inturn.suncomputer.enquiry.dto.UpdateEnquiryRequest;
import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;

import java.time.LocalDate;
import java.util.List;

public interface EnquiryService {


    EnquiryResponse createEnquiry(
            CreateEnquiryRequest request
    );


    EnquiryResponse getEnquiryById(
            Long id
    );


    List<EnquiryResponse> getAllEnquiries();


    List<EnquiryResponse> getEnquiriesByStatus(
            EnquiryStatus status
    );


    List<EnquiryResponse> getTodayFollowUps();


    List<EnquiryResponse> getFollowUpsByDate(
            LocalDate date
    );


    EnquiryResponse updateEnquiry(
            Long id,
            UpdateEnquiryRequest request
    );


    void deleteEnquiry(
            Long id
    );
}
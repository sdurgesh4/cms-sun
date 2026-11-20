package com.inturn.suncomputer.enquiry.service;

import com.inturn.suncomputer.enquiry.dto.CreateFollowUpRequest;
import com.inturn.suncomputer.enquiry.dto.FollowUpResponse;

import java.time.LocalDate;
import java.util.List;

public interface EnquiryFollowUpService {

    FollowUpResponse createFollowUp(
            Long enquiryId,
            CreateFollowUpRequest request
    );


    List<FollowUpResponse> getEnquiryFollowUps(
            Long enquiryId
    );


    List<FollowUpResponse> getFollowUpsByDate(
            LocalDate date
    );


    List<FollowUpResponse> getTodayFollowUps();


    void deleteFollowUp(
            Long id
    );
}
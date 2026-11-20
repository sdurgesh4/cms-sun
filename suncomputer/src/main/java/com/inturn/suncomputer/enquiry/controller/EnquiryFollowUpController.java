package com.inturn.suncomputer.enquiry.controller;

import com.inturn.suncomputer.enquiry.dto.CreateFollowUpRequest;
import com.inturn.suncomputer.enquiry.dto.FollowUpResponse;
import com.inturn.suncomputer.enquiry.service.EnquiryFollowUpService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/enquiries")
@RequiredArgsConstructor
public class EnquiryFollowUpController {


    private final EnquiryFollowUpService
            followUpService;


    // =====================================================
    // CREATE FOLLOW-UP
    // =====================================================

    @PostMapping("/{enquiryId}/follow-ups")
    public ResponseEntity<FollowUpResponse>
    createFollowUp(

            @PathVariable
            Long enquiryId,

            @Valid
            @RequestBody
            CreateFollowUpRequest request

    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        followUpService
                                .createFollowUp(
                                        enquiryId,
                                        request
                                )
                );
    }


    // =====================================================
    // GET ENQUIRY FOLLOW-UP HISTORY
    // =====================================================

    @GetMapping("/{enquiryId}/follow-ups")
    public ResponseEntity<List<FollowUpResponse>>
    getEnquiryFollowUps(

            @PathVariable
            Long enquiryId

    ) {

        return ResponseEntity.ok(
                followUpService
                        .getEnquiryFollowUps(
                                enquiryId
                        )
        );
    }


    // =====================================================
    // GET TODAY'S FOLLOW-UPS
    // =====================================================

    @GetMapping("/follow-ups/history/today")
    public ResponseEntity<List<FollowUpResponse>>
    getTodayFollowUps() {

        return ResponseEntity.ok(
                followUpService
                        .getTodayFollowUps()
        );
    }


    // =====================================================
    // GET FOLLOW-UPS BY DATE
    // =====================================================

    @GetMapping("/follow-ups/history")
    public ResponseEntity<List<FollowUpResponse>>
    getFollowUpsByDate(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date

    ) {

        return ResponseEntity.ok(
                followUpService
                        .getFollowUpsByDate(
                                date
                        )
        );
    }


    // =====================================================
    // DELETE FOLLOW-UP
    // =====================================================

    @DeleteMapping("/follow-ups/{id}")
    public ResponseEntity<Void>
    deleteFollowUp(
            @PathVariable
            Long id
    ) {

        followUpService.deleteFollowUp(
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}
package com.inturn.suncomputer.enquiry.controller;

import com.inturn.suncomputer.enquiry.dto.CreateEnquiryRequest;
import com.inturn.suncomputer.enquiry.dto.EnquiryResponse;
import com.inturn.suncomputer.enquiry.dto.UpdateEnquiryRequest;
import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;
import com.inturn.suncomputer.enquiry.service.EnquiryService;

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
public class EnquiryController {


    private final EnquiryService
            enquiryService;


    // =====================================================
    // CREATE ENQUIRY
    // =====================================================

    @PostMapping
    public ResponseEntity<EnquiryResponse>
    createEnquiry(

            @Valid
            @RequestBody
            CreateEnquiryRequest request

    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        enquiryService
                                .createEnquiry(
                                        request
                                )
                );
    }


    // =====================================================
    // GET ALL ENQUIRIES
    // =====================================================

    @GetMapping
    public ResponseEntity<List<EnquiryResponse>>
    getAllEnquiries() {

        return ResponseEntity.ok(
                enquiryService
                        .getAllEnquiries()
        );
    }


    // =====================================================
    // GET ENQUIRY BY ID
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<EnquiryResponse>
    getEnquiryById(

            @PathVariable
            Long id

    ) {

        return ResponseEntity.ok(
                enquiryService
                        .getEnquiryById(id)
        );
    }


    // =====================================================
    // GET BY STATUS
    // =====================================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EnquiryResponse>>
    getByStatus(

            @PathVariable
            EnquiryStatus status

    ) {

        return ResponseEntity.ok(
                enquiryService
                        .getEnquiriesByStatus(
                                status
                        )
        );
    }


    // =====================================================
    // TODAY'S FOLLOW UPS
    // =====================================================

    @GetMapping("/follow-ups/today")
    public ResponseEntity<List<EnquiryResponse>>
    getTodayFollowUps() {

        return ResponseEntity.ok(
                enquiryService
                        .getTodayFollowUps()
        );
    }


    // =====================================================
    // FOLLOW UPS BY DATE
    // =====================================================

    @GetMapping("/follow-ups")
    public ResponseEntity<List<EnquiryResponse>>
    getFollowUpsByDate(

            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate date

    ) {

        return ResponseEntity.ok(
                enquiryService
                        .getFollowUpsByDate(
                                date
                        )
        );
    }


    // =====================================================
    // UPDATE ENQUIRY
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<EnquiryResponse>
    updateEnquiry(

            @PathVariable
            Long id,

            @Valid
            @RequestBody
            UpdateEnquiryRequest request

    ) {

        return ResponseEntity.ok(
                enquiryService
                        .updateEnquiry(
                                id,
                                request
                        )
        );
    }


    // =====================================================
    // DELETE ENQUIRY
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>
    deleteEnquiry(

            @PathVariable
            Long id

    ) {

        enquiryService.deleteEnquiry(
                id
        );

        return ResponseEntity.noContent()
                .build();
    }
}
package com.inturn.suncomputer.enquiry.controller;

import com.inturn.suncomputer.enquiry.dto.ConvertEnquiryRequest;
import com.inturn.suncomputer.enquiry.dto.EnquiryConversionResponse;
import com.inturn.suncomputer.enquiry.service.EnquiryConversionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enquiries")
@RequiredArgsConstructor
public class EnquiryConversionController {

    private final EnquiryConversionService conversionService;

    @PostMapping("/{enquiryId}/convert")
    public ResponseEntity<EnquiryConversionResponse> convertEnquiry(
            @PathVariable Long enquiryId,
            @Valid @RequestBody ConvertEnquiryRequest request
    ) {

        EnquiryConversionResponse response =
                conversionService.convertEnquiry(
                        enquiryId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
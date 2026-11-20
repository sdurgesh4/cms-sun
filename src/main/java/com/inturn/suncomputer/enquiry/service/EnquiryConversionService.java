package com.inturn.suncomputer.enquiry.service;

import com.inturn.suncomputer.enquiry.dto.ConvertEnquiryRequest;
import com.inturn.suncomputer.enquiry.dto.EnquiryConversionResponse;

public interface EnquiryConversionService {

    EnquiryConversionResponse convertEnquiry(
            Long enquiryId,
            ConvertEnquiryRequest request
    );
}
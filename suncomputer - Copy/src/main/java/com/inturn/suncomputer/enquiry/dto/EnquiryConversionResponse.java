package com.inturn.suncomputer.enquiry.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EnquiryConversionResponse(

        Long enquiryId,

        Long studentId,

        String studentCode,

        Long enrollmentId,

        Long batchId,

        BigDecimal agreedFee,

        BigDecimal discount,

        BigDecimal finalFee,

        LocalDate admissionDate,

        LocalDate enrollmentDate

) {
}
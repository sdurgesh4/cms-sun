package com.inturn.suncomputer.enquiry.service;

import com.inturn.suncomputer.enquiry.dto.CreateEnquiryRequest;
import com.inturn.suncomputer.enquiry.dto.EnquiryResponse;
import com.inturn.suncomputer.enquiry.dto.UpdateEnquiryRequest;
import com.inturn.suncomputer.enquiry.entity.Enquiry;
import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;
import com.inturn.suncomputer.enquiry.repository.EnquiryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnquiryServiceImpl
        implements EnquiryService {


    private final EnquiryRepository
            enquiryRepository;


    @Override
    public EnquiryResponse createEnquiry(
            CreateEnquiryRequest request
    ) {

        /*
         * Basic duplicate protection.
         *
         * A person can enquire more than once,
         * so we do NOT reject duplicate mobile numbers.
         *
         * We simply create a new enquiry.
         */

        Enquiry enquiry =
                Enquiry.builder()

                        .fullName(
                                request.fullName()
                        )

                        .mobile(
                                request.mobile()
                        )

                        .email(
                                request.email()
                        )

                        .address(
                                request.address()
                        )

                        .interestedCourse(
                                request.interestedCourse()
                        )

                        .source(
                                request.source()
                        )

                        .nextFollowUpDate(
                                request.nextFollowUpDate()
                        )

                        .remarks(
                                request.remarks()
                        )

                        .assignedTo(
                                request.assignedTo()
                        )

                        .status(
                                EnquiryStatus.NEW
                        )

                        .build();


        Enquiry saved =
                enquiryRepository.save(
                        enquiry
                );


        return mapToResponse(
                saved
        );
    }


    @Override
    @Transactional(readOnly = true)
    public EnquiryResponse getEnquiryById(
            Long id
    ) {

        Enquiry enquiry =
                enquiryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Enquiry not found with id: "
                                                + id
                                )
                        );


        return mapToResponse(
                enquiry
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<EnquiryResponse> getAllEnquiries() {

        return enquiryRepository
                .findAll(
                        org.springframework.data.domain.Sort
                                .by(
                                        org.springframework.data.domain.Sort.Direction.DESC,
                                        "createdAt"
                                )
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<EnquiryResponse>
    getEnquiriesByStatus(
            EnquiryStatus status
    ) {

        return enquiryRepository
                .findByStatusOrderByCreatedAtDesc(
                        status
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<EnquiryResponse>
    getTodayFollowUps() {

        return getFollowUpsByDate(
                LocalDate.now()
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<EnquiryResponse>
    getFollowUpsByDate(
            LocalDate date
    ) {

        return enquiryRepository
                .findByNextFollowUpDateOrderByCreatedAtDesc(
                        date
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public EnquiryResponse updateEnquiry(
            Long id,
            UpdateEnquiryRequest request
    ) {

        Enquiry enquiry =
                enquiryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Enquiry not found with id: "
                                                + id
                                )
                        );


        if (
                request.fullName() != null
        ) {

            enquiry.setFullName(
                    request.fullName()
            );
        }


        if (
                request.mobile() != null
        ) {

            enquiry.setMobile(
                    request.mobile()
            );
        }


        if (
                request.email() != null
        ) {

            enquiry.setEmail(
                    request.email()
            );
        }


        if (
                request.address() != null
        ) {

            enquiry.setAddress(
                    request.address()
            );
        }


        if (
                request.interestedCourse() != null
        ) {

            enquiry.setInterestedCourse(
                    request.interestedCourse()
            );
        }


        if (
                request.status() != null
        ) {

            enquiry.setStatus(
                    request.status()
            );
        }


        if (
                request.source() != null
        ) {

            enquiry.setSource(
                    request.source()
            );
        }


        if (
                request.nextFollowUpDate() != null
        ) {

            enquiry.setNextFollowUpDate(
                    request.nextFollowUpDate()
            );
        }


        if (
                request.remarks() != null
        ) {

            enquiry.setRemarks(
                    request.remarks()
            );
        }


        if (
                request.assignedTo() != null
        ) {

            enquiry.setAssignedTo(
                    request.assignedTo()
            );
        }


        Enquiry updated =
                enquiryRepository.save(
                        enquiry
                );


        return mapToResponse(
                updated
        );
    }


    @Override
    public void deleteEnquiry(
            Long id
    ) {

        Enquiry enquiry =
                enquiryRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Enquiry not found with id: "
                                                + id
                                )
                        );


        enquiryRepository.delete(
                enquiry
        );
    }


    private EnquiryResponse mapToResponse(
            Enquiry enquiry
    ) {

        return new EnquiryResponse(

                enquiry.getId(),

                enquiry.getFullName(),

                enquiry.getMobile(),

                enquiry.getEmail(),

                enquiry.getAddress(),

                enquiry.getInterestedCourse(),

                enquiry.getStatus(),

                enquiry.getSource(),

                enquiry.getNextFollowUpDate(),

                enquiry.getRemarks(),

                enquiry.getAssignedTo(),

                enquiry.getConvertedStudentId(),

                enquiry.getCreatedAt(),

                enquiry.getUpdatedAt()
        );
    }
}
package com.inturn.suncomputer.enquiry.service;

import com.inturn.suncomputer.enquiry.dto.CreateFollowUpRequest;
import com.inturn.suncomputer.enquiry.dto.FollowUpResponse;
import com.inturn.suncomputer.enquiry.entity.Enquiry;
import com.inturn.suncomputer.enquiry.entity.EnquiryFollowUp;
import com.inturn.suncomputer.enquiry.repository.EnquiryFollowUpRepository;
import com.inturn.suncomputer.enquiry.repository.EnquiryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EnquiryFollowUpServiceImpl
        implements EnquiryFollowUpService {


    private final EnquiryRepository enquiryRepository;

    private final EnquiryFollowUpRepository
            followUpRepository;


    @Override
    public FollowUpResponse createFollowUp(
            Long enquiryId,
            CreateFollowUpRequest request
    ) {

        Enquiry enquiry =
                enquiryRepository
                        .findById(enquiryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Enquiry not found with id: "
                                                + enquiryId
                                )
                        );


        EnquiryFollowUp followUp =
                EnquiryFollowUp.builder()

                        .enquiry(enquiry)

                        .followUpType(
                                request.followUpType()
                        )

                        .followUpDate(
                                request.followUpDate()
                        )

                        .notes(
                                request.notes()
                        )

                        .nextFollowUpDate(
                                request.nextFollowUpDate()
                        )

                        .createdBy(
                                request.createdBy()
                        )

                        .build();


        EnquiryFollowUp saved =
                followUpRepository.save(
                        followUp
                );


        /*
         * Keep the enquiry's next follow-up date
         * synchronized with the latest follow-up.
         */

        if (
                request.nextFollowUpDate() != null
        ) {

            enquiry.setNextFollowUpDate(
                    request.nextFollowUpDate()
            );

            enquiryRepository.save(
                    enquiry
            );
        }


        return mapToResponse(
                saved
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getEnquiryFollowUps(
            Long enquiryId
    ) {

        if (
                !enquiryRepository
                        .existsById(enquiryId)
        ) {

            throw new RuntimeException(
                    "Enquiry not found with id: "
                            + enquiryId
            );
        }


        return followUpRepository
                .findByEnquiryIdOrderByFollowUpDateDescCreatedAtDesc(
                        enquiryId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getFollowUpsByDate(
            LocalDate date
    ) {

        return followUpRepository
                .findByFollowUpDateOrderByCreatedAtDesc(
                        date
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<FollowUpResponse>
    getTodayFollowUps() {

        return getFollowUpsByDate(
                LocalDate.now()
        );
    }


    @Override
    public void deleteFollowUp(
            Long id
    ) {

        if (
                !followUpRepository
                        .existsById(id)
        ) {

            throw new RuntimeException(
                    "Follow-up not found with id: "
                            + id
            );
        }


        followUpRepository.deleteById(
                id
        );
    }


    private FollowUpResponse mapToResponse(
            EnquiryFollowUp followUp
    ) {

        return new FollowUpResponse(

                followUp.getId(),

                followUp.getEnquiry().getId(),

                followUp.getFollowUpType(),

                followUp.getFollowUpDate(),

                followUp.getNotes(),

                followUp.getNextFollowUpDate(),

                followUp.getCreatedBy(),

                followUp.getCreatedAt()
        );
    }
}
package com.inturn.suncomputer.enquiry.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "enquiry_follow_ups",
        indexes = {
                @Index(
                        name = "idx_follow_up_enquiry",
                        columnList = "enquiry_id"
                ),
                @Index(
                        name = "idx_follow_up_date",
                        columnList = "follow_up_date"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnquiryFollowUp {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "enquiry_id",
            nullable = false
    )
    private Enquiry enquiry;


    @Enumerated(EnumType.STRING)
    @Column(
            name = "follow_up_type",
            nullable = false,
            length = 30
    )
    private FollowUpType followUpType;


    @Column(
            name = "follow_up_date",
            nullable = false
    )
    private LocalDate followUpDate;


    @Column(
            columnDefinition = "TEXT"
    )
    private String notes;


    @Column(
            name = "next_follow_up_date"
    )
    private LocalDate nextFollowUpDate;


    @Column(
            name = "created_by"
    )
    private Long createdBy;


    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
    }
}
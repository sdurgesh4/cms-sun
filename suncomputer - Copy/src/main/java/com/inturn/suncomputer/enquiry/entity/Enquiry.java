package com.inturn.suncomputer.enquiry.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "enquiries",
        indexes = {
                @Index(
                        name = "idx_enquiry_mobile",
                        columnList = "mobile"
                ),
                @Index(
                        name = "idx_enquiry_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_enquiry_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enquiry {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    /*
     * Person details
     */

    @Column(
            nullable = false,
            length = 100
    )
    private String fullName;


    @Column(
            nullable = false,
            length = 15
    )
    private String mobile;


    @Column(
            length = 150
    )
    private String email;


    @Column(
            length = 255
    )
    private String address;


    /*
     * Course interested in
     *
     * We keep this as a reference for now.
     * Course relationship will be added after
     * confirming your existing Course entity field names.
     */

    @Column(
            name = "interested_course",
            length = 150
    )
    private String interestedCourse;


    /*
     * Lead information
     */

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private EnquiryStatus status =
            EnquiryStatus.NEW;


    @Enumerated(EnumType.STRING)
    @Column(
            length = 30
    )
    private EnquirySource source;


    /*
     * Follow-up information
     */

    @Column(
            name = "next_follow_up_date"
    )
    private LocalDate nextFollowUpDate;


    @Column(
            columnDefinition = "TEXT"
    )
    private String remarks;


    /*
     * Assigned counsellor / staff member.
     *
     * We will connect this to User later
     * after checking your existing User entity.
     */

    @Column(
            name = "assigned_to"
    )
    private Long assignedTo;


    /*
     * Conversion information
     */

    @Column(
            name = "converted_student_id"
    )
    private Long convertedStudentId;


    /*
     * Audit fields
     */

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updated_at"
    )
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }
}
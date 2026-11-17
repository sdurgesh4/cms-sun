package com.inturn.suncomputer.attendance.entity;

import com.inturn.suncomputer.enrollment.entity.Enrollment;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_enrollment_date",
                        columnNames = {
                                "enrollment_id",
                                "attendance_date"
                        }
                )
        }
)
public class Attendance {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    /*
     * Student's enrollment.
     *
     * We link attendance to enrollment
     * instead of directly to student.
     */

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "enrollment_id",
            nullable = false
    )
    private Enrollment enrollment;


    /*
     * Date on which attendance was taken.
     */

    @Column(
            name = "attendance_date",
            nullable = false
    )
    private LocalDate attendanceDate;


    /*
     * Attendance status.
     */

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private AttendanceStatus status;


    /*
     * Optional remarks.
     */

    @Column(
            length = 500
    )
    private String remarks;


    /*
     * When attendance was created.
     */

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    /*
     * Automatically set creation time.
     */

    @PrePersist
    protected void onCreate() {

        createdAt =
                LocalDateTime.now();
    }


    // =========================================================
    // GETTERS AND SETTERS
    // =========================================================

    public Long getId() {
        return id;
    }


    public Enrollment getEnrollment() {
        return enrollment;
    }


    public void setEnrollment(
            Enrollment enrollment
    ) {

        this.enrollment =
                enrollment;
    }


    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }


    public void setAttendanceDate(
            LocalDate attendanceDate
    ) {

        this.attendanceDate =
                attendanceDate;
    }


    public AttendanceStatus getStatus() {
        return status;
    }


    public void setStatus(
            AttendanceStatus status
    ) {

        this.status =
                status;
    }


    public String getRemarks() {
        return remarks;
    }


    public void setRemarks(
            String remarks
    ) {

        this.remarks =
                remarks;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
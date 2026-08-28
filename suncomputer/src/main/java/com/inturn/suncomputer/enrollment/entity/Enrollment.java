package com.inturn.suncomputer.enrollment.entity;

import com.inturn.suncomputer.batch.entity.Batch;
import com.inturn.suncomputer.student.entity.Student;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "enrollments",
        indexes = {
                @Index(
                        name = "idx_enrollment_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_enrollment_batch",
                        columnList = "batch_id"
                )
        }
)
public class Enrollment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "batch_id",
            nullable = false
    )
    private Batch batch;

    @Column(
            name = "enrollment_date",
            nullable = false
    )
    private LocalDate enrollmentDate;

    @Column(
            name = "agreed_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal agreedFee;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal discount =
            BigDecimal.ZERO;

    @Column(
            name = "final_fee",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal finalFee;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private EnrollmentStatus status =
            EnrollmentStatus.ACTIVE;

    @Column(length = 1000)
    private String notes;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
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

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Batch getBatch() {
        return batch;
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(
            LocalDate enrollmentDate
    ) {
        this.enrollmentDate = enrollmentDate;
    }

    public BigDecimal getAgreedFee() {
        return agreedFee;
    }

    public void setAgreedFee(
            BigDecimal agreedFee
    ) {
        this.agreedFee = agreedFee;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(
            BigDecimal discount
    ) {
        this.discount = discount;
    }

    public BigDecimal getFinalFee() {
        return finalFee;
    }

    public void setFinalFee(
            BigDecimal finalFee
    ) {
        this.finalFee = finalFee;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(
            EnrollmentStatus status
    ) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
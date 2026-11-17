package com.inturn.suncomputer.installment.entity;

import com.inturn.suncomputer.enrollment.entity.Enrollment;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "installments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_enrollment_installment_number",
                        columnNames = {
                                "enrollment_id",
                                "installment_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_installment_enrollment",
                        columnList = "enrollment_id"
                ),
                @Index(
                        name = "idx_installment_due_date",
                        columnList = "due_date"
                )
        }
)
public class Installment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "enrollment_id",
            nullable = false
    )
    private Enrollment enrollment;

    @Column(
            name = "installment_number",
            nullable = false
    )
    private Integer installmentNumber;

    @Column(
            name = "due_date",
            nullable = false
    )
    private LocalDate dueDate;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private InstallmentStatus status =
            InstallmentStatus.PENDING;

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

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(
            Enrollment enrollment
    ) {
        this.enrollment = enrollment;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(
            Integer installmentNumber
    ) {
        this.installmentNumber =
                installmentNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(
            LocalDate dueDate
    ) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount
    ) {
        this.amount = amount;
    }

    public InstallmentStatus getStatus() {
        return status;
    }

    public void setStatus(
            InstallmentStatus status
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
package com.inturn.suncomputer.payment.entity;

import com.inturn.suncomputer.enrollment.entity.Enrollment;

import com.inturn.suncomputer.installment.entity.Installment;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(
                        name = "idx_payment_enrollment",
                        columnList = "enrollment_id"
                ),
                @Index(
                        name = "idx_payment_date",
                        columnList = "payment_date"
                )
        }
)
public class Payment {

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
            name = "receipt_number",
            nullable = false,
            unique = true,
            length = 50
    )
    private String receiptNumber;

    @Column(
            name = "payment_date",
            nullable = false
    )
    private LocalDate paymentDate;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "payment_method",
            nullable = false,
            length = 30
    )
    private PaymentMethod paymentMethod;

    @Column(
            name = "transaction_reference",
            length = 150
    )
    private String transactionReference;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private PaymentStatus status = PaymentStatus.SUCCESS;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "installment_id"
    )
    private Installment installment;


    public Installment getInstallment() {
        return installment;
    }

    public void setInstallment(
            Installment installment
    ) {
        this.installment = installment;
    }

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

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(
            String receiptNumber
    ) {
        this.receiptNumber = receiptNumber;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(
            LocalDate paymentDate
    ) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount
    ) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(
            PaymentMethod paymentMethod
    ) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(
            String transactionReference
    ) {
        this.transactionReference =
                transactionReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(
            PaymentStatus status
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
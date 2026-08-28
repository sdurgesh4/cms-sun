package com.inturn.suncomputer.teacher.entity;

import com.inturn.suncomputer.user.entity.User;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "teachers",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "employee_code"
                ),
                @UniqueConstraint(
                        columnNames = "user_id"
                )
        }
)
public class Teacher {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(
            name = "employee_code",
            nullable = false,
            unique = true,
            length = 50
    )
    private String employeeCode;

    @Column(length = 200)
    private String specialization;

    @Column(length = 200)
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private TeacherStatus status =
            TeacherStatus.ACTIVE;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(
            String employeeCode
    ) {
        this.employeeCode = employeeCode;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(
            String specialization
    ) {
        this.specialization = specialization;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(
            String qualification
    ) {
        this.qualification = qualification;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(
            Integer experienceYears
    ) {
        this.experienceYears =
                experienceYears;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(
            LocalDate joiningDate
    ) {
        this.joiningDate = joiningDate;
    }

    public TeacherStatus getStatus() {
        return status;
    }

    public void setStatus(
            TeacherStatus status
    ) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
package com.inturn.suncomputer.batch.entity;

import com.inturn.suncomputer.course.entity.Course;
import com.inturn.suncomputer.teacher.entity.Teacher;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "batches",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "batch_code"
                )
        }
)
public class Batch {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            name = "batch_code",
            nullable = false,
            unique = true,
            length = 50
    )
    private String batchCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_id",
            nullable = false
    )
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "teacher_id",
            nullable = false
    )
    private Teacher teacher;

    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(
            name = "start_time",
            nullable = false
    )
    private LocalTime startTime;

    @Column(
            name = "end_time",
            nullable = false
    )
    private LocalTime endTime;

    @ElementCollection
    @CollectionTable(
            name = "batch_days",
            joinColumns = @JoinColumn(
                    name = "batch_id"
            )
    )
    @Enumerated(EnumType.STRING)
    @Column(
            name = "day",
            nullable = false
    )
    private Set<BatchDay> days =
            new HashSet<>();

    @Column(length = 100)
    private String room;

    @Column(
            nullable = false
    )
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private BatchStatus status =
            BatchStatus.PLANNED;

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

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(
            String batchCode
    ) {
        this.batchCode = batchCode;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(
            Course course
    ) {
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(
            Teacher teacher
    ) {
        this.teacher = teacher;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(
            LocalDate startDate
    ) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(
            LocalDate endDate
    ) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(
            LocalTime startTime
    ) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(
            LocalTime endTime
    ) {
        this.endTime = endTime;
    }

    public Set<BatchDay> getDays() {
        return days;
    }

    public void setDays(
            Set<BatchDay> days
    ) {
        this.days = days;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(
            String room
    ) {
        this.room = room;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(
            Integer capacity
    ) {
        this.capacity = capacity;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public void setStatus(
            BatchStatus status
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
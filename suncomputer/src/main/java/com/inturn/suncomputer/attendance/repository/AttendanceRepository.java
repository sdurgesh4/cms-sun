package com.inturn.suncomputer.attendance.repository;

import com.inturn.suncomputer.attendance.entity.Attendance;
import com.inturn.suncomputer.attendance.entity.AttendanceStatus;

import com.inturn.suncomputer.attendance.repository.projection.AttendanceReportProjection;
import com.inturn.suncomputer.attendance.repository.projection.DailyAttendanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {


    /*
     * Find attendance for a particular
     * enrollment and date.
     */

    Optional<Attendance>
    findByEnrollmentIdAndAttendanceDate(
            Long enrollmentId,
            LocalDate attendanceDate
    );


    /*
     * Check whether attendance already
     * exists.
     */

    boolean
    existsByEnrollmentIdAndAttendanceDate(
            Long enrollmentId,
            LocalDate attendanceDate
    );


    /*
     * Get student's attendance history.
     */

    List<Attendance>
    findByEnrollmentIdOrderByAttendanceDateDesc(
            Long enrollmentId
    );


    /*
     * Get attendance between two dates.
     */

    List<Attendance>
    findByEnrollmentIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            Long enrollmentId,
            LocalDate startDate,
            LocalDate endDate
    );


    /*
     * Get attendance for a particular date.
     *
     * Useful for taking batch attendance.
     */

    List<Attendance>
    findByAttendanceDate(
            LocalDate attendanceDate
    );


    /*
     * Count attendance by status.
     */

    long countByEnrollmentIdAndStatus(
            Long enrollmentId,
            AttendanceStatus status
    );


    /*
     * Count attendance between dates.
     */

    long countByEnrollmentIdAndAttendanceDateBetween(
            Long enrollmentId,
            LocalDate startDate,
            LocalDate endDate
    );


    /*
     * Count present days.
     */

    long countByEnrollmentIdAndStatusAndAttendanceDateBetween(
            Long enrollmentId,
            AttendanceStatus status,
            LocalDate startDate,
            LocalDate endDate
    );


    @Query("""
    SELECT
        e.id AS enrollmentId,
        s.id AS studentId,
        s.studentCode AS studentCode,

        CONCAT(
            u.firstName,
            ' ',
            u.lastName
        ) AS studentName,

        b.id AS batchId,
        b.batchCode AS batchCode,

        COUNT(a.id) AS totalDays,

        SUM(
            CASE
                WHEN a.status = com.inturn.suncomputer.attendance.entity.AttendanceStatus.PRESENT
                THEN 1
                ELSE 0
            END
        ) AS presentDays,

        SUM(
            CASE
                WHEN a.status = com.inturn.suncomputer.attendance.entity.AttendanceStatus.ABSENT
                THEN 1
                ELSE 0
            END
        ) AS absentDays,

        SUM(
            CASE
                WHEN a.status = com.inturn.suncomputer.attendance.entity.AttendanceStatus.LATE
                THEN 1
                ELSE 0
            END
        ) AS lateDays,

        SUM(
            CASE
                WHEN a.status = com.inturn.suncomputer.attendance.entity.AttendanceStatus.LEAVE
                THEN 1
                ELSE 0
            END
        ) AS leaveDays

    FROM Attendance a

    JOIN a.enrollment e
    JOIN e.student s
    JOIN s.user u
    JOIN e.batch b

    WHERE
        e.batch.id = :batchId
        AND e.status =
            com.inturn.suncomputer.enrollment.entity.EnrollmentStatus.ACTIVE

        AND a.attendanceDate
            BETWEEN :startDate AND :endDate

    GROUP BY
        e.id,
        s.id,
        s.studentCode,
        u.firstName,
        u.lastName,
        b.id,
        b.batchCode

    ORDER BY
        s.studentCode
""")
    List<AttendanceReportProjection> getBatchAttendanceReport(
            @Param("batchId")
            Long batchId,

            @Param("startDate")
            LocalDate startDate,

            @Param("endDate")
            LocalDate endDate
    );



    @Query("""
    SELECT
        a.attendanceDate AS attendanceDate,
        a.status AS status,
        COUNT(a.id) AS studentCount

    FROM Attendance a

    JOIN a.enrollment e

    WHERE
        e.batch.id = :batchId

        AND e.status =
            com.inturn.suncomputer.enrollment.entity.EnrollmentStatus.ACTIVE

        AND a.attendanceDate
            BETWEEN :startDate AND :endDate

    GROUP BY
        a.attendanceDate,
        a.status

    ORDER BY
        a.attendanceDate
""")
    List<DailyAttendanceProjection> getDailyAttendanceReport(
            @Param("batchId")
            Long batchId,

            @Param("startDate")
            LocalDate startDate,

            @Param("endDate")
            LocalDate endDate
    );


}
package com.inturn.suncomputer.attendance.service;

import com.inturn.suncomputer.attendance.dto.*;

import com.inturn.suncomputer.attendance.entity.Attendance;
import com.inturn.suncomputer.attendance.entity.AttendanceStatus;

import com.inturn.suncomputer.attendance.repository.AttendanceRepository;

import com.inturn.suncomputer.attendance.repository.projection.AttendanceReportProjection;
import com.inturn.suncomputer.attendance.repository.projection.DailyAttendanceProjection;
import com.inturn.suncomputer.batch.entity.Batch;
import com.inturn.suncomputer.batch.repository.BatchRepository;
import com.inturn.suncomputer.common.exception.BusinessRuleException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;

import com.inturn.suncomputer.enrollment.entity.Enrollment;
import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;
import com.inturn.suncomputer.enrollment.repository.EnrollmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AttendanceServiceImpl
        implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    private final EnrollmentRepository enrollmentRepository;
    private final BatchRepository batchRepository;


    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            EnrollmentRepository enrollmentRepository,
            BatchRepository batchRepository
    ) {

        this.attendanceRepository =
                attendanceRepository;

        this.enrollmentRepository =
                enrollmentRepository;

        this.batchRepository =
                batchRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public List<AttendanceReportResponse> getBatchAttendanceReport(
            Long batchId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate.isAfter(endDate)) {

            throw new BusinessRuleException(
                    "Start date cannot be after end date"
            );
        }


        /*
         * Verify batch exists.
         */

        batchRepository
                .findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch not found with id: "
                                        + batchId
                        )
                );


        List<AttendanceReportProjection> projections =
                attendanceRepository
                        .getBatchAttendanceReport(
                                batchId,
                                startDate,
                                endDate
                        );


        List<AttendanceReportResponse> result =
                new java.util.ArrayList<>();


        for (
                AttendanceReportProjection projection :
                projections
        ) {

            long total =
                    projection.getTotalDays() == null
                            ? 0
                            : projection.getTotalDays();

            long present =
                    projection.getPresentDays() == null
                            ? 0
                            : projection.getPresentDays();

            long absent =
                    projection.getAbsentDays() == null
                            ? 0
                            : projection.getAbsentDays();

            long late =
                    projection.getLateDays() == null
                            ? 0
                            : projection.getLateDays();

            long leave =
                    projection.getLeaveDays() == null
                            ? 0
                            : projection.getLeaveDays();


            long attended =
                    present + late;


            double percentage =
                    total == 0
                            ? 0
                            : attended * 100.0 / total;


            percentage =
                    Math.round(
                            percentage * 100.0
                    ) / 100.0;


            result.add(
                    new AttendanceReportResponse(

                            projection.getEnrollmentId(),

                            projection.getStudentId(),

                            projection.getStudentCode(),

                            projection.getStudentName(),

                            projection.getBatchId(),

                            projection.getBatchCode(),

                            total,

                            present,

                            absent,

                            late,

                            leave,

                            attended,

                            percentage
                    )
            );
        }


        return result;
    }




    @Override
    @Transactional(readOnly = true)
    public List<DailyAttendanceReportResponse>
    getDailyAttendanceReport(
            Long batchId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate.isAfter(endDate)) {

            throw new BusinessRuleException(
                    "Start date cannot be after end date"
            );
        }


        batchRepository
                .findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch not found with id: "
                                        + batchId
                        )
                );


        List<DailyAttendanceProjection> projections =
                attendanceRepository
                        .getDailyAttendanceReport(
                                batchId,
                                startDate,
                                endDate
                        );


        return projections
                .stream()
                .map(
                        projection ->
                                new DailyAttendanceReportResponse(
                                        projection.getAttendanceDate(),
                                        projection.getStatus(),
                                        projection.getStudentCount()
                                )
                )
                .toList();
    }




    @Override
    @Transactional(readOnly = true)
    public List<BatchAttendanceResponse> getBatchAttendance(
            Long batchId,
            LocalDate attendanceDate
    ) {

        /*
         * Verify batch.
         */

        Batch batch =
                batchRepository
                        .findById(batchId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Batch not found with id: "
                                                + batchId
                                )
                        );


        /*
         * Get all active enrollments
         * belonging to this batch.
         */

        List<Enrollment> enrollments =
                enrollmentRepository
                        .findByBatchIdAndStatus(
                                batchId,
                                EnrollmentStatus.ACTIVE
                        );


        List<BatchAttendanceResponse> responses =
                new java.util.ArrayList<>();


        /*
         * Build attendance sheet.
         */

        for (Enrollment enrollment : enrollments) {

            Attendance attendance =
                    attendanceRepository
                            .findByEnrollmentIdAndAttendanceDate(
                                    enrollment.getId(),
                                    attendanceDate
                            )
                            .orElse(null);


            /*
             * Get student information.
             */

            var student =
                    enrollment.getStudent();

            var user =
                    student.getUser();


            String studentName =
                    user.getFirstName()
                            + " "
                            + user.getLastName();


            /*
             * If attendance exists,
             * mark = true.
             *
             * Otherwise:
             * status = null
             * marked = false
             */

            if (attendance != null) {

                responses.add(
                        new BatchAttendanceResponse(

                                enrollment.getId(),

                                student.getId(),

                                student.getStudentCode(),

                                studentName,

                                batch.getId(),

                                batch.getBatchCode(),

                                attendanceDate,

                                attendance.getStatus(),

                                attendance.getRemarks(),

                                true
                        )
                );

            } else {

                responses.add(
                        new BatchAttendanceResponse(

                                enrollment.getId(),

                                student.getId(),

                                student.getStudentCode(),

                                studentName,

                                batch.getId(),

                                batch.getBatchCode(),

                                attendanceDate,

                                null,

                                null,

                                false
                        )
                );
            }
        }


        return responses;
    }





    @Override
    @Transactional(readOnly = true)
    public StudentAttendanceDashboardResponse
    getStudentAttendanceDashboard(
            Long enrollmentId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        Enrollment enrollment =
                enrollmentRepository
                        .findById(enrollmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Enrollment not found with id: "
                                                + enrollmentId
                                )
                        );

        if (startDate.isAfter(endDate)) {

            throw new BusinessRuleException(
                    "Start date cannot be after end date"
            );
        }

        long totalDays =
                attendanceRepository
                        .countByEnrollmentIdAndAttendanceDateBetween(
                                enrollmentId,
                                startDate,
                                endDate
                        );

        long presentDays =
                attendanceRepository
                        .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                enrollmentId,
                                AttendanceStatus.PRESENT,
                                startDate,
                                endDate
                        );

        long absentDays =
                attendanceRepository
                        .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                enrollmentId,
                                AttendanceStatus.ABSENT,
                                startDate,
                                endDate
                        );

        long lateDays =
                attendanceRepository
                        .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                enrollmentId,
                                AttendanceStatus.LATE,
                                startDate,
                                endDate
                        );

        long leaveDays =
                attendanceRepository
                        .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                enrollmentId,
                                AttendanceStatus.LEAVE,
                                startDate,
                                endDate
                        );

        long attendedDays =
                presentDays + lateDays;

        double percentage =
                totalDays == 0
                        ? 0
                        : attendedDays * 100.0 / totalDays;

        percentage =
                Math.round(percentage * 100.0) / 100.0;

        var student =
                enrollment.getStudent();

        var user =
                student.getUser();

        String studentName =
                user.getFirstName()
                        + " "
                        + user.getLastName();

        var batch =
                enrollment.getBatch();

        return new StudentAttendanceDashboardResponse(

                enrollment.getId(),

                student.getId(),

                student.getStudentCode(),

                studentName,

                batch.getId(),

                batch.getBatchCode(),

                totalDays,

                presentDays,

                absentDays,

                lateDays,

                leaveDays,

                percentage
        );
    }




    @Override
    @Transactional(readOnly = true)
    public BatchAttendanceDashboardResponse
    getBatchAttendanceDashboard(
            Long batchId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        Batch batch =
                batchRepository
                        .findById(batchId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Batch not found with id: "
                                                + batchId
                                )
                        );

        if (startDate.isAfter(endDate)) {

            throw new BusinessRuleException(
                    "Start date cannot be after end date"
            );
        }

        List<Enrollment> enrollments =
                enrollmentRepository
                        .findByBatchIdAndStatus(
                                batchId,
                                EnrollmentStatus.ACTIVE
                        );

        long totalPresent = 0;
        long totalAbsent = 0;
        long totalLate = 0;
        long totalLeave = 0;

        double percentageTotal = 0;

        for (Enrollment enrollment : enrollments) {

            Long enrollmentId =
                    enrollment.getId();

            long present =
                    attendanceRepository
                            .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                    enrollmentId,
                                    AttendanceStatus.PRESENT,
                                    startDate,
                                    endDate
                            );

            long absent =
                    attendanceRepository
                            .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                    enrollmentId,
                                    AttendanceStatus.ABSENT,
                                    startDate,
                                    endDate
                            );

            long late =
                    attendanceRepository
                            .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                    enrollmentId,
                                    AttendanceStatus.LATE,
                                    startDate,
                                    endDate
                            );

            long leave =
                    attendanceRepository
                            .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                    enrollmentId,
                                    AttendanceStatus.LEAVE,
                                    startDate,
                                    endDate
                            );

            long total =
                    present
                            + absent
                            + late
                            + leave;

            long attended =
                    present + late;

            double studentPercentage =
                    total == 0
                            ? 0
                            : attended * 100.0 / total;

            percentageTotal += studentPercentage;

            totalPresent += present;
            totalAbsent += absent;
            totalLate += late;
            totalLeave += leave;
        }

        double averagePercentage =
                enrollments.isEmpty()
                        ? 0
                        : percentageTotal
                          / enrollments.size();

        averagePercentage =
                Math.round(
                        averagePercentage * 100.0
                ) / 100.0;

        return new BatchAttendanceDashboardResponse(

                batch.getId(),

                batch.getBatchCode(),

                enrollments.size(),

                totalPresent,

                totalAbsent,

                totalLate,

                totalLeave,

                averagePercentage
        );
    }





    @Override
    @Transactional(readOnly = true)
    public List<LowAttendanceStudentResponse>
    getLowAttendanceStudents(
            Long batchId,
            LocalDate startDate,
            LocalDate endDate,
            double threshold
    ) {

        if (startDate.isAfter(endDate)) {

            throw new BusinessRuleException(
                    "Start date cannot be after end date"
            );
        }

        if (
                threshold < 0
                        || threshold > 100
        ) {

            throw new BusinessRuleException(
                    "Attendance threshold must be between 0 and 100"
            );
        }

        Batch batch =
                batchRepository
                        .findById(batchId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Batch not found with id: "
                                                + batchId
                                )
                        );

        List<Enrollment> enrollments =
                enrollmentRepository
                        .findByBatchIdAndStatus(
                                batchId,
                                EnrollmentStatus.ACTIVE
                        );

        List<LowAttendanceStudentResponse> result =
                new java.util.ArrayList<>();

        for (Enrollment enrollment : enrollments) {

            long total =
                    attendanceRepository
                            .countByEnrollmentIdAndAttendanceDateBetween(
                                    enrollment.getId(),
                                    startDate,
                                    endDate
                            );

            if (total == 0) {
                continue;
            }

            long present =
                    attendanceRepository
                            .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                    enrollment.getId(),
                                    AttendanceStatus.PRESENT,
                                    startDate,
                                    endDate
                            );

            long late =
                    attendanceRepository
                            .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                    enrollment.getId(),
                                    AttendanceStatus.LATE,
                                    startDate,
                                    endDate
                            );

            long attended =
                    present + late;

            double percentage =
                    attended * 100.0 / total;

            percentage =
                    Math.round(
                            percentage * 100.0
                    ) / 100.0;

            if (percentage < threshold) {

                var student =
                        enrollment.getStudent();

                var user =
                        student.getUser();

                String studentName =
                        user.getFirstName()
                                + " "
                                + user.getLastName();

                result.add(
                        new LowAttendanceStudentResponse(

                                enrollment.getId(),

                                student.getId(),

                                student.getStudentCode(),

                                studentName,

                                batch.getId(),

                                batch.getBatchCode(),

                                total,

                                attended,

                                percentage
                        )
                );
            }
        }

        result.sort(
                java.util.Comparator.comparingDouble(
                        LowAttendanceStudentResponse
                                ::attendancePercentage
                )
        );

        return result;
    }



    // =========================================================
    // CREATE ATTENDANCE
    // =========================================================

    @Override
    public AttendanceResponse createAttendance(
            CreateAttendanceRequest request
    ) {

        /*
         * Find enrollment.
         */

        Enrollment enrollment =
                enrollmentRepository
                        .findById(
                                request.enrollmentId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Enrollment not found with id: "
                                                + request.enrollmentId()
                                )
                        );


        /*
         * Only active enrollments should
         * receive attendance.
         */

        if (
                enrollment.getStatus()
                        != EnrollmentStatus.ACTIVE
        ) {

            throw new BusinessRuleException(
                    "Attendance can only be marked for an active enrollment"
            );
        }


        /*
         * Do not allow future attendance.
         */

        if (
                request.attendanceDate()
                        .isAfter(
                                LocalDate.now()
                        )
        ) {

            throw new BusinessRuleException(
                    "Attendance date cannot be in the future"
            );
        }


        /*
         * Prevent duplicate attendance.
         */

        if (
                attendanceRepository
                        .existsByEnrollmentIdAndAttendanceDate(
                                request.enrollmentId(),
                                request.attendanceDate()
                        )
        ) {

            throw new BusinessRuleException(
                    "Attendance already exists for this student on "
                            + request.attendanceDate()
            );
        }


        /*
         * Create entity.
         */

        Attendance attendance =
                new Attendance();

        attendance.setEnrollment(
                enrollment
        );

        attendance.setAttendanceDate(
                request.attendanceDate()
        );

        attendance.setStatus(
                request.status()
        );

        attendance.setRemarks(
                request.remarks()
        );


        /*
         * Save.
         */

        Attendance saved =
                attendanceRepository.save(
                        attendance
                );


        return mapToResponse(
                saved
        );
    }
    @Override
    public List<AttendanceResponse> createBatchAttendance(
            BatchAttendanceRequest request
    ) {

        /*
         * Validate batch.
         */

        Batch batch =
                batchRepository
                        .findById(request.batchId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Batch not found with id: "
                                                + request.batchId()
                                )
                        );


        /*
         * Future attendance is not allowed.
         */

        if (
                request.attendanceDate()
                        .isAfter(LocalDate.now())
        ) {

            throw new BusinessRuleException(
                    "Attendance date cannot be in the future"
            );
        }


        /*
         * Prevent duplicate enrollment IDs
         * in the same request.
         */

        long uniqueEnrollmentCount =
                request.records()
                        .stream()
                        .map(
                                BatchAttendanceRecordRequest
                                        ::enrollmentId
                        )
                        .distinct()
                        .count();

        if (
                uniqueEnrollmentCount
                        != request.records().size()
        ) {

            throw new BusinessRuleException(
                    "Duplicate enrollment found in attendance request"
            );
        }


        List<AttendanceResponse> responses =
                new java.util.ArrayList<>();


        /*
         * Process every enrollment.
         */

        for (
                BatchAttendanceRecordRequest record :
                request.records()
        ) {

            Enrollment enrollment =
                    enrollmentRepository
                            .findById(
                                    record.enrollmentId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Enrollment not found with id: "
                                                    + record.enrollmentId()
                                    )
                            );


            /*
             * Verify enrollment belongs to batch.
             */

            if (
                    enrollment.getBatch() == null
                            || enrollment.getBatch().getId() == null
                            || !enrollment.getBatch().getId()
                            .equals(batch.getId())
            ) {

                throw new BusinessRuleException(
                        "Enrollment "
                                + enrollment.getId()
                                + " does not belong to batch "
                                + batch.getId()
                );
            }


            /*
             * Only active enrollments can
             * have attendance.
             */

            if (
                    enrollment.getStatus()
                            != EnrollmentStatus.ACTIVE
            ) {

                throw new BusinessRuleException(
                        "Enrollment "
                                + enrollment.getId()
                                + " is not active"
                );
            }


            /*
             * Check whether attendance already exists.
             */

            Attendance attendance =
                    attendanceRepository
                            .findByEnrollmentIdAndAttendanceDate(
                                    enrollment.getId(),
                                    request.attendanceDate()
                            )
                            .orElse(null);


            /*
             * CREATE
             */

            if (attendance == null) {

                attendance =
                        new Attendance();

                attendance.setEnrollment(
                        enrollment
                );

                attendance.setAttendanceDate(
                        request.attendanceDate()
                );
            }


            /*
             * CREATE or UPDATE status.
             */

            attendance.setStatus(
                    record.status()
            );


            /*
             * CREATE or UPDATE remarks.
             */

            attendance.setRemarks(
                    record.remarks()
            );


            /*
             * Save.
             */

            Attendance saved =
                    attendanceRepository.save(
                            attendance
                    );


            responses.add(
                    mapToResponse(
                            saved
                    )
            );
        }


        return responses;
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(
            Long id
    ) {

        Attendance attendance =
                attendanceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(
                attendance
        );
    }


    // =========================================================
    // GET STUDENT ATTENDANCE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse>
    getAttendanceByEnrollment(
            Long enrollmentId
    ) {

        if (
                !enrollmentRepository
                        .existsById(
                                enrollmentId
                        )
        ) {

            throw new ResourceNotFoundException(
                    "Enrollment not found with id: "
                            + enrollmentId
            );
        }


        return attendanceRepository
                .findByEnrollmentIdOrderByAttendanceDateDesc(
                        enrollmentId
                )
                .stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }


    // =========================================================
    // GET BETWEEN DATES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse>
    getAttendanceBetweenDates(
            Long enrollmentId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (
                !enrollmentRepository
                        .existsById(
                                enrollmentId
                        )
        ) {

            throw new ResourceNotFoundException(
                    "Enrollment not found with id: "
                            + enrollmentId
            );
        }


        if (
                startDate.isAfter(endDate)
        ) {

            throw new BusinessRuleException(
                    "Start date cannot be after end date"
            );
        }


        return attendanceRepository
                .findByEnrollmentIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
                        enrollmentId,
                        startDate,
                        endDate
                )
                .stream()
                .map(
                        this::mapToResponse
                )
                .toList();
    }


    // =========================================================
    // ATTENDANCE SUMMARY
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryResponse
    getAttendanceSummary(
            Long enrollmentId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (
                !enrollmentRepository
                        .existsById(
                                enrollmentId
                        )
        ) {

            throw new ResourceNotFoundException(
                    "Enrollment not found with id: "
                            + enrollmentId
            );
        }


        if (
                startDate.isAfter(endDate)
        ) {

            throw new BusinessRuleException(
                    "Start date cannot be after end date"
            );
        }


        /*
         * Total attendance records.
         */

        long totalDays =
                attendanceRepository
                        .countByEnrollmentIdAndAttendanceDateBetween(
                                enrollmentId,
                                startDate,
                                endDate
                        );


        /*
         * Present.
         */

        long presentDays =
                attendanceRepository
                        .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                enrollmentId,
                                AttendanceStatus.PRESENT,
                                startDate,
                                endDate
                        );


        /*
         * Absent.
         */

        long absentDays =
                attendanceRepository
                        .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                enrollmentId,
                                AttendanceStatus.ABSENT,
                                startDate,
                                endDate
                        );


        /*
         * Late.
         */

        long lateDays =
                attendanceRepository
                        .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                enrollmentId,
                                AttendanceStatus.LATE,
                                startDate,
                                endDate
                        );


        /*
         * Leave.
         */

        long leaveDays =
                attendanceRepository
                        .countByEnrollmentIdAndStatusAndAttendanceDateBetween(
                                enrollmentId,
                                AttendanceStatus.LEAVE,
                                startDate,
                                endDate
                        );


        /*
         * PRESENT + LATE count as attended.
         */

        long attendedDays =
                presentDays
                        + lateDays;


        /*
         * Avoid division by zero.
         */

        double attendancePercentage =
                totalDays == 0
                        ? 0.0
                        : (
                        attendedDays * 100.0
                        / totalDays
                );


        /*
         * Round to two decimal places.
         */

        attendancePercentage =
                Math.round(
                        attendancePercentage * 100.0
                ) / 100.0;


        return new AttendanceSummaryResponse(

                enrollmentId,

                totalDays,

                presentDays,

                absentDays,

                lateDays,

                leaveDays,

                attendancePercentage
        );
    }


    // =========================================================
    // MAP ENTITY TO RESPONSE
    // =========================================================

    private AttendanceResponse mapToResponse(
            Attendance attendance
    ) {

        Enrollment enrollment =
                attendance.getEnrollment();

        var student =
                enrollment.getStudent();

        var user =
                student.getUser();

        var batch =
                enrollment.getBatch();

        var course =
                batch.getCourse();


        String studentName =
                user.getFirstName()
                        + " "
                        + user.getLastName();


        return new AttendanceResponse(

                attendance.getId(),

                enrollment.getId(),

                student.getId(),

                student.getStudentCode(),

                studentName,

                batch.getBatchCode(),

                course.getName(),

                attendance.getAttendanceDate(),

                attendance.getStatus(),

                attendance.getRemarks(),

                attendance.getCreatedAt()
        );
    }
}
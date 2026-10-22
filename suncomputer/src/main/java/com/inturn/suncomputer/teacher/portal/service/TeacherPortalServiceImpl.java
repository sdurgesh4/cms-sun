package com.inturn.suncomputer.teacher.portal.service;

import com.inturn.suncomputer.attendance.dto.*;
import com.inturn.suncomputer.attendance.service.AttendanceService;
import com.inturn.suncomputer.batch.dto.BatchResponse;
import com.inturn.suncomputer.batch.entity.Batch;
import com.inturn.suncomputer.batch.repository.BatchRepository;
import com.inturn.suncomputer.common.exception.BusinessRuleException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;
import com.inturn.suncomputer.enrollment.entity.Enrollment;
import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;
import com.inturn.suncomputer.enrollment.repository.EnrollmentRepository;
import com.inturn.suncomputer.teacher.entity.Teacher;
import com.inturn.suncomputer.teacher.entity.TeacherStatus;
import com.inturn.suncomputer.teacher.repository.TeacherRepository;
import com.inturn.suncomputer.teacher.portal.dto.TeacherProfileResponse;
import com.inturn.suncomputer.teacher.portal.dto.TeacherStudentResponse;
import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TeacherPortalServiceImpl implements TeacherPortalService {
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final BatchRepository batchRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceService attendanceService;

    public TeacherPortalServiceImpl(TeacherRepository teacherRepository, UserRepository userRepository,
            BatchRepository batchRepository, EnrollmentRepository enrollmentRepository,
            AttendanceService attendanceService) {
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.batchRepository = batchRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceService = attendanceService;
    }

    @Override
    public TeacherProfileResponse getMyProfile(String username) {
        Teacher teacher = requireTeacher(username);
        User user = teacher.getUser();
        return new TeacherProfileResponse(teacher.getId(), user.getId(), user.getUsername(),
                user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(),
                teacher.getEmployeeCode(), teacher.getSpecialization(), teacher.getQualification(),
                teacher.getExperienceYears(), teacher.getJoiningDate(), teacher.getStatus());
    }

    @Override
    public List<BatchResponse> getMyBatches(String username) {
        Teacher teacher = requireTeacher(username);
        return batchRepository.findByTeacherId(teacher.getId()).stream().map(this::toBatchResponse).toList();
    }

    @Override
    public List<TeacherStudentResponse> getStudentsInBatch(String username, Long batchId) {
        requireOwnedBatch(username, batchId);
        return enrollmentRepository.findByBatchIdAndStatus(batchId, EnrollmentStatus.ACTIVE).stream()
                .map(this::toStudentResponse).toList();
    }

    @Override
    public List<BatchAttendanceResponse> getBatchAttendance(String username, Long batchId, LocalDate date) {
        requireOwnedBatch(username, batchId);
        return attendanceService.getBatchAttendance(batchId, date);
    }

    @Override
    @Transactional
    public List<AttendanceResponse> saveBatchAttendance(String username, BatchAttendanceRequest request) {
        requireOwnedBatch(username, request.batchId());
        return attendanceService.createBatchAttendance(request);
    }

    @Override
    public BatchAttendanceDashboardResponse getAttendanceDashboard(String username, Long batchId,
            LocalDate startDate, LocalDate endDate) {
        requireOwnedBatch(username, batchId);
        return attendanceService.getBatchAttendanceDashboard(batchId, startDate, endDate);
    }

    @Override
    public List<AttendanceReportResponse> getAttendanceReport(String username, Long batchId,
            LocalDate startDate, LocalDate endDate) {
        requireOwnedBatch(username, batchId);
        return attendanceService.getBatchAttendanceReport(batchId, startDate, endDate);
    }

    @Override
    public List<LowAttendanceStudentResponse> getLowAttendance(String username, Long batchId,
            LocalDate startDate, LocalDate endDate, double threshold) {
        requireOwnedBatch(username, batchId);
        if (threshold < 0 || threshold > 100) throw new BusinessRuleException("Threshold must be between 0 and 100");
        return attendanceService.getLowAttendanceStudents(batchId, startDate, endDate, threshold);
    }

    private Teacher requireTeacher(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Signed-in user was not found"));
        Teacher teacher = teacherRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile is not configured for this account"));
        if (!user.isEnabled()) throw new BusinessRuleException("This account is disabled");
        if (teacher.getStatus() != TeacherStatus.ACTIVE) throw new BusinessRuleException("Teacher account is not active");
        return teacher;
    }

    private Batch requireOwnedBatch(String username, Long batchId) {
        Teacher teacher = requireTeacher(username);
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id: " + batchId));
        if (batch.getTeacher() == null || !batch.getTeacher().getId().equals(teacher.getId())) {
            // Deliberately avoid revealing whether another teacher owns this batch.
            throw new ResourceNotFoundException("Assigned batch not found with id: " + batchId);
        }
        return batch;
    }

    private BatchResponse toBatchResponse(Batch batch) {
        var course = batch.getCourse();
        var teacher = batch.getTeacher();
        var teacherUser = teacher.getUser();
        return new BatchResponse(batch.getId(), batch.getBatchCode(), course.getId(), course.getCode(),
                course.getName(), teacher.getId(), teacher.getEmployeeCode(),
                (teacherUser.getFirstName() + " " + (teacherUser.getLastName() == null ? "" : teacherUser.getLastName())).trim(),
                batch.getStartDate(), batch.getEndDate(), batch.getStartTime(), batch.getEndTime(),
                batch.getDays(), batch.getRoom(), batch.getCapacity(), batch.getStatus(),
                batch.getCreatedAt(), batch.getUpdatedAt());
    }

    private TeacherStudentResponse toStudentResponse(Enrollment enrollment) {
        var student = enrollment.getStudent();
        var user = student.getUser();
        String fullName = (user.getFirstName() + " " + (user.getLastName() == null ? "" : user.getLastName())).trim();
        return new TeacherStudentResponse(enrollment.getId(), student.getId(), student.getStudentCode(),
                user.getFirstName(), user.getLastName(), fullName, user.getEmail(), user.getPhone(),
                enrollment.getStatus(), student.getStatus(), enrollment.getEnrollmentDate());
    }
}

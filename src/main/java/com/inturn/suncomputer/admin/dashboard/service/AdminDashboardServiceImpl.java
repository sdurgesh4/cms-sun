package com.inturn.suncomputer.admin.dashboard.service;

import com.inturn.suncomputer.admin.dashboard.dto.AdminDashboardResponse;
import com.inturn.suncomputer.admin.dashboard.dto.DashboardAttendanceResponse;
import com.inturn.suncomputer.admin.dashboard.dto.DashboardEnquiryResponse;
import com.inturn.suncomputer.admin.dashboard.dto.DashboardFeeResponse;
import com.inturn.suncomputer.admin.dashboard.dto.DashboardSummaryResponse;

import com.inturn.suncomputer.attendance.entity.AttendanceStatus;
import com.inturn.suncomputer.attendance.repository.AttendanceRepository;

import com.inturn.suncomputer.batch.entity.BatchStatus;
import com.inturn.suncomputer.batch.repository.BatchRepository;

import com.inturn.suncomputer.course.entity.CourseStatus;
import com.inturn.suncomputer.course.repository.CourseRepository;

import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;
import com.inturn.suncomputer.enquiry.repository.EnquiryRepository;

import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;
import com.inturn.suncomputer.enrollment.repository.EnrollmentRepository;

import com.inturn.suncomputer.installment.entity.InstallmentStatus;
import com.inturn.suncomputer.installment.repository.InstallmentRepository;

import com.inturn.suncomputer.notification.repository.NotificationRepository;

import com.inturn.suncomputer.payment.entity.PaymentStatus;
import com.inturn.suncomputer.payment.repository.PaymentRepository;

import com.inturn.suncomputer.student.entity.StudentStatus;
import com.inturn.suncomputer.student.repository.StudentRepository;

import com.inturn.suncomputer.teacher.entity.TeacherStatus;
import com.inturn.suncomputer.teacher.repository.TeacherRepository;

import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final CourseRepository courseRepository;

    private final BatchRepository batchRepository;

    private final TeacherRepository teacherRepository;

    private final StudentRepository studentRepository;

    private final EnrollmentRepository enrollmentRepository;

    private final PaymentRepository paymentRepository;

    private final InstallmentRepository installmentRepository;

    private final AttendanceRepository attendanceRepository;

    private final EnquiryRepository enquiryRepository;

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;


    public AdminDashboardServiceImpl(

            CourseRepository courseRepository,

            BatchRepository batchRepository,

            TeacherRepository teacherRepository,

            StudentRepository studentRepository,

            EnrollmentRepository enrollmentRepository,

            PaymentRepository paymentRepository,

            InstallmentRepository installmentRepository,

            AttendanceRepository attendanceRepository,

            EnquiryRepository enquiryRepository,

            NotificationRepository notificationRepository,

            UserRepository userRepository
    ) {

        this.courseRepository =
                courseRepository;

        this.batchRepository =
                batchRepository;

        this.teacherRepository =
                teacherRepository;

        this.studentRepository =
                studentRepository;

        this.enrollmentRepository =
                enrollmentRepository;

        this.paymentRepository =
                paymentRepository;

        this.installmentRepository =
                installmentRepository;

        this.attendanceRepository =
                attendanceRepository;

        this.enquiryRepository =
                enquiryRepository;

        this.notificationRepository =
                notificationRepository;

        this.userRepository =
                userRepository;
    }


    @Override
    public AdminDashboardResponse getDashboard(
            String username
    ) {

        LocalDate today =
                LocalDate.now();

        DashboardSummaryResponse summary =
                buildSummary();

        DashboardFeeResponse fees =
                buildFees(today);

        DashboardAttendanceResponse attendance =
                buildAttendance(today);

        DashboardEnquiryResponse enquiries =
                buildEnquiries(today);

        long unreadNotifications =
                getUnreadNotifications(username);


        return new AdminDashboardResponse(

                LocalDateTime.now(),

                today,

                summary,

                fees,

                attendance,

                enquiries,

                unreadNotifications
        );
    }


    private DashboardSummaryResponse buildSummary() {

        return new DashboardSummaryResponse(

                studentRepository.count(),

                studentRepository.countByStatus(
                        StudentStatus.ACTIVE
                ),


                teacherRepository.count(),

                teacherRepository.countByStatus(
                        TeacherStatus.ACTIVE
                ),


                courseRepository.count(),

                courseRepository.countByStatus(
                        CourseStatus.ACTIVE
                ),


                batchRepository.count(),

                batchRepository.countByStatus(
                        BatchStatus.ACTIVE
                ),


                enrollmentRepository.count(),

                enrollmentRepository.countByStatus(
                        EnrollmentStatus.ACTIVE
                )
        );
    }


    private DashboardFeeResponse buildFees(
            LocalDate today
    ) {

        BigDecimal totalFees =
                enrollmentRepository
                        .getTotalFinalFeeByStatus(
                                EnrollmentStatus.ACTIVE
                        );


        BigDecimal collectedFees =
                paymentRepository
                        .getTotalAmountByStatus(
                                PaymentStatus.SUCCESS
                        );


        BigDecimal pendingFees =
                totalFees
                        .subtract(collectedFees)
                        .max(BigDecimal.ZERO);


        BigDecimal todayCollection =
                paymentRepository
                        .getTotalAmountByStatusAndPaymentDate(
                                PaymentStatus.SUCCESS,
                                today
                        );


        BigDecimal overdueAmount =
                installmentRepository
                        .getTotalAmountByStatus(
                                InstallmentStatus.OVERDUE
                        );


        long overdueCount =
                installmentRepository
                        .countByStatus(
                                InstallmentStatus.OVERDUE
                        );


        return new DashboardFeeResponse(

                totalFees,

                collectedFees,

                pendingFees,

                todayCollection,

                overdueAmount,

                overdueCount
        );
    }


    private DashboardAttendanceResponse buildAttendance(
            LocalDate today
    ) {

        long totalMarked =
                attendanceRepository
                        .countByAttendanceDate(
                                today
                        );


        long present =
                attendanceRepository
                        .countByAttendanceDateAndStatus(
                                today,
                                AttendanceStatus.PRESENT
                        );


        long absent =
                attendanceRepository
                        .countByAttendanceDateAndStatus(
                                today,
                                AttendanceStatus.ABSENT
                        );


        long late =
                attendanceRepository
                        .countByAttendanceDateAndStatus(
                                today,
                                AttendanceStatus.LATE
                        );


        long leave =
                attendanceRepository
                        .countByAttendanceDateAndStatus(
                                today,
                                AttendanceStatus.LEAVE
                        );


        BigDecimal attendancePercentage =
                calculateAttendancePercentage(
                        totalMarked,
                        present + late
                );


        return new DashboardAttendanceResponse(

                totalMarked,

                present,

                absent,

                late,

                leave,

                attendancePercentage
        );
    }


    private DashboardEnquiryResponse buildEnquiries(
            LocalDate today
    ) {

        long total =
                enquiryRepository.count();


        long pending =
                enquiryRepository.countByStatusIn(

                        List.of(

                                EnquiryStatus.NEW,

                                EnquiryStatus.CONTACTED,

                                EnquiryStatus.INTERESTED,

                                EnquiryStatus.FOLLOW_UP
                        )
                );


        long converted =
                enquiryRepository.countByStatus(
                        EnquiryStatus.CONVERTED
                );


        long todayFollowUps =
                enquiryRepository
                        .countByNextFollowUpDate(
                                today
                        );


        return new DashboardEnquiryResponse(

                total,

                pending,

                converted,

                todayFollowUps
        );
    }


    private long getUnreadNotifications(
            String username
    ) {

        if (
                username == null
                        || username.isBlank()
        ) {

            return 0;
        }


        User user =
                userRepository
                        .findByUsername(username)
                        .orElse(null);


        if (user == null) {

            return 0;
        }


        return notificationRepository
                .countByRecipientIdAndReadFalse(
                        user.getId()
                );
    }


    private BigDecimal calculateAttendancePercentage(

            long total,

            long attended
    ) {

        if (total == 0) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }


        return BigDecimal.valueOf(attended)

                .multiply(
                        BigDecimal.valueOf(100)
                )

                .divide(
                        BigDecimal.valueOf(total),
                        2,
                        RoundingMode.HALF_UP
                );
    }
}
package com.inturn.suncomputer.student.portal;

import com.inturn.suncomputer.attendance.entity.Attendance;
import com.inturn.suncomputer.attendance.repository.AttendanceRepository;
import com.inturn.suncomputer.enrollment.entity.Enrollment;
import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;
import com.inturn.suncomputer.enrollment.repository.EnrollmentRepository;
import com.inturn.suncomputer.installment.entity.Installment;
import com.inturn.suncomputer.installment.repository.InstallmentRepository;
import com.inturn.suncomputer.payment.entity.Payment;
import com.inturn.suncomputer.payment.entity.PaymentStatus;
import com.inturn.suncomputer.payment.repository.PaymentRepository;
import com.inturn.suncomputer.student.entity.Student;
import com.inturn.suncomputer.student.repository.StudentRepository;
import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/student")
public class StudentPortalController {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;
    private final InstallmentRepository installmentRepository;

    public StudentPortalController(UserRepository userRepository,
                                   StudentRepository studentRepository,
                                   EnrollmentRepository enrollmentRepository,
                                   AttendanceRepository attendanceRepository,
                                   PaymentRepository paymentRepository,
                                   InstallmentRepository installmentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
        this.installmentRepository = installmentRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        Student student = currentStudent(authentication);
        User user = student.getUser();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", student.getId());
        result.put("studentCode", student.getStudentCode());
        result.put("username", user.getUsername());
        result.put("firstName", user.getFirstName());
        result.put("lastName", user.getLastName());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());
        result.put("status", student.getStatus());
        result.put("admissionDate", student.getAdmissionDate());
        result.put("dateOfBirth", student.getDateOfBirth());
        result.put("gender", student.getGender());
        return result;
    }

    @Transactional(readOnly = true)
    @GetMapping("/courses")
    public List<Map<String, Object>> courses(Authentication authentication) {
        Student student = currentStudent(authentication);
        return enrollmentRepository.findByStudentId(student.getId()).stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE || e.getStatus() == EnrollmentStatus.COMPLETED)
                .sorted(Comparator.comparing(Enrollment::getEnrollmentDate).reversed())
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    var batch = e.getBatch();
                    var course = batch.getCourse();
                    row.put("enrollmentId", e.getId());
                    row.put("status", e.getStatus());
                    row.put("enrollmentDate", e.getEnrollmentDate());
                    row.put("agreedFee", e.getAgreedFee());
                    row.put("discount", e.getDiscount());
                    row.put("finalFee", e.getFinalFee());
                    row.put("batchId", batch.getId());
                    row.put("batchCode", batch.getBatchCode());
                    row.put("batchStatus", batch.getStatus());
                    row.put("startDate", batch.getStartDate());
                    row.put("endDate", batch.getEndDate());
                    row.put("startTime", batch.getStartTime());
                    row.put("endTime", batch.getEndTime());
                    row.put("days", batch.getDays());
                    row.put("room", batch.getRoom());
                    row.put("courseId", course.getId());
                    row.put("courseCode", course.getCode());
                    row.put("courseName", course.getName());
                    row.put("courseDescription", course.getDescription());
                    row.put("duration", course.getDuration());
                    row.put("durationUnit", course.getDurationUnit());
                    row.put("courseLevel", course.getLevel());
                    return row;
                }).toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/attendance")
    public Map<String, Object> attendance(Authentication authentication,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Student student = currentStudent(authentication);
        LocalDate from = startDate == null ? LocalDate.now().minusMonths(6) : startDate;
        LocalDate to = endDate == null ? LocalDate.now() : endDate;
        if (from.isAfter(to)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate must be on or before endDate");
        List<Map<String, Object>> records = new ArrayList<>();
        long total = 0, present = 0, absent = 0, late = 0, leave = 0;
        for (Enrollment e : enrollmentRepository.findByStudentId(student.getId())) {
            if (e.getStatus() != EnrollmentStatus.ACTIVE && e.getStatus() != EnrollmentStatus.COMPLETED) continue;
            for (Attendance a : attendanceRepository.findByEnrollmentIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(e.getId(), from, to)) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", a.getId()); row.put("enrollmentId", e.getId());
                row.put("attendanceDate", a.getAttendanceDate()); row.put("status", a.getStatus());
                row.put("remarks", a.getRemarks()); row.put("batchCode", e.getBatch().getBatchCode());
                row.put("courseName", e.getBatch().getCourse().getName());
                records.add(row); total++;
                switch (a.getStatus()) { case PRESENT -> present++; case ABSENT -> absent++; case LATE -> late++; case LEAVE -> leave++; }
            }
        }
        records.sort(Comparator.comparing(m -> (LocalDate)m.get("attendanceDate"), Comparator.reverseOrder()));
        double percentage = total == 0 ? 0.0 : Math.round(((present + late) * 10000.0 / total)) / 100.0;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("startDate", from); result.put("endDate", to); result.put("totalDays", total);
        result.put("presentDays", present); result.put("absentDays", absent); result.put("lateDays", late);
        result.put("leaveDays", leave); result.put("attendancePercentage", percentage); result.put("records", records);
        return result;
    }

    @Transactional(readOnly = true)
    @GetMapping("/fees")
    public List<Map<String, Object>> fees(Authentication authentication) {
        Student student = currentStudent(authentication);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Enrollment e : enrollmentRepository.findByStudentId(student.getId())) {
            if (e.getStatus() == EnrollmentStatus.CANCELLED || e.getStatus() == EnrollmentStatus.TRANSFERRED) continue;
            List<Payment> payments = paymentRepository.findByEnrollmentId(e.getId()).stream()
                    .sorted(Comparator.comparing(Payment::getPaymentDate).reversed()).toList();
            BigDecimal paid = payments.stream().filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                    .map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal fee = e.getFinalFee() == null ? BigDecimal.ZERO : e.getFinalFee();
            BigDecimal outstanding = fee.subtract(paid).max(BigDecimal.ZERO);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("enrollmentId", e.getId()); row.put("courseName", e.getBatch().getCourse().getName());
            row.put("batchCode", e.getBatch().getBatchCode()); row.put("status", e.getStatus());
            row.put("totalFee", fee); row.put("totalPaid", paid); row.put("outstanding", outstanding);
            row.put("fullyPaid", outstanding.compareTo(BigDecimal.ZERO) == 0);
            row.put("payments", payments.stream().map(p -> {
                Map<String, Object> item = new LinkedHashMap<>(); item.put("id", p.getId());
                item.put("receiptNumber", p.getReceiptNumber()); item.put("paymentDate", p.getPaymentDate());
                item.put("amount", p.getAmount()); item.put("paymentMethod", p.getPaymentMethod());
                item.put("status", p.getStatus()); return item;
            }).toList());
            row.put("installments", installmentRepository.findByEnrollmentIdOrderByInstallmentNumberAsc(e.getId()).stream().map(i -> {
                BigDecimal installmentPaid = paymentRepository.findByInstallmentId(i.getId()).stream()
                        .filter(p -> p.getStatus() == PaymentStatus.SUCCESS).map(Payment::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                Map<String, Object> item = new LinkedHashMap<>(); item.put("id", i.getId());
                item.put("installmentNumber", i.getInstallmentNumber()); item.put("dueDate", i.getDueDate());
                item.put("amount", i.getAmount()); item.put("paidAmount", installmentPaid);
                item.put("outstandingAmount", i.getAmount().subtract(installmentPaid).max(BigDecimal.ZERO));
                item.put("status", i.getStatus()); return item;
            }).toList());
            result.add(row);
        }
        return result;
    }

    private Student currentStudent(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User account not found"));
        return studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No student profile is linked to this account. Ask an administrator to link your student record."));
    }
}

package com.inturn.suncomputer.enquiry.service;

import com.inturn.suncomputer.batch.entity.Batch;
import com.inturn.suncomputer.batch.repository.BatchRepository;
import com.inturn.suncomputer.enrollment.entity.Enrollment;
import com.inturn.suncomputer.enrollment.entity.EnrollmentStatus;
import com.inturn.suncomputer.enrollment.repository.EnrollmentRepository;
import com.inturn.suncomputer.enquiry.dto.ConvertEnquiryRequest;
import com.inturn.suncomputer.enquiry.dto.EnquiryConversionResponse;
import com.inturn.suncomputer.enquiry.entity.Enquiry;
import com.inturn.suncomputer.enquiry.entity.EnquiryStatus;
import com.inturn.suncomputer.enquiry.repository.EnquiryRepository;
import com.inturn.suncomputer.student.entity.Student;
import com.inturn.suncomputer.student.entity.StudentStatus;
import com.inturn.suncomputer.student.repository.StudentRepository;
import com.inturn.suncomputer.user.dto.CreateUserRequest;
import com.inturn.suncomputer.user.dto.UserResponse;
import com.inturn.suncomputer.user.repository.UserRepository;
import com.inturn.suncomputer.user.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.util.Set;

@Service
@Transactional
public class EnquiryConversionServiceImpl
        implements EnquiryConversionService {

    private final EnquiryRepository enquiryRepository;
    private final BatchRepository batchRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public EnquiryConversionServiceImpl(
            EnquiryRepository enquiryRepository,
            BatchRepository batchRepository,
            StudentRepository studentRepository,
            EnrollmentRepository enrollmentRepository,
            UserRepository userRepository,
            UserService userService
    ) {
        this.enquiryRepository =
                enquiryRepository;

        this.batchRepository =
                batchRepository;

        this.studentRepository =
                studentRepository;

        this.enrollmentRepository =
                enrollmentRepository;

        this.userRepository =
                userRepository;

        this.userService =
                userService;
    }

    @Override
    public EnquiryConversionResponse convertEnquiry(
            Long enquiryId,
            ConvertEnquiryRequest request
    ) {

        /*
         * 1. Find enquiry
         */

        Enquiry enquiry =
                enquiryRepository.findById(enquiryId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Enquiry not found with id: "
                                                + enquiryId
                                )
                        );

        /*
         * 2. Prevent duplicate conversion
         */

        if (
                enquiry.getStatus()
                        == EnquiryStatus.CONVERTED
        ) {
            throw new RuntimeException(
                    "Enquiry is already converted"
            );
        }

        /*
         * 3. Find batch
         */

        Batch batch =
                batchRepository.findById(
                                request.batchId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Batch not found with id: "
                                                + request.batchId()
                                )
                        );

        /*
         * 4. Calculate fees
         */

        BigDecimal agreedFee =
                request.agreedFee();

        BigDecimal discount =
                request.discount() != null
                        ? request.discount()
                        : BigDecimal.ZERO;

        if (
                discount.compareTo(agreedFee) > 0
        ) {
            throw new RuntimeException(
                    "Discount cannot be greater than agreed fee"
            );
        }

        BigDecimal finalFee =
                agreedFee.subtract(discount);

        /*
         * 5. Generate student code
         *    if admin did not provide one
         */

        String studentCode =
                request.studentCode();

        if (
                studentCode == null
                        || studentCode.isBlank()
        ) {
            studentCode =
                    generateStudentCode();
        }

        /*
         * 6. Check student code
         */

        if (
                studentRepository
                        .existsByStudentCode(studentCode)
        ) {
            throw new RuntimeException(
                    "Student code already exists: "
                            + studentCode
            );
        }

        /*
         * 7. Prepare username/email
         */

        String username =
                studentCode;

        String email =
                enquiry.getEmail();

        /*
         * users.email is mandatory and unique.
         * If enquiry has no email, create
         * an internal email.
         */

        if (
                email == null
                        || email.isBlank()
        ) {
            email =
                    studentCode.toLowerCase()
                            + "@suncomputer.local";
        }

        /*
         * 8. Generate temporary password
         */

        String temporaryPassword =
                generateTemporaryPassword();

        /*
         * 9. Create application User
         */

        UserResponse userResponse =
                userService.createUser(
                        new CreateUserRequest(
                                username,
                                email,
                                temporaryPassword,
                                enquiry.getFullName(),
                                null,
                                enquiry.getMobile(),
                                true,
                                Set.of("STUDENT")
                        )
                );

        /*
         * 10. Load actual User entity
         */

        var user =
                userRepository.findById(
                                userResponse.id()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Created user could not be found"
                                )
                        );

        /*
         * 11. Create Student
         */

        Student student =
                new Student();

        student.setUser(user);

        student.setStudentCode(
                studentCode
        );

        student.setAddress(
                enquiry.getAddress()
        );

        /*
         * Current enquiry contains only
         * the prospective student's mobile.
         *
         * Parent details can be captured
         * during the full admission module later.
         */

        student.setParentPhone(
                enquiry.getMobile()
        );

        student.setAdmissionDate(
                request.admissionDate() != null
                        ? request.admissionDate()
                        : LocalDate.now()
        );

        student.setStatus(
                StudentStatus.ACTIVE
        );

        Student savedStudent =
                studentRepository.save(student);

        /*
         * 12. Create Enrollment
         */

        Enrollment enrollment =
                new Enrollment();

        enrollment.setStudent(
                savedStudent
        );

        enrollment.setBatch(
                batch
        );

        enrollment.setEnrollmentDate(
                request.enrollmentDate() != null
                        ? request.enrollmentDate()
                        : LocalDate.now()
        );

        enrollment.setAgreedFee(
                agreedFee
        );

        enrollment.setDiscount(
                discount
        );

        enrollment.setFinalFee(
                finalFee
        );

        enrollment.setStatus(
                EnrollmentStatus.ACTIVE
        );

        enrollment.setNotes(
                request.notes()
        );

        Enrollment savedEnrollment =
                enrollmentRepository.save(
                        enrollment
                );

        /*
         * 13. Mark enquiry as converted
         */

        enquiry.setStatus(
                EnquiryStatus.CONVERTED
        );

        enquiry.setConvertedStudentId(
                savedStudent.getId()
        );

        /*
         * No further follow-up is required
         * after successful conversion.
         */

        enquiry.setNextFollowUpDate(
                null
        );

        enquiryRepository.save(
                enquiry
        );

        /*
         * 14. Return conversion result
         */

        return new EnquiryConversionResponse(
                enquiry.getId(),
                savedStudent.getId(),
                savedStudent.getStudentCode(),
                savedEnrollment.getId(),
                batch.getId(),
                agreedFee,
                discount,
                finalFee,
                savedStudent.getAdmissionDate(),
                savedEnrollment.getEnrollmentDate()
        );
    }

    /*
     * Generate:
     *
     * SC2026123456
     */

    private String generateStudentCode() {

        String studentCode;

        do {

            int randomNumber =
                    100000
                            + secureRandom.nextInt(900000);

            studentCode =
                    "SC"
                            + Year.now().getValue()
                            + randomNumber;

        } while (
                studentRepository
                        .existsByStudentCode(
                                studentCode
                        )
        );

        return studentCode;
    }

    /*
     * Temporary password.
     *
     * Example:
     * Sc@483921
     */

    private String generateTemporaryPassword() {

        int randomNumber =
                100000
                        + secureRandom.nextInt(900000);

        return "Sc@"
                + randomNumber;
    }
}
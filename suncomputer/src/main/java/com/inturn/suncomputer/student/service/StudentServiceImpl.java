package com.inturn.suncomputer.student.service;

import com.inturn.suncomputer.common.exception.BusinessRuleException;
import com.inturn.suncomputer.common.exception.DuplicateResourceException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;

import com.inturn.suncomputer.student.dto.CreateStudentRequest;
import com.inturn.suncomputer.student.dto.StudentResponse;
import com.inturn.suncomputer.student.dto.UpdateStudentRequest;

import com.inturn.suncomputer.student.entity.Student;
import com.inturn.suncomputer.student.entity.StudentStatus;

import com.inturn.suncomputer.student.repository.StudentRepository;

import com.inturn.suncomputer.user.entity.RoleName;
import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentServiceImpl
        implements StudentService {

    private final StudentRepository studentRepository;

    private final UserRepository userRepository;

    public StudentServiceImpl(
            StudentRepository studentRepository,
            UserRepository userRepository
    ) {

        this.studentRepository =
                studentRepository;

        this.userRepository =
                userRepository;
    }

    @Override
    public StudentResponse createStudent(
            CreateStudentRequest request
    ) {

        if (
                studentRepository
                        .existsByStudentCode(
                                request.studentCode()
                        )
        ) {

            throw new DuplicateResourceException(
                    "Student code already exists: "
                            + request.studentCode()
            );
        }

        if (
                studentRepository
                        .existsByUserId(
                                request.userId()
                        )
        ) {

            throw new DuplicateResourceException(
                    "Student profile already exists for user id: "
                            + request.userId()
            );
        }

        User user =
                userRepository
                        .findById(request.userId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found with id: "
                                                + request.userId()
                                )
                        );

        boolean isStudent =
                user.getRoles()
                        .stream()
                        .anyMatch(
                                role ->
                                        role.getName()
                                                == RoleName.STUDENT
                        );

        if (!isStudent) {

            throw new BusinessRuleException(
                    "User must have STUDENT role"
            );
        }

        if (
                request.admissionDate()
                        .isAfter(
                                java.time.LocalDate.now()
                        )
        ) {

            throw new BusinessRuleException(
                    "Admission date cannot be in the future"
            );
        }

        Student student =
                new Student();

        student.setUser(user);

        student.setStudentCode(
                request.studentCode()
                        .trim()
                        .toUpperCase()
        );

        student.setDateOfBirth(
                request.dateOfBirth()
        );

        student.setGender(
                request.gender()
        );

        student.setAddress(
                request.address()
        );

        student.setCity(
                request.city()
        );

        student.setState(
                request.state()
        );

        student.setPincode(
                request.pincode()
        );

        student.setParentName(
                request.parentName()
        );

        student.setParentPhone(
                request.parentPhone()
        );

        student.setAdmissionDate(
                request.admissionDate()
        );

        student.setStatus(
                StudentStatus.ACTIVE
        );

        Student savedStudent =
                studentRepository.save(
                        student
                );

        return mapToResponse(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(
            Long id
    ) {

        return mapToResponse(
                findStudent(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse>
    getAllStudents() {

        return studentRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse>
    getActiveStudents() {

        return studentRepository
                .findByStatus(
                        StudentStatus.ACTIVE
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public StudentResponse updateStudent(
            Long id,
            UpdateStudentRequest request
    ) {

        Student student =
                findStudent(id);

        if (request.dateOfBirth() != null) {

            student.setDateOfBirth(
                    request.dateOfBirth()
            );
        }

        if (request.gender() != null) {

            student.setGender(
                    request.gender()
            );
        }

        if (request.address() != null) {

            student.setAddress(
                    request.address()
            );
        }

        if (request.city() != null) {

            student.setCity(
                    request.city()
            );
        }

        if (request.state() != null) {

            student.setState(
                    request.state()
            );
        }

        if (request.pincode() != null) {

            student.setPincode(
                    request.pincode()
            );
        }

        if (request.parentName() != null) {

            student.setParentName(
                    request.parentName()
            );
        }

        if (request.parentPhone() != null) {

            student.setParentPhone(
                    request.parentPhone()
            );
        }

        if (request.status() != null) {

            student.setStatus(
                    request.status()
            );
        }

        return mapToResponse(student);
    }

    @Override
    public void deactivateStudent(
            Long id
    ) {

        Student student =
                findStudent(id);

        student.setStatus(
                StudentStatus.INACTIVE
        );
    }

    private Student findStudent(
            Long id
    ) {

        return studentRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: "
                                        + id
                        )
                );
    }

    private StudentResponse mapToResponse(
            Student student
    ) {

        User user =
                student.getUser();

        return new StudentResponse(

                student.getId(),

                user.getId(),

                user.getUsername(),

                user.getFirstName(),

                user.getLastName(),

                user.getEmail(),

                user.getPhone(),

                student.getStudentCode(),

                student.getDateOfBirth(),

                student.getGender(),

                student.getAddress(),

                student.getCity(),

                student.getState(),

                student.getPincode(),

                student.getParentName(),

                student.getParentPhone(),

                student.getAdmissionDate(),

                student.getStatus(),

                student.getCreatedAt(),

                student.getUpdatedAt()
        );
    }
}
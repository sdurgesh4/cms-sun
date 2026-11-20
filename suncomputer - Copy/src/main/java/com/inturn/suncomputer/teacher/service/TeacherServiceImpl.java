package com.inturn.suncomputer.teacher.service;

import com.inturn.suncomputer.common.exception.DuplicateResourceException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;

import com.inturn.suncomputer.teacher.dto.CreateTeacherRequest;
import com.inturn.suncomputer.teacher.dto.TeacherResponse;
import com.inturn.suncomputer.teacher.dto.UpdateTeacherRequest;

import com.inturn.suncomputer.teacher.entity.Teacher;
import com.inturn.suncomputer.teacher.entity.TeacherStatus;

import com.inturn.suncomputer.teacher.repository.TeacherRepository;

import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.entity.RoleName;
import com.inturn.suncomputer.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeacherServiceImpl
        implements TeacherService {

    private final TeacherRepository teacherRepository;

    private final UserRepository userRepository;

    public TeacherServiceImpl(
            TeacherRepository teacherRepository,
            UserRepository userRepository
    ) {

        this.teacherRepository =
                teacherRepository;

        this.userRepository =
                userRepository;
    }

    @Override
    public TeacherResponse createTeacher(
            CreateTeacherRequest request
    ) {

        if (
                teacherRepository
                        .existsByEmployeeCode(
                                request.employeeCode()
                        )
        ) {

            throw new DuplicateResourceException(
                    "Employee code already exists: "
                            + request.employeeCode()
            );
        }

        if (
                teacherRepository
                        .existsByUserId(
                                request.userId()
                        )
        ) {

            throw new DuplicateResourceException(
                    "Teacher profile already exists for user id: "
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

        boolean isTeacher =
                user.getRoles()
                        .stream()
                        .anyMatch(
                                role ->
                                        role.getName()
                                                == RoleName.TEACHER
                        );

        if (!isTeacher) {

            throw new IllegalArgumentException(
                    "User must have TEACHER role"
            );
        }

        Teacher teacher =
                new Teacher();

        teacher.setUser(user);

        teacher.setEmployeeCode(
                request.employeeCode()
                        .trim()
                        .toUpperCase()
        );

        teacher.setSpecialization(
                request.specialization()
        );

        teacher.setQualification(
                request.qualification()
        );

        teacher.setExperienceYears(
                request.experienceYears()
        );

        teacher.setJoiningDate(
                request.joiningDate()
        );

        teacher.setStatus(
                TeacherStatus.ACTIVE
        );

        Teacher savedTeacher =
                teacherRepository.save(
                        teacher
                );

        return mapToResponse(savedTeacher);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(
            Long id
    ) {

        Teacher teacher =
                findTeacher(id);

        return mapToResponse(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<TeacherResponse>
    getAllTeachers() {

        return teacherRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.List<TeacherResponse>
    getActiveTeachers() {

        return teacherRepository
                .findByStatus(
                        TeacherStatus.ACTIVE
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public TeacherResponse updateTeacher(
            Long id,
            UpdateTeacherRequest request
    ) {

        Teacher teacher =
                findTeacher(id);

        if (
                request.specialization()
                        != null
        ) {

            teacher.setSpecialization(
                    request.specialization()
            );
        }

        if (
                request.qualification()
                        != null
        ) {

            teacher.setQualification(
                    request.qualification()
            );
        }

        if (
                request.experienceYears()
                        != null
        ) {

            teacher.setExperienceYears(
                    request.experienceYears()
            );
        }

        if (
                request.joiningDate()
                        != null
        ) {

            teacher.setJoiningDate(
                    request.joiningDate()
            );
        }

        if (
                request.status()
                        != null
        ) {

            teacher.setStatus(
                    request.status()
            );
        }

        return mapToResponse(teacher);
    }

    @Override
    public void deactivateTeacher(
            Long id
    ) {

        Teacher teacher =
                findTeacher(id);

        teacher.setStatus(
                TeacherStatus.INACTIVE
        );
    }

    private Teacher findTeacher(
            Long id
    ) {

        return teacherRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found with id: "
                                        + id
                        )
                );
    }

    private TeacherResponse mapToResponse(
            Teacher teacher
    ) {

        User user =
                teacher.getUser();

        return new TeacherResponse(

                teacher.getId(),

                user.getId(),

                user.getUsername(),

                user.getFirstName(),

                user.getLastName(),

                user.getEmail(),

                user.getPhone(),

                teacher.getEmployeeCode(),

                teacher.getSpecialization(),

                teacher.getQualification(),

                teacher.getExperienceYears(),

                teacher.getJoiningDate(),

                teacher.getStatus(),

                teacher.getCreatedAt(),

                teacher.getUpdatedAt()
        );
    }
}
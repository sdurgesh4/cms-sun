package com.inturn.suncomputer.user.service;

import com.inturn.suncomputer.common.exception.DuplicateResourceException;
import com.inturn.suncomputer.common.exception.ResourceNotFoundException;
import com.inturn.suncomputer.user.dto.CreateUserRequest;
import com.inturn.suncomputer.user.dto.UpdateUserRequest;
import com.inturn.suncomputer.user.dto.UserResponse;
import com.inturn.suncomputer.user.entity.Role;
import com.inturn.suncomputer.user.entity.RoleName;
import com.inturn.suncomputer.user.entity.User;
import com.inturn.suncomputer.user.repository.RoleRepository;
import com.inturn.suncomputer.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder   )
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(
            CreateUserRequest request
    ) {

        if (
                userRepository
                        .existsByUsername(
                                request.username()
                        )
        ) {

            throw new DuplicateResourceException(
                    "Username already exists: "
                            + request.username()
            );
        }

        if (
                userRepository
                        .existsByEmail(
                                request.email()
                        )
        ) {

            throw new DuplicateResourceException(
                    "Email already exists: "
                            + request.email()
            );
        }

        User user = new User();

        user.setUsername(
                request.username()
        );

        user.setEmail(
                request.email()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.password()
                )
        );

        user.setFirstName(
                request.firstName()
        );

        user.setLastName(
                request.lastName()
        );

        user.setPhone(
                request.phone()
        );

        user.setEnabled(
                request.enabled()
        );

        Set<Role> roles =
                new HashSet<>();

        if (
                request.roles() != null &&
                        !request.roles().isEmpty()
        ) {

            for (String roleName :
                    request.roles()) {

                RoleName role =
                        RoleName.valueOf(
                                roleName.toUpperCase()
                        );

                Role roleEntity =
                        roleRepository
                                .findByName(role)
                                .orElseThrow(() ->
                                     new ResourceNotFoundException(
                                            "Role not found: " + roleName
                                    )
                                );

                roles.add(roleEntity);
            }

        } else {

            Role studentRole =
                    roleRepository
                            .findByName(
                                    RoleName.STUDENT
                            )
                            .orElseThrow();

            roles.add(studentRole);
        }

        user.setRoles(roles);

        User savedUser =
                userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(
            Long id
    ) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                             new ResourceNotFoundException(
                                     "User not found with id: " + id
                            )
                        );

        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        if (request.email() != null) {
            user.setEmail(
                    request.email()
            );
        }

        if (request.firstName() != null) {
            user.setFirstName(
                    request.firstName()
            );
        }

        if (request.lastName() != null) {
            user.setLastName(
                    request.lastName()
            );
        }

        if (request.phone() != null) {
            user.setPhone(
                    request.phone()
            );
        }

        if (request.enabled() != null) {
            user.setEnabled(
                    request.enabled()
            );
        }

        if (
                request.roles() != null &&
                        !request.roles().isEmpty()
        ) {

            Set<Role> roles =
                    new HashSet<>();

            for (String roleName :
                    request.roles()) {

                RoleName role =
                        RoleName.valueOf(
                                roleName.toUpperCase()
                        );

                Role roleEntity =
                        roleRepository
                                .findByName(role)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Role not found"
                                        )
                                );

                roles.add(roleEntity);
            }

            user.setRoles(roles);
        }

        return mapToResponse(user);
    }

    @Override
    public void disableUser(
            Long id
    ) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        user.setEnabled(false);
    }

    private UserResponse mapToResponse(
            User user
    ) {

        List<String> roles =
                user.getRoles()
                        .stream()
                        .map(role ->
                                role.getName().name()
                        )
                        .toList();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.isEnabled(),
                roles
        );
    }
}
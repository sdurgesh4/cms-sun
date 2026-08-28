package com.inturn.suncomputer.user.service;

import com.inturn.suncomputer.user.dto.CreateUserRequest;
import com.inturn.suncomputer.user.dto.UpdateUserRequest;
import com.inturn.suncomputer.user.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(
            CreateUserRequest request
    );

    UserResponse getUserById(
            Long id
    );

    List<UserResponse> getAllUsers();

    UserResponse updateUser(
            Long id,
            UpdateUserRequest request
    );

    void disableUser(
            Long id
    );
}
package com.uom.Software_design_competition.domain.service.impl;

import com.uom.Software_design_competition.application.transport.request.UserProfileRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.UserProfileResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.domain.entity.User;
import com.uom.Software_design_competition.domain.repository.UserRepository;
import com.uom.Software_design_competition.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ApiResponse<UserProfileResponse> getUserProfile(String username) throws BaseException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(String.valueOf(HttpStatus.NOT_FOUND.value()), "User not found"));

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setDepartment(user.getDepartment());

        return new ApiResponse<>(String.valueOf(HttpStatus.OK.value()), "User profile retrieved successfully", response);
    }

    @Override
    public ApiResponse<UserProfileResponse> updateUserProfile(String username, UserProfileRequest request) throws BaseException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(String.valueOf(HttpStatus.NOT_FOUND.value()), "User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getDepartment() != null) user.setDepartment(request.getDepartment());

        userRepository.save(user);

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setDepartment(user.getDepartment());

        return new ApiResponse<>(String.valueOf(HttpStatus.OK.value()), "User profile updated successfully", response);
    }
}

package com.uom.Software_design_competition.domain.service;

import com.uom.Software_design_competition.application.transport.request.UserProfileRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.UserProfileResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;

public interface UserService {
    ApiResponse<UserProfileResponse> getUserProfile(String username) throws BaseException;
    ApiResponse<UserProfileResponse> updateUserProfile(String username, UserProfileRequest request) throws BaseException;
}

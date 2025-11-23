package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.transport.request.UserProfileRequest;
import com.uom.Software_design_competition.application.transport.response.ApiResponse;
import com.uom.Software_design_competition.application.transport.response.UserProfileResponse;
import com.uom.Software_design_competition.application.util.exception.type.BaseException;
import com.uom.Software_design_competition.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${base-url.context}" + "/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile() throws BaseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(@RequestBody UserProfileRequest request) throws BaseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ResponseEntity.ok(userService.updateUserProfile(username, request));
    }
}

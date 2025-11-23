package com.uom.Software_design_competition.application.transport.request;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String fullName;
    private String email;
    private String role;
    private String department;
}

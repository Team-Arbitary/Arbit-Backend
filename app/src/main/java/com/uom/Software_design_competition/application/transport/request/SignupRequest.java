package com.uom.Software_design_competition.application.transport.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String role;
}

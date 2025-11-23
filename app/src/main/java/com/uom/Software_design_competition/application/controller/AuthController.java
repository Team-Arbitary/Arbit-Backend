package com.uom.Software_design_competition.application.controller;

import com.uom.Software_design_competition.application.transport.request.AuthRequest;
import com.uom.Software_design_competition.application.transport.request.SignupRequest;
import com.uom.Software_design_competition.application.transport.response.AuthResponse;
import com.uom.Software_design_competition.config.JwtUtil;
import com.uom.Software_design_competition.domain.entity.User;
import com.uom.Software_design_competition.domain.repository.UserRepository;
import com.uom.Software_design_competition.domain.service.impl.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${base-url.context}" + "/api/auth")
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:8080",
        "https://arbit-frontend.vercel.app" }, allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(signupRequest.getPassword());
        user.setRole(signupRequest.getRole());

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }
}

package com.epam.gymcrm.controller;

import com.epam.gymcrm.dto.request.LoginRequest;
import com.epam.gymcrm.dto.response.LoginResponse;
import com.epam.gymcrm.security.JwtService;
import com.epam.gymcrm.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService,
                          JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        authService.authenticate(
                request.getUsername(),
                request.getPassword()
        );

        String token = jwtService.generateToken(request.getUsername());
        return new LoginResponse(token);
    }
}
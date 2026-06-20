package org.dtt.msauthpublic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.AuthResponse;
import org.dtt.msauthpublic.dto.LoginRequest;
import org.dtt.msauthpublic.dto.RegisterRequest;
import org.dtt.msauthpublic.dto.RegisterResponse;
import org.dtt.msauthpublic.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Endpoints for user authentication and token management"
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register user",
            description = "Creates a new user account."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public RegisterResponse userRegister(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return authService.registerUser(registerRequest);
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates the user and returns JWT tokens."
    )
    @PostMapping("/login")
    public AuthResponse loginUser(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return authService.loginUser(loginRequest);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a refresh token."
    )
    @PostMapping("/refresh")
    public AuthResponse refreshToken(
            @RequestBody String refreshToken
    ) {
        return authService.refreshToken(refreshToken);
    }
}
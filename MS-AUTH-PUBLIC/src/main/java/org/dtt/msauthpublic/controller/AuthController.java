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
        description = "Endpoints for authentication and token management. All endpoints are public — no authentication required."
)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register user",
            description = "Creates a new user account. Sends a verification code to the provided email. The account must be verified before login. Public endpoint."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public RegisterResponse userRegister(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    @Operation(
            summary = "Login",
            description = "Authenticates a verified user and returns an access token and a refresh token. Public endpoint."
    )
    @PostMapping("/login")
    public AuthResponse loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.loginUser(loginRequest);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token. Public endpoint."
    )
    @PostMapping("/refresh")
    public AuthResponse refreshToken(@RequestBody String refreshToken) {
        return authService.refreshToken(refreshToken);
    }
}
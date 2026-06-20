package org.dtt.msauthpublic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.UserResponse;
import org.dtt.msauthpublic.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Tag(
        name = "Users",
        description = "Endpoints for user management"
)
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get authenticated user",
            description = "Returns information about the authenticated user."
    )
    @GetMapping("/me")
    public UserResponse me(
            @AuthenticationPrincipal Jwt jwt
    ) {
        // Temporal: imprime todos los claims para saber el nombre correcto
        System.out.println("Claims: " + jwt.getClaims());

        UUID id = UUID.fromString(
                jwt.getClaim("userId")
        );
        return userService.getUserById(id);
    }

    @Operation(
            summary = "Get user by id",
            description = "Returns user information by identifier."
    )
    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable UUID id
    ) {
        return userService.getUserById(id);
    }


}
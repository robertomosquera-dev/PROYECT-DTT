package org.dtt.msauthpublic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.ChangePassword;
import org.dtt.msauthpublic.dto.UserRequestUpdate;
import org.dtt.msauthpublic.dto.UserResponse;
import org.dtt.msauthpublic.dto.VerifyRequest;
import org.dtt.msauthpublic.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Get user by id", description = "Returns user information by identifier.")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @GetMapping
    public List<UserResponse> findAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        if (sortBy == null || sortBy.isBlank()) sortBy = "id";
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return userService.FindAllUsers(page, size, sort);
    }

    @Operation(summary = "Verify user", description = "Verifies user account with code sent to email.")
    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyUser(@RequestBody @Valid VerifyRequest request) {
        userService.verifyUser(request);
    }

    @Operation(summary = "Resend verification code", description = "Resends verification code to email.")
    @PostMapping("/resend-code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendCode(@RequestParam String email) {
        userService.resendCode(email);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','USER')")
    @Operation(summary = "Update user", description = "Partially updates authenticated user info.")
    @PatchMapping
    public UserResponse update(@RequestBody @Valid UserRequestUpdate request) {
        return userService.update(request);
    }

    @Operation(summary = "Change password", description = "Changes authenticated user password.")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','USER')")
    @PatchMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody @Valid ChangePassword changePassword) {
        userService.changePassword(changePassword);
    }

    @Operation(summary = "Delete user", description = "Disables a user by id.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
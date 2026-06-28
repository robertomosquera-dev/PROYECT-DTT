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
@Tag(
        name = "Users",
        description = "Endpoints for user management. Roles: USER (update own profile, change password), ADMIN (view and manage users), SUPER_ADMIN (full access). Public: verify account, resend code."
)
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(
            summary = "Get user by id",
            description = "Returns user information by identifier. Accessible by: ADMIN, SUPER_ADMIN."
    )
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of verified and active users with role USER. Accessible by: ADMIN, SUPER_ADMIN."
    )
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

    @Operation(
            summary = "Verify account",
            description = "Verifies a user account using the code sent to their email. Public endpoint — no authentication required."
    )
    @PostMapping("/verify")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verifyUser(@RequestBody @Valid VerifyRequest request) {
        userService.verifyUser(request);
    }

    @Operation(
            summary = "Resend verification code",
            description = "Resends a new verification code to the given email. Public endpoint — no authentication required."
    )
    @PostMapping("/resend-code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resendCode(@RequestParam String email) {
        userService.resendCode(email);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','USER')")
    @Operation(
            summary = "Update own profile",
            description = "Partially updates the authenticated user's profile (username, name, surname, phone, address). Accessible by: USER, ADMIN, SUPER_ADMIN."
    )
    @PatchMapping
    public UserResponse update(@RequestBody @Valid UserRequestUpdate request) {
        return userService.update(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','USER')")
    @Operation(
            summary = "Change password",
            description = "Changes the authenticated user's password. Requires current password for validation. Accessible by: USER, ADMIN, SUPER_ADMIN."
    )
    @PatchMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody @Valid ChangePassword changePassword) {
        userService.changePassword(changePassword);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(
            summary = "Disable user",
            description = "Soft deletes a user by setting enabled=false. The user must be verified. Accessible by: ADMIN, SUPER_ADMIN."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
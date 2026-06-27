package org.dtt.msauthpublic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.ChangePassword;
import org.dtt.msauthpublic.dto.UserRequestUpdate;
import org.dtt.msauthpublic.dto.UserResponse;
import org.dtt.msauthpublic.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/users")
@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user by id", description = "Returns user information by identifier.")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Get all users", description = "Returns paginated list of users.")
    @GetMapping
    public List<UserResponse> findAllUsers(Pageable pageable) {
        return userService.FindAllUsers(pageable);
    }

    @Operation(summary = "Update user", description = "Partially updates authenticated user info.")
    @PatchMapping
    public UserResponse update(@RequestBody @Valid UserRequestUpdate request) {
        return userService.update(request);
    }

    @Operation(summary = "Change password", description = "Changes authenticated user password.")
    @PatchMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody @Valid ChangePassword changePassword) {
        userService.changePassword(changePassword);
    }

    @Operation(summary = "Delete user", description = "Disables a user by id.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
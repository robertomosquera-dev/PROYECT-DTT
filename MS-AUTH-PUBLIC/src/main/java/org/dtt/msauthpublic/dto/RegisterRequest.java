package org.dtt.msauthpublic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Set;
import java.util.UUID;

@Schema(description = "Request used to register a new user")
public record RegisterRequest(

        @NotBlank
        @Email
        @Schema(
                description = "User email address",
                example = "roberto@example.com"
        )
        String email,

        @NotBlank
        @Size(min = 4, max = 20)
        @Schema(
                description = "Unique username",
                example = "robmosquera"
        )
        String username,

        @NotBlank
        @Size(min = 3, max = 100)
        @Schema(
                description = "User password",
                example = "SecurePassword123"
        )
        String password,

        @NotBlank
        @Size(min = 2, max = 50)
        @Schema(
                description = "User first name",
                example = "Roberto"
        )
        String name,

        @NotBlank
        @Size(min = 2, max = 50)
        @Schema(
                description = "User surname",
                example = "Mosquera"
        )
        String surname,

        @NotBlank
        @Pattern(
                regexp = "^[0-9+\\- ]{7,20}$",
                message = "Invalid phone format"
        )
        @Schema(
                description = "User phone number",
                example = "+51987654321"
        )
        String phone,

        @NotBlank
        @Size(min = 5, max = 255)
        @Schema(
                description = "User address",
                example = "Av. Javier Prado 123"
        )
        String address,

        @NotEmpty
        @Schema(
                description = "Assigned role identifiers"
        )
        Set<UUID> roles
) {
}

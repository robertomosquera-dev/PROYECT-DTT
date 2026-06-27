package org.dtt.msauthpublic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record UserRequestUpdate(

        @Email
        @Schema(description = "User email address", example = "roberto@example.com")
        String email,

        @Size(min = 4, max = 20)
        @Schema(description = "Unique username", example = "robmosquera")
        String username,

        @Size(min = 2, max = 50)
        @Schema(description = "User first name", example = "Roberto")
        String name,

        @Size(min = 2, max = 50)
        @Schema(description = "User surname", example = "Mosquera")
        String surname,

        @Pattern(
                regexp = "^[0-9+\\- ]{7,20}$",
                message = "Invalid phone format"
        )
        @Schema(description = "User phone number", example = "+51987654321")
        String phone,

        @Size(min = 5, max = 255)
        @Schema(description = "User address", example = "Av. Javier Prado 123")
        String address
) {}
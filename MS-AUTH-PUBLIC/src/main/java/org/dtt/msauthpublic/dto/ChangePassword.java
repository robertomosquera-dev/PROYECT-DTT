package org.dtt.msauthpublic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePassword(
        @NotBlank
        @Size(min = 3, max = 100)
        @Schema(
                description = "old password",
                example = "SecurePassword123"
        )
        String oldPassword,
        @NotBlank
        @Size(min = 3, max = 100)
        @Schema(
                description = "new password",
                example = "elPepe123"
        )
        String newPassword
) {
}

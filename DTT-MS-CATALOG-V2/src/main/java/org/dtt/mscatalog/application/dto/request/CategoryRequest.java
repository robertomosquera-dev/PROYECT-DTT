package org.dtt.mscatalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.util.UUID;

@Builder
public record CategoryRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    String name,

    @NotBlank(message = "El slug es obligatorio")
    @Size(max = 100, message = "El slug no puede exceder los 100 caracteres")
    String slug,

    UUID parentId
) {}

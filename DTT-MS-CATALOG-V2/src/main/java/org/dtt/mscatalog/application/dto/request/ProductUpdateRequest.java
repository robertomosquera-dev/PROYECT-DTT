package org.dtt.mscatalog.application.dto.request;

import jakarta.validation.constraints.Size;

public record ProductUpdateRequest(
    @Size(max = 200, message = "El nombre no puede exceder los 200 caracteres")
    String name,

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    String description
) {}
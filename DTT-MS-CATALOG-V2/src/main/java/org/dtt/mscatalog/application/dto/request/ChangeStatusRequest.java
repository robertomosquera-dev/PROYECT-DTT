package org.dtt.mscatalog.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChangeStatusRequest(
        @NotNull(message = "El id es obligatorio") UUID id,
        @NotNull(message = "El status es obligatorio") StatusRequest status
) {}

package org.dtt.mscatalog.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Builder
public record ProductImgRequest(
    @NotNull(message = "La imagen es obligatoria")
    MultipartFile image,

    @NotNull(message = "El orden es obligatorio")
    @PositiveOrZero(message = "El orden debe ser un número positivo o cero")
    Short order
) {}

package org.dtt.mscatalog.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Builder
public record ImageRequest(
        @NotNull(message = "El ownerId es obligatorio") UUID ownerId,
        @NotNull(message = "La imagen es obligatoria") MultipartFile image,
        @NotNull(message = "El ownerType es obligatorio") OwnerType ownerType,
        @PositiveOrZero(message = "El orden debe ser un número positivo o cero") Short order
) {
}

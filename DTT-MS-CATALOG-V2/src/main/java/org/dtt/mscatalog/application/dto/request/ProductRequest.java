package org.dtt.mscatalog.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.dtt.mscatalog.domain.model.Enum.ProductBaseStatus;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
public record ProductRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder los 200 caracteres")
    String name,

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    String description,

    @NotEmpty(message = "Debe asignar al menos una categoría")
    Set<UUID> categoryIds,

    List<ProductImgRequest> images
) {}

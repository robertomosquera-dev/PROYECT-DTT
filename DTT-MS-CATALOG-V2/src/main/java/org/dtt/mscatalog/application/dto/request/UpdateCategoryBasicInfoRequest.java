package org.dtt.mscatalog.application.dto.request;

import jakarta.validation.constraints.Size;
import org.dtt.mscatalog.domain.model.Enum.CategoryStatus;

public record UpdateCategoryBasicInfoRequest(
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    String name,

    @Size(max = 100, message = "El slug no puede exceder los 100 caracteres")
    String slug,

    CategoryStatus categoryStatus
) {
}

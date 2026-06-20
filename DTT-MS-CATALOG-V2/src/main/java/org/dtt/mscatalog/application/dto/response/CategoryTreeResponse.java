package org.dtt.mscatalog.application.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record CategoryTreeResponse(
        UUID id,
        String name,
        List<CategoryTreeResponse> children
) implements Serializable {
}

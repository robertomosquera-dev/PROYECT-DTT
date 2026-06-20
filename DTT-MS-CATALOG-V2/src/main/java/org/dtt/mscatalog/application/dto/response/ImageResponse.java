package org.dtt.mscatalog.application.dto.response;

import org.dtt.mscatalog.domain.model.Enum.OwnerType;

import java.io.Serializable;
import java.util.UUID;

public record ImageResponse(
        UUID id,
        UUID ownerId,
        OwnerType ownerType,
        String url,
        Short order
) implements Serializable {
}

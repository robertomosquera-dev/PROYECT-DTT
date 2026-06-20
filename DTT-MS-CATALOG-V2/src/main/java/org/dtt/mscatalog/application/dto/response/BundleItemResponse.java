package org.dtt.mscatalog.application.dto.response;

import java.util.UUID;

public record BundleItemResponse(
        UUID id,
        UUID productSaleId,
        Integer weight
) {
}

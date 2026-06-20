package org.dtt.mscatalog.application.dto.response.Inventory;

import java.util.UUID;

public record BundleItemResponse(
        UUID id,
        UUID productSaleId,
        Integer weight
) {
}

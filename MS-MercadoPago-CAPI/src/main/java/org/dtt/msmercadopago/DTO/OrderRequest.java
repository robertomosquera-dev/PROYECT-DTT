package org.dtt.msmercadopago.DTO;

import java.util.List;
import java.util.UUID;

public record OrderRequest (
        UUID orderId,
        List<ItemRequest> items,
        PayerRequest payer,
        String platform
) {
}

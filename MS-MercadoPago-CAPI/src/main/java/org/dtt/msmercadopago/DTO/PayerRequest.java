package org.dtt.msmercadopago.DTO;

import java.util.UUID;

public record PayerRequest (
        UUID payerId,
        String email,
        String name,
        String surname
)  {
}

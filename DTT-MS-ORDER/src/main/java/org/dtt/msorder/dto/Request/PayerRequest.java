package org.dtt.msorder.dto.Request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PayerRequest (
        UUID payerId,
        String email,
        String name,
        String surname
)  {
}

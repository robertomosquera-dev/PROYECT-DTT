package org.dtt.msauthpublic.dto;

import lombok.Builder;

@Builder
public record VerificationCodeRequest(
      String code,
      String name,
      String email
) {
}

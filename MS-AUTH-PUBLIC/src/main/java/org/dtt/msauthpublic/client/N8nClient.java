package org.dtt.msauthpublic.client;

import org.dtt.msauthpublic.dto.VerificationCodeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "n8n-service",
        url = "${api.n8n}"
)
public interface N8nClient {
    @PostMapping("/webhook/send-code-verification")
    void sendCode(VerificationCodeRequest codeRequest);
}

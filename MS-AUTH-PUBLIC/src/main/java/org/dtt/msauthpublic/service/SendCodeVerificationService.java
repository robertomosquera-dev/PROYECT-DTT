package org.dtt.msauthpublic.service;

import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.client.N8nClient;
import org.dtt.msauthpublic.dto.VerificationCodeRequest;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class SendCodeVerificationService {

    private final N8nClient n8nClient;

    public String sendCodeVerification(String email,String name){
        String code = Integer.toString(new SecureRandom().nextInt(900000) + 100000);
        n8nClient.sendCode(
                VerificationCodeRequest
                        .builder()
                        .code(code)
                        .name(name)
                        .email(email)
                        .build());
        return code;
    }

}

package org.dtt.msauthpublic.Utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        RSAPrivateKey privateKey,
        RSAPublicKey publicKey,
        Long expirationTime,
        Long refreshExpirationTime,
        String issuer
){

}

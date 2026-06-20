package org.dtt.msauthpublic.Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;

    public String generateToken(Authentication authentication){
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        String authorities = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Instant now = Instant.now();
        Instant expiration = now.plus(jwtProperties.expirationTime(), ChronoUnit.MINUTES);
        JwtClaimsSet jwt = JwtClaimsSet
                .builder()
                .issuer(jwtProperties.issuer())
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(expiration)
                .claim("userId", userDetails.getId().toString())
                .claim("email", userDetails.getEmail())
                .claim("firstName", userDetails.getFirstName())
                .claim("lastName", userDetails.getLastName())
                .claim("authorities", authorities)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwt)).getTokenValue();
    }

    public String generateSystemToken(String systemName) {

        Instant now = Instant.now();
        Instant expiration = now.plus(60, ChronoUnit.MINUTES);

        JwtClaimsSet jwt = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(systemName)
                .issuedAt(now)
                .expiresAt(expiration)
                .claim("authorities", "ROLE_SYSTEM")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwt)).getTokenValue();
    }

}

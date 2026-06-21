package org.dtt.msorder.service.WebClientService;

import lombok.RequiredArgsConstructor;

import org.dtt.msorder.dto.Response.UserResponse;
import org.dtt.msorder.utils.JwtUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtils jwtUtils;

    public UserResponse getUser(){

        Jwt jwt = jwtUtils.getToken();

        return UserResponse.builder()
                .userId(UUID.fromString(jwt.getClaimAsString("userId")))
                .email(jwt.getClaimAsString("email"))
                .firstName(jwt.getClaimAsString("firstName"))
                .lastName(jwt.getClaimAsString("lastName"))
                .build();
    }
}
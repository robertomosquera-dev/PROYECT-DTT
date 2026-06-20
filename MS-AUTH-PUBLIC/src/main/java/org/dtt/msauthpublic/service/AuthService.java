package org.dtt.msauthpublic.service;

import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.Utils.JwtUtils;
import org.dtt.msauthpublic.dto.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final UserDetailsServiceImpl userDetails;

    public RegisterResponse registerUser(RegisterRequest registerRequest){
        return userService.saveUser(registerRequest);
    }

    public AuthResponse loginUser(LoginRequest loginRequest){
        return userDetails.authentication(loginRequest.username(),loginRequest.password());
    }

    public AuthResponse refreshToken(String refreshToken){
        return userDetails.refreshToken(refreshToken);
    }

}

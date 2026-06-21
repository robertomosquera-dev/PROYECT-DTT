package org.dtt.msauthpublic.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.ApiServiceRequest;
import org.dtt.msauthpublic.dto.ApiServiceResponse;
import org.dtt.msauthpublic.dto.ServiceAuthRequest;
import org.dtt.msauthpublic.model.RefreshToken;
import org.dtt.msauthpublic.service.ApiServiceService;
import org.dtt.msauthpublic.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/service")
@RequiredArgsConstructor
@Tag(
        name = "Service Authentication",
        description = "Endpoints for internal microservice authentication"
)
public class AuthSystemController {

    private final ApiServiceService apiServiceService;

    @Operation(
            summary = "Register internal service",
            description = "Registers a new internal microservice."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    @PostMapping("/register")
    public ApiServiceResponse registerService(
            @RequestBody ApiServiceRequest request
    ) {
        return apiServiceService.createApi(request);
    }

    @Operation(
            summary = "Generate service token",
            description = "Authenticates internal services using API KEY credentials."
    )
    @PostMapping("/token")
    public Map<String, String> getToken(
            @RequestHeader("X-SERVICE-NAME") String name,
            @RequestHeader("X-API-KEY") String apiKey
    ) {

        String token = apiServiceService.authenticateService(
                name,
                apiKey
        );

        return Map.of("token", token);
    }
}

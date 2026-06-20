package org.dtt.msauthpublic.service;

import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.Utils.JwtUtils;
import org.dtt.msauthpublic.dto.ApiServiceRequest;
import org.dtt.msauthpublic.dto.ApiServiceResponse;
import org.dtt.msauthpublic.model.ApiService;
import org.dtt.msauthpublic.repository.ApiServiceRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiServiceService {

    private final ApiServiceRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public ApiServiceResponse createApi(ApiServiceRequest request) {

        if (repository.existsByName(request.name())) {
            throw new RuntimeException("Service already exists");
        }

        String rawApiKey = UUID.randomUUID().toString();

        ApiService service = new ApiService();
        service.setName(request.name());
        service.setApiKeyHash(passwordEncoder.encode(rawApiKey));

        repository.save(service);

        return new ApiServiceResponse(
                service.getName(),
                rawApiKey,
                service.isEnabled()
        );
    }

    public String authenticateService(String name, String apiKey) {

        ApiService service = repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!service.isEnabled()) {
            throw new RuntimeException("Service disabled");
        }

        if (!passwordEncoder.matches(apiKey, service.getApiKeyHash())) {
            throw new RuntimeException("Invalid API KEY");
        }

        return jwtUtils.generateSystemToken(service.getName());
    }
}
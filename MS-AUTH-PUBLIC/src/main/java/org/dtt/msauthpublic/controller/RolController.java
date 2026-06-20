package org.dtt.msauthpublic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.RolResponse;
import org.dtt.msauthpublic.service.RolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/rol")
@RestController
@RequiredArgsConstructor
@Tag(
        name = "Roles",
        description = "Endpoints for role management"
)
public class RolController {

    private final RolService rolService;

    @Operation(
            summary = "Get all roles",
            description = "Returns the list of available roles."
    )
    @GetMapping
    public List<RolResponse> getList() {
        return rolService.ListRol();
    }
}
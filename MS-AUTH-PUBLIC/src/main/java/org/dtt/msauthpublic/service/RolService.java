package org.dtt.msauthpublic.service;

import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.dto.RolResponse;
import org.dtt.msauthpublic.model.Permissions;
import org.dtt.msauthpublic.model.PermissionsNames;
import org.dtt.msauthpublic.model.Roles;
import org.dtt.msauthpublic.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RoleRepository roleRepository;

    public List<RolResponse> ListRol(){

        List<Roles> roles = roleRepository.findAll();

        return roles
                .stream()
                .map(rol -> RolResponse
                        .builder()
                        .id(rol.getId())
                        .name(rol.getName().name())
                        .permissions(
                                rol.getPermissions()
                                        .stream()
                                        .map(Permissions::getName)
                                        .map(PermissionsNames::name)
                                        .collect(Collectors.joining(", "))
                        )
                        .build()
                )
                .toList();

    }

}

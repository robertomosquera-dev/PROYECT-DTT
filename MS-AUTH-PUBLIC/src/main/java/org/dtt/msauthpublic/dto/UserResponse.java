package org.dtt.msauthpublic.dto;


import lombok.Builder;
import org.dtt.msauthpublic.model.RoleName;

import java.util.List;
import java.util.UUID;

@Builder
public record UserResponse (
        UUID id,
        String name,
        String surname,
        String phone,
        String address,
        List<RoleName> roles
){
}

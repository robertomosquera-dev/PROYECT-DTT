package org.dtt.msauthpublic.Utils;


import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

@Getter
public class CustomerUserDetails extends User {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;

    public CustomerUserDetails(
            UUID userId,
            String email,
            String firstName,
            String lastName,
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.id = userId; // ✅ FIX
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
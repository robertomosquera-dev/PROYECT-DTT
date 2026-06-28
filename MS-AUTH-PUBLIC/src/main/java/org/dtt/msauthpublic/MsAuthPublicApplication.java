package org.dtt.msauthpublic;

import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.Utils.JwtProperties;
import org.dtt.msauthpublic.model.*;
import org.dtt.msauthpublic.repository.PermissionsRepository;
import org.dtt.msauthpublic.repository.RoleRepository;
import org.dtt.msauthpublic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients
public class MsAuthPublicApplication {

    @Value("${security.username}")
    private String username;
    @Value("${security.password}")
    private String password;



    public static void main(String[] args) {
        SpringApplication.run(MsAuthPublicApplication.class, args);
    }

    @Bean
    CommandLineRunner initData(RoleRepository rolesRepository, PermissionsRepository permissionsRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (rolesRepository.count() > 0) return;

            Permissions create = permissionsRepository.save(new Permissions(null, PermissionsNames.CREATE));
            Permissions read   = permissionsRepository.save(new Permissions(null, PermissionsNames.READ));
            Permissions update = permissionsRepository.save(new Permissions(null, PermissionsNames.UPDATE));
            Permissions delete = permissionsRepository.save(new Permissions(null, PermissionsNames.DELETE));

            Roles userRole = new Roles();
            userRole.setName(RoleName.USER);
            userRole.setPermissions(Set.of(read));

            Roles adminRole = new Roles();
            adminRole.setName(RoleName.ADMIN);
            adminRole.setPermissions(Set.of(create, read, update));

            Roles superAdminRole = new Roles();
            superAdminRole.setName(RoleName.SUPER_ADMIN);
            superAdminRole.setPermissions(Set.of(create, read, update, delete));

            rolesRepository.saveAll(List.of(userRole, adminRole, superAdminRole));

            UserDetails superAdminDetails = UserDetails.builder()
                    .name("Super")
                    .surname("Admin")
                    .phone("")
                    .address("")
                    .build();

            User superAdmin = userRepository.save(
                    User.builder()
                            .email("superadmin@rbcode.xyz")
                            .username(username)
                            .password(passwordEncoder.encode(password))
                            .userDetails(superAdminDetails)
                            .roles(Set.of(superAdminRole))
                            .build()
            );

            superAdmin.setVerified(true);
            userRepository.save(superAdmin);
        };
    }
}

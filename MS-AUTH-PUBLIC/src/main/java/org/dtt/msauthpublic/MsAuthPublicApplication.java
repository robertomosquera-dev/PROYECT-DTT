package org.dtt.msauthpublic;

import lombok.RequiredArgsConstructor;
import org.dtt.msauthpublic.Utils.JwtProperties;
import org.dtt.msauthpublic.model.Permissions;
import org.dtt.msauthpublic.model.PermissionsNames;
import org.dtt.msauthpublic.model.RoleName;
import org.dtt.msauthpublic.model.Roles;
import org.dtt.msauthpublic.repository.PermissionsRepository;
import org.dtt.msauthpublic.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Set;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
@RequiredArgsConstructor
public class MsAuthPublicApplication {


    public static void main(String[] args) {
        SpringApplication.run(MsAuthPublicApplication.class, args);
    }
    @Bean
    CommandLineRunner initData(RoleRepository rolesRepository, PermissionsRepository permissionsRepository) {
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
        };
    }
}

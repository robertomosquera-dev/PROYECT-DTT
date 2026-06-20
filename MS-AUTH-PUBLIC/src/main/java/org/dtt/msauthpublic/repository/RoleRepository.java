package org.dtt.msauthpublic.repository;

import org.dtt.msauthpublic.model.Roles;
import org.dtt.msauthpublic.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Roles, UUID> {

    Set<Roles> findAllByIdIn(Set<UUID> ids);
    
    Optional<Roles> findByName(RoleName name);

}

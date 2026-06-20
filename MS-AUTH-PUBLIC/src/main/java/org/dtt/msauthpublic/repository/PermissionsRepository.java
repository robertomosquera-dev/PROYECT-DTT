package org.dtt.msauthpublic.repository;

import org.dtt.msauthpublic.model.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, UUID> {
}

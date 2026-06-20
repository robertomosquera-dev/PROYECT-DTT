package org.dtt.msauthpublic.repository;

import org.dtt.msauthpublic.model.User;
import org.dtt.msauthpublic.repository.projections.AuthorityProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByIdAndEnabledIsTrue(UUID id);

    @Query(value = """
    SELECT DISTINCT authority, username
    FROM (
        SELECT
            CONCAT('ROLE_', r.name) AS authority,
            u.username
        FROM users u
        JOIN user_roles ur ON ur.user_id = u.id
        JOIN roles r ON r.id = ur.role_id

        UNION ALL

        SELECT
            p.name AS authority,
            u.username
        FROM users u
        JOIN user_roles ur ON ur.user_id = u.id
        JOIN role_permissions rp ON rp.role_id = ur.role_id
        JOIN permissions p ON p.id = rp.permission_id
    ) final
    WHERE username = :username
    """, nativeQuery = true)
    List<AuthorityProjection> findAuthorities(String username);
}

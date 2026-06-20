package org.dtt.msauthpublic.repository;

import org.dtt.msauthpublic.model.ApiService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiServiceRepository extends JpaRepository<ApiService, UUID> {

    Optional<ApiService> findByName(String name);

    boolean existsByName(String name);
}

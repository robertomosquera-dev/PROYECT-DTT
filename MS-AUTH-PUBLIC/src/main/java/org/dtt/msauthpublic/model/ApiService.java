package org.dtt.msauthpublic.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiService {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String apiKeyHash;

    private boolean enabled;

    public ApiService(String name) {
        this.name = name;
    }

    @PrePersist
    private void init() {
        if (this.id == null) this.id = UUID.randomUUID();
        this.enabled = true;
    }
}

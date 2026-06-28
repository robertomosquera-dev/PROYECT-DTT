package org.dtt.msauthpublic.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Table( name = "users")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    private UUID id;
    @Column(unique = true,nullable = false)
    private String email;
    @Column(unique = true,nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Embedded
    private UserDetails userDetails;

    private boolean verified;
    private String verificationCode;
    private Instant verificationCodeIssuedAt;
    private Instant verificationCodeExpiration;

    @CreationTimestamp
    private Instant createdAt;

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;
    private boolean accountNonLocked;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Roles> roles;

    @PrePersist
    private void init(){
        if (this.id == null) this.id = UuidCreator.getTimeOrderedEpoch();
        this.enabled = true;
        this.verified = false;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.accountNonLocked = true;
    }
}

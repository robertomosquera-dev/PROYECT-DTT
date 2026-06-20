package org.dtt.msauthpublic.model;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table( name = "permissions")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permissions {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PermissionsNames name;

    @PrePersist
    private void init(){
        if (this.id == null) this.id = UuidCreator.getTimeOrderedEpoch();
    }

}

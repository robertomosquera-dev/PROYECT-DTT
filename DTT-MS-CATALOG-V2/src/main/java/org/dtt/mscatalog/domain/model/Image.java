package org.dtt.mscatalog.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.dtt.mscatalog.domain.exception.InvalidImageOrderException;
import org.dtt.mscatalog.domain.exception.InvalidImageUrlException;
import org.dtt.mscatalog.domain.model.Enum.OwnerType; // Importa tu Enum

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Image {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UUID ownerId;
    private Short order;
    private OwnerType ownerType;

    private String url;


    public void setOrder(Short order) {
        if (order == null) {
            throw new InvalidImageOrderException();
        }
        this.order = order;
        this.updatedAt = LocalDateTime.now();
    }

    public void refreshUrl(String url) {
        if (url.isBlank()) {
            throw new InvalidImageUrlException();
        }
        this.url = url;
        this.updatedAt = LocalDateTime.now();
    }

}
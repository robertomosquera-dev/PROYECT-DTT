package org.dtt.mscatalog.infrastructure.mapper;

import org.dtt.mscatalog.application.dto.request.ImageRequest;
import org.dtt.mscatalog.application.dto.response.ImageResponse;
import org.dtt.mscatalog.domain.model.Image;
import org.dtt.mscatalog.infrastructure.persistence.entity.ImageEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper {


    @BeanMapping(
            nullValueMappingStrategy =  NullValueMappingStrategy.RETURN_NULL
    )
    ImageEntity toEntity(Image image);

    Image toDomain(ImageEntity imageEntity);
    ImageResponse toResponse(Image image);

    List<Image> toDomain(List<ImageEntity> imageEntities);
    List<ImageEntity> toEntity(List<Image> images);

}

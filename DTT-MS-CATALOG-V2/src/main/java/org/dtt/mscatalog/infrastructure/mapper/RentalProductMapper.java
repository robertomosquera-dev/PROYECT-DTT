package org.dtt.mscatalog.infrastructure.mapper;

import org.dtt.mscatalog.application.dto.request.Inventory.RentalProductRequest;
import org.dtt.mscatalog.application.dto.response.ImageResponse;
import org.dtt.mscatalog.application.dto.response.Inventory.RentalProductResponse;
import org.dtt.mscatalog.application.service.ImageService;
import org.dtt.mscatalog.domain.model.RentalProduct;
import org.dtt.mscatalog.infrastructure.persistence.entity.RentalProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",uses = {ProductMapper.class})
public interface RentalProductMapper {

    RentalProductEntity toEntity(RentalProduct product);
    List<RentalProductEntity> toEntity(List<RentalProduct> products);
    RentalProduct toDomain(RentalProductEntity entity);
    List<RentalProduct> toDomain(List<RentalProductEntity> entities);



    @Mapping(target = "product", ignore = true)
    RentalProduct toDomain(RentalProductRequest request);

    @Mapping(target = "productId", source = "product.product.id")
    @Mapping(target = "images", source = "images")
    RentalProductResponse toResponse(RentalProduct product, List<ImageResponse>images);
}

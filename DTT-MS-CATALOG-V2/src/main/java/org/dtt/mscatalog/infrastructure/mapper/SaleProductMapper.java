package org.dtt.mscatalog.infrastructure.mapper;

import org.dtt.mscatalog.application.dto.request.Inventory.SaleProductRequest;
import org.dtt.mscatalog.application.dto.response.ImageResponse;
import org.dtt.mscatalog.application.dto.response.Inventory.SaleProductResponse;
import org.dtt.mscatalog.application.dto.response.ProductImgResponse;
import org.dtt.mscatalog.domain.model.SaleProduct;
import org.dtt.mscatalog.infrastructure.persistence.entity.SaleProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring",uses = {ProductMapper.class})
public interface SaleProductMapper {

    @Mapping(target = "product", ignore = true)
    SaleProduct toDomain(SaleProductRequest saleProductRequest);

    @Mapping(target = "productId", source = "saleProduct.product.id")
    @Mapping(target = "images", source = "imageResponses")
    SaleProductResponse toResponse(SaleProduct saleProduct, List<ImageResponse>imageResponses);


    SaleProductEntity toEntity(SaleProduct saleProduct);

    List<SaleProductEntity> toEntity(List<SaleProduct> saleProducts);

    SaleProduct toDomain(SaleProductEntity entity);
    List<SaleProduct> toDomain(List<SaleProductEntity> entities);
}

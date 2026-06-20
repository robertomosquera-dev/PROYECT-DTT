package org.dtt.mscatalog.infrastructure.mapper;

import org.dtt.mscatalog.application.dto.request.Inventory.ProductBundleRequest;
import org.dtt.mscatalog.application.dto.response.BundleItemResponse;
import org.dtt.mscatalog.application.dto.response.ImageResponse;
import org.dtt.mscatalog.application.dto.response.Inventory.ProductBundleResponse;
import org.dtt.mscatalog.application.dto.response.ProductImgResponse;
import org.dtt.mscatalog.domain.model.BundleItem;
import org.dtt.mscatalog.domain.model.ProductBundle;
import org.dtt.mscatalog.infrastructure.persistence.entity.BundleItemEntity;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductBundleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, SaleProductMapper.class})
public interface ProductBundleMapper {

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "items", ignore = true)
    ProductBundle toDomain(ProductBundleRequest request);

    ProductBundle toDomain(ProductBundleEntity entity);

    ProductBundleEntity toEntity(ProductBundle productBundle);

    @Mapping(target = "bundle", ignore = true)
    BundleItemEntity toEntity(BundleItem bundleItem);

    @Mapping(target = "bundle", ignore = true)
    BundleItem toDomain(BundleItemEntity bundleItemEntity);

    @Mapping(target = "productSaleId", source = "saleProduct.id")
    BundleItemResponse toItemResponse(BundleItem bundleItem);

    ProductBundleResponse toResponse(ProductBundle productBundle, List<ImageResponse> images);

    ProductImgResponse toProductImgResponse(ImageResponse imageResponse);

    List<ProductBundle> toDomain(List<ProductBundleEntity> entities);
    List<ProductBundleEntity> toEntity(List<ProductBundle> productBundles);
}
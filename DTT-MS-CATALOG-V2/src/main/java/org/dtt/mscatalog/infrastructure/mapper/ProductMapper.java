package org.dtt.mscatalog.infrastructure.mapper;

import org.dtt.mscatalog.application.dto.request.ProductRequest;
import org.dtt.mscatalog.application.dto.response.*;
import org.dtt.mscatalog.domain.model.Category;
import org.dtt.mscatalog.domain.model.Product;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    @BeanMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
    @Mapping(target = "categories", source = "categories")
    ProductEntity toEntity(Product product);

    @Mapping(target = "categories", source = "product.categories", qualifiedByName = "toCategorySlug")
    @Mapping(target = "images", source = "imageResponses")
    ProductResponse toResponse(Product product, List<ImageResponse> imageResponses);

    ProductImgResponse toProductImgResponse(ImageResponse imageResponse);

    @Mapping(source = "product.id", target = "id")
    @Mapping(source = "primaryImage", target = "image")
    ProductDetailsResponse toDetailsResponse(Product product, ImageResponse primaryImage);


    @Mapping(target = "categories", ignore = true)
    Product toDomain(ProductRequest productRequest);


    Product toDomain(ProductEntity productEntity);


    @Named("toCategorySlug")
    default List<CategoryDetailsResponse> toCategorySlug(Set<Category> categories) {
        if (categories == null) return java.util.Collections.emptyList();

        return categories.stream()
                .map(category -> CategoryDetailsResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .path(category.getPath())
                        .build())
                .toList();
    }

}

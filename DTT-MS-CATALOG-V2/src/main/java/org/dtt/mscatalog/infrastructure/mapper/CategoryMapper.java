package org.dtt.mscatalog.infrastructure.mapper;

import org.dtt.mscatalog.application.dto.request.CategoryRequest;
import org.dtt.mscatalog.application.dto.response.CategoryResponse;
import org.dtt.mscatalog.domain.model.Category;
import org.dtt.mscatalog.infrastructure.persistence.entity.CategoryEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @BeanMapping(
            nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
            builder = @Builder(disableBuilder = true)
    )
    @Mapping(target = "parent", ignore = true)
    CategoryEntity toEntity(Category category);

    List<CategoryEntity>toEntity(List<Category>categories);

    Category toDomain(CategoryEntity categoryEntity);

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "imgUrl", ignore = true)
    CategoryResponse toResponse(Category category);

    Category toDomain(CategoryRequest categoryRequest);
}

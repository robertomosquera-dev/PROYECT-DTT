package org.dtt.mscatalog.application.port.in.categoryUseCase;

import org.dtt.mscatalog.application.dto.response.CategoryResponse;

import java.util.UUID;

public interface FindCategoryUseCase {
    CategoryResponse findById(UUID id);
    CategoryResponse findBySlug(String slug);
    CategoryResponse findByName(String name);
    CategoryResponse findByPath(String path);
}

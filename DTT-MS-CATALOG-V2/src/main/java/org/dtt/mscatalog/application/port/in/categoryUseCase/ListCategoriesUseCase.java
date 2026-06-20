package org.dtt.mscatalog.application.port.in.categoryUseCase;

import org.dtt.mscatalog.application.dto.CategoryFilter;
import org.dtt.mscatalog.application.dto.request.ChangeStatusRequest;
import org.dtt.mscatalog.application.dto.response.CategoryResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

// Nuevo caso de uso único
public interface ListCategoriesUseCase {
    List<CategoryResponse> listCategories(CategoryFilter filter, PageRequest pageRequest);
}
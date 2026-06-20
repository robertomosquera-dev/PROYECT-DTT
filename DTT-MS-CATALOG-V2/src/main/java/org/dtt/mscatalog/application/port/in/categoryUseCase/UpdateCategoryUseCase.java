package org.dtt.mscatalog.application.port.in.categoryUseCase;

import org.dtt.mscatalog.application.dto.request.UpdateCategoryBasicInfoRequest;
import org.dtt.mscatalog.application.dto.response.CategoryResponse;

import java.util.UUID;

public interface UpdateCategoryUseCase {
    CategoryResponse updateCategory(UUID id, UpdateCategoryBasicInfoRequest updateCategoryBasicInfoRequest);
}

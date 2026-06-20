package org.dtt.mscatalog.application.port.in.categoryUseCase;

import org.dtt.mscatalog.application.dto.request.CategoryRequest;
import org.dtt.mscatalog.application.dto.response.CategoryResponse;
import org.dtt.mscatalog.domain.model.Category;
import org.springframework.web.multipart.MultipartFile;

public interface CreateCategoryUseCase {
    CategoryResponse createCategory(CategoryRequest categoryRequest, MultipartFile image);
}

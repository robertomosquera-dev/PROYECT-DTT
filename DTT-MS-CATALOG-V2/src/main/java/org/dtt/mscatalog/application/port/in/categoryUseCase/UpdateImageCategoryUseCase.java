package org.dtt.mscatalog.application.port.in.categoryUseCase;

import org.dtt.mscatalog.application.dto.response.CategoryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UpdateImageCategoryUseCase {
    CategoryResponse updateCategoryImage(UUID id, MultipartFile image);
}

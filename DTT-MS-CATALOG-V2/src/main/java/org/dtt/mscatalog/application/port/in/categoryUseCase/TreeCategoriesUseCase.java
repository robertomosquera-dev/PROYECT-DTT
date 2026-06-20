package org.dtt.mscatalog.application.port.in.categoryUseCase;

import org.dtt.mscatalog.application.dto.response.CategoryTreeResponse;

import java.util.UUID;

public interface TreeCategoriesUseCase {
    CategoryTreeResponse getTreeCategoriesByParentId(UUID parentId);
}

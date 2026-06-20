package org.dtt.mscatalog.application.port.in.categoryUseCase;

import java.util.UUID;

public interface DeleteCategoryUseCase {
    void deleteCategory(UUID id);
}

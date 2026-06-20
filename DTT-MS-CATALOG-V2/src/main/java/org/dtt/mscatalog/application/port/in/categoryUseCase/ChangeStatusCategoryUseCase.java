package org.dtt.mscatalog.application.port.in.categoryUseCase;

import org.dtt.mscatalog.application.dto.request.StatusRequest;

import java.util.UUID;

public interface ChangeStatusCategoryUseCase {
    void changeStatusCategory(UUID id, StatusRequest status);
}

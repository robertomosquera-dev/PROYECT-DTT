package org.dtt.mscatalog.application.port.in.productUseCase;


import org.dtt.mscatalog.application.dto.request.StatusRequest;

import java.util.UUID;

public interface ChangeStatusProductUseCase {
    void changeStatusProduct(UUID id,StatusRequest status);
}

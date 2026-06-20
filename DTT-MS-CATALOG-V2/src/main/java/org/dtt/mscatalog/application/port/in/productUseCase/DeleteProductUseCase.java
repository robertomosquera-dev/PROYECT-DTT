package org.dtt.mscatalog.application.port.in.productUseCase;

import java.util.UUID;

public interface DeleteProductUseCase {
    void deleteProduct(UUID id);
}

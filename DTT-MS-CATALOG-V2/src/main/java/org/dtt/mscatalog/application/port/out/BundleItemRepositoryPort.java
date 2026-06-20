package org.dtt.mscatalog.application.port.out;

import java.util.UUID;

public interface BundleItemRepositoryPort {
    void deleteById(UUID id);
}

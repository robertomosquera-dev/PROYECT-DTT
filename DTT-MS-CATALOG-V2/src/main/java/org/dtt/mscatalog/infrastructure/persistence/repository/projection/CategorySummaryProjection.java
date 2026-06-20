package org.dtt.mscatalog.infrastructure.persistence.repository.projection;

import java.util.UUID;

public interface CategorySummaryProjection {
    UUID getId();
    String getName();
    String getPath();
}

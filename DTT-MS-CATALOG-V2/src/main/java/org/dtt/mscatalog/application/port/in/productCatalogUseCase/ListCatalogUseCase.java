package org.dtt.mscatalog.application.port.in.productCatalogUseCase;

import org.dtt.mscatalog.application.dto.ProductCatalogFilter;
import org.dtt.mscatalog.application.dto.response.ProductCatalogResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ListCatalogUseCase {
    List<ProductCatalogResponse> listProductCatalog(ProductCatalogFilter filter, PageRequest pageRequest);
}

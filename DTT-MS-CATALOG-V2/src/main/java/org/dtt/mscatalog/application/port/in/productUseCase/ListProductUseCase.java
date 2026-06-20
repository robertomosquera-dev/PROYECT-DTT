package org.dtt.mscatalog.application.port.in.productUseCase;

import org.dtt.mscatalog.application.dto.ProductFilter;
import org.dtt.mscatalog.application.dto.request.StatusRequest;
import org.dtt.mscatalog.application.dto.response.ProductDetailsResponse;
import org.dtt.mscatalog.application.dto.response.ProductResponse;
import org.dtt.mscatalog.domain.model.Product;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public interface ListProductUseCase {
    List<ProductDetailsResponse> listProducts(ProductFilter filter, PageRequest pageRequest);
}

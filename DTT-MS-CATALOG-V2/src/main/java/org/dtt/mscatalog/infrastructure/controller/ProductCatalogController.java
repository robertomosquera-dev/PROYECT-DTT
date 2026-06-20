package org.dtt.mscatalog.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.ProductCatalogFilter;
import org.dtt.mscatalog.application.dto.response.ProductCatalogResponse;
import org.dtt.mscatalog.application.service.ProductCatalogService;
import org.dtt.mscatalog.domain.model.Enum.ProductStatus;
import org.dtt.mscatalog.domain.model.Enum.ProductType;
import org.dtt.mscatalog.infrastructure.utils.ConsumerResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class ProductCatalogController {

    private final ProductCatalogService productCatalogService;

    @GetMapping
    public ConsumerResponse<List<ProductCatalogResponse>> list(
            @RequestParam(required = false) ProductType skuType,
            @RequestParam(required = false) List<String> categorySlug,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)ProductStatus ProductStatus
    ) {

        ProductCatalogFilter filter = new ProductCatalogFilter(
                skuType,
                categorySlug,
                minPrice,
                maxPrice,
                ProductStatus
        );

        return ConsumerResponse.success(
                productCatalogService.listProductCatalog(
                        filter,
                        PageRequest.of(page, size)
                )
        );
    }

    @GetMapping("/{id}")
    public Object findById(
            @PathVariable UUID id,
            @RequestParam ProductType type
    ) {
        return productCatalogService.findById(id, type);
    }

}
package org.dtt.mscatalog.application.service;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.ISellableInventory;
import org.dtt.mscatalog.application.dto.ProductCatalogFilter;
import org.dtt.mscatalog.application.dto.response.ProductCatalogResponse;
import org.dtt.mscatalog.application.port.in.productCatalogUseCase.FindProductCatalogUseCase;
import org.dtt.mscatalog.application.port.in.productCatalogUseCase.ListCatalogUseCase;
import org.dtt.mscatalog.application.port.out.ProductCatalogRepositoryPort;
import org.dtt.mscatalog.domain.model.Enum.ProductType;
import org.dtt.mscatalog.infrastructure.mapper.ProductCatalogMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductCatalogEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing operations related to the product catalog, such as listing
 * and retrieving product catalog details. It delegates operations to appropriate sub-services
 * based on the type of product and interacts with repository ports for data access.
 * This class implements {@link ListCatalogUseCase} and {@link FindProductCatalogUseCase}.
 *
 * Main capabilities of this service:
 * - Listing products based on filters and pagination.
 * - Finding product details by ID and type.
 * - Fetching multiple products by their IDs.
 *
 * Caching is applied to key methods to enhance performance for frequently accessed data.
 * Dependencies used by this service include repository ports, sub-services for specific product
 * types, and a mapper for transforming data between domain and response representations.
 *
 * Constructor injection is employed to ensure immutability and testability.
 */
@Service
@RequiredArgsConstructor
public class ProductCatalogService implements ListCatalogUseCase, FindProductCatalogUseCase {

    private final ProductCatalogRepositoryPort productCatalogRepositoryPort;
    private final SaleProductService saleProductService;
    private final RentalProductService rentalProductService;
    private final ProductBundleService productBundleService;
    private final ProductCatalogMapper productCatalogMapper;

    /**
     * Retrieves a paginated list of products in the catalog based on the provided filter and pagination information.
     * This method uses caching to enhance performance for frequently accessed data.
     *
     * @param filter the product catalog filter that specifies the criteria for filtering the products,
     *               including attributes such as SKU type, category slugs, price range, and product status.
     * @param pageRequest the pagination information that determines the page number and size for the results.
     * @return a list of {@code ProductCatalogResponse} objects representing the products in the catalog
     *         that match the given filter and pagination criteria.
     */
    @Override
    @Cacheable(value = "catalog-list", key = "#filter.toString() + '_' + #pageRequest.toString()")
    public List<ProductCatalogResponse> listProductCatalog(ProductCatalogFilter filter, PageRequest pageRequest) {
        var products = productCatalogRepositoryPort.findAll(filter, pageRequest);
        return productCatalogMapper.toResponse(products);
    }

    /**
     * Retrieves a sellable inventory item based on its unique identifier and product type.
     * The method delegates the retrieval process to the corresponding service based on the
     * specified product type (SALE, BUNDLE, or RENTAL).
     *
     * Caching is applied to enhance performance by storing the result in a cache with a
     * composite key derived from the product ID and type.
     *
     * @param id   the unique identifier of the product to find
     * @param type the type of the product (e.g., SALE, BUNDLE, RENTAL)
     * @return an {@code ISellableInventory} object representing the product details, including
     *         price and stock information
     */
    @Override
    @Cacheable(value = "catalog", key = "#id + '_' + #type")
    public ISellableInventory findById(UUID id, ProductType type) {
        return switch (type) {
            case SALE -> saleProductService.findById(id);
            case BUNDLE -> productBundleService.findById(id);
            case RENTAL -> rentalProductService.findById(id);
        };
    }

    /**
     * Retrieves a list of product catalog entities based on their unique identifiers.
     *
     * @param ids a list of UUIDs representing the unique identifiers of the products to be retrieved.
     * @return a list of {@code ProductCatalogEntity} objects corresponding to the provided IDs.
     */
    public List<ProductCatalogEntity> listProductByIds(List<UUID> ids) {
        return productCatalogRepositoryPort.findAllByIds(ids);
    }
}
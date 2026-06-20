package org.dtt.mscatalog.application.service;

import jakarta.transaction.Transactional;
import org.dtt.mscatalog.application.dto.request.Inventory.SaleProductRequest;
import org.dtt.mscatalog.application.dto.request.Inventory.SaleProductUpdateRequest;
import org.dtt.mscatalog.application.dto.request.ItemRequest;
import org.dtt.mscatalog.application.dto.response.ImageResponse;
import org.dtt.mscatalog.application.dto.response.Inventory.SaleProductResponse;
import org.dtt.mscatalog.application.exception.ProductAlreadyInSaleException;
import org.dtt.mscatalog.application.exception.ProductNotFoundException;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ActivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.DeactivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ReplenishStockUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.WithdrawStockUseCase;
import org.dtt.mscatalog.application.port.in.saleProductUseCase.CreateSaleUseCase;
import org.dtt.mscatalog.application.port.in.saleProductUseCase.UpdateSaleUseCase;
import org.dtt.mscatalog.application.port.out.ProductRepositoryPort;
import org.dtt.mscatalog.application.port.out.SaleProductRepositoryPort;
import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.dtt.mscatalog.domain.model.Enum.ProductType;
import org.dtt.mscatalog.domain.model.Product;
import org.dtt.mscatalog.domain.model.ReservationItemStock;
import org.dtt.mscatalog.domain.model.SaleProduct;
import org.dtt.mscatalog.infrastructure.mapper.SaleProductMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SaleProductService is a service class responsible for managing the sale product
 * inventory, including creating, updating, retrieving, processing stock operations,
 * and managing stock cancellation. This class extends BaseInventoryService and
 * implements various use cases for product management.
 *
 * It provides the following functionalities:
 *
 * - Creating new sale products.
 * - Updating existing sale products based on their ID.
 * - Retrieving sale product details by ID.
 * - Processing stock for reservations by withdrawing stock quantities for specific products.
 * - Canceling stock reservations and replenishing the stock accordingly.
 *
 * The service also integrates with external services such as ImageService for managing
 * product-related images and SaleProductMapper for mapping between domain and response/request models.
 *
 * Cache invalidation is performed for relevant caches such as "catalog-list" and "catalog"
 * whenever a write operation affects the inventory.
 *
 * Dependencies:
 * - SaleProductRepositoryPort: Repository interface for managing sale products.
 * - ProductRepositoryPort: Repository interface for managing product data.
 * - SaleProductMapper: Mapper for domain and DTO conversions.
 * - ImageService: Service for managing product-related images.
 *
 * Exceptions:
 * - ProductAlreadyInSaleException: Thrown when attempting to create a sale product that already exists.
 * - ProductNotFoundException: Thrown when the specified product or sale product cannot be found.
 * - IllegalArgumentException: Thrown when request parameters or data inconsistencies cause processing failures.
 */
@Service
@Transactional
public class SaleProductService extends BaseInventoryService<SaleProduct, SaleProductRepositoryPort> implements IProductBaseService<SaleProductResponse, SaleProductRequest, SaleProductUpdateRequest, UUID>, CreateSaleUseCase, UpdateSaleUseCase, ActivateInventoryItemUseCase, DeactivateInventoryItemUseCase, ReplenishStockUseCase, WithdrawStockUseCase {

    private final SaleProductMapper mapper;
    private final ImageService imageService;

    public SaleProductService(SaleProductRepositoryPort repository, ProductRepositoryPort productRepository, SaleProductMapper mapper, ImageService imageService) {
        super(repository, productRepository);
        this.mapper = mapper;
        this.imageService = imageService;
    }

    /**
     * Creates a new sale product based on the provided request data.
     * Evicts cache entries for "catalog-list" and "catalog" to ensure data consistency.
     *
     * @param request the request object containing sale product creation details.
     *                It includes information such as the product ID and sale specifications.
     * @return a response object representing the created sale product, including
     *         associated images and other sale details.
     * @throws ProductAlreadyInSaleException if the product is already marked as being on sale.
     * @throws ProductNotFoundException if the product with the specified ID does not exist.
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public SaleProductResponse create(SaleProductRequest request) {

        if (repository.existsByProductId(request.productId())) {
            throw new ProductAlreadyInSaleException();
        }

        SaleProduct saleProduct = mapper.toDomain(request);

        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new ProductNotFoundException(request.productId()));

        saleProduct.assignProduct(product);

        List<ImageResponse> images = imageService.listImagesByOwnerId(product.getId(), OwnerType.PRODUCT);

        return mapper.toResponse(repository.save(saleProduct), images);

    }

    /**
     * Updates the given SaleProduct entity with the provided update request.
     * This method will update the price and/or stock of the product if specified,
     * and it evicts all cache entries associated with "catalog-list" and "catalog".
     *
     * @param id the unique identifier of the SaleProduct to be updated
     * @param request the request object containing the fields to update, such as price or stock
     * @return a response object containing the updated SaleProduct details, including associated images
     * @throws ProductNotFoundException if no SaleProduct is found with the specified id
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public SaleProductResponse update(UUID id, SaleProductUpdateRequest request) {

        SaleProduct saleProduct = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        if (request.price() != null) {
            saleProduct.updatePrice(request.price());
        }

        if (request.stock() != null) {
            saleProduct.updateStock(request.stock());
        }

        List<ImageResponse> images = imageService.listImagesByOwnerId(saleProduct.getProduct().getId(), OwnerType.PRODUCT);


        return mapper.toResponse(repository.save(saleProduct), images);
    }

    /**
     * Finds a sale product by its unique identifier (UUID).
     *
     * @param id the unique identifier of the sale product to be retrieved
     * @return a {@code SaleProductResponse} containing the details of the found sale product and its associated images
     * @throws ProductNotFoundException if no sale product with the specified {@code id} is found
     */
    @Override
    public SaleProductResponse findById(UUID id) {

        SaleProduct saleProduct = repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        List<ImageResponse> images = imageService.listImagesByOwnerId(saleProduct.getProduct().getId(), OwnerType.PRODUCT);

        return mapper.toResponse(repository.save(saleProduct), images);
    }

    /**
     * Processes stock for a list of item requests. It identifies sale items, validates their existence, adjusts stock levels,
     * and creates a list of reservation item stocks to reflect the changes. The method also clears relevant cache entries.
     *
     * @param requests the list of item requests to be processed, where each request contains details about the product type and quantity
     * @return a list of {@code ReservationItemStock} objects representing the updated stock reservations for sale products
     * @throws IllegalArgumentException if any of the requested sale products are not found
     * @throws ProductNotFoundException if a specific sale product does not exist in the repository
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public List<ReservationItemStock> processStock(List<ItemRequest> requests) {

        Map<UUID, ItemRequest> saleItems = requests.stream().filter(item -> item.type().equals(ProductType.SALE)).collect(Collectors.toMap(ItemRequest::productId, Function.identity()));

        List<UUID> productIds = new ArrayList<>(saleItems.keySet());

        List<SaleProduct> saleProducts = repository.findAllByIdsIn(productIds);

        if (saleProducts.size() != productIds.size()) {
            throw new IllegalArgumentException("Some products not found");
        }

        Map<UUID, SaleProduct> productMap = saleProducts.stream().collect(Collectors.toMap(SaleProduct::getId, Function.identity()));

        List<ReservationItemStock> items = new ArrayList<>();

        for (Map.Entry<UUID, ItemRequest> entry : saleItems.entrySet()) {

            SaleProduct saleProduct = productMap.get(entry.getKey());

            if (saleProduct == null) {
                throw new ProductNotFoundException(entry.getKey());
            }

            saleProduct.withdrawStock(entry.getValue().quantity());

            items.add(ReservationItemStock.builder().productId(saleProduct.getId()).type(ProductType.SALE).quantity(entry.getValue().quantity()).build());
        }
        repository.saveAll(saleProducts);
        return items;
    }

    /**
     * Cancels the stock for a given list of reservation items by replenishing the stock
     * of associated sale products. The method updates the stock quantities in the
     * database and clears related cache entries.
     *
     * @param reservationItems the list of reservation item stocks to be cancelled. Each item
     *                         contains product information and the quantity to be returned
     *                         to stock. Only items of type {@code ProductType.SALE} will
     *                         be processed. Other types are ignored.
     * @throws IllegalArgumentException if some sale products associated with the given reservation
     *                                  items are not found in the system during the cancellation.
     * @throws ProductNotFoundException if any specific sale product is not found when trying to
     *                                  replenish stock for a corresponding reservation item.
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public void cancelStock(List<ReservationItemStock> reservationItems) {

        Map<UUID, Integer> saleItems = reservationItems.stream().filter(item -> ProductType.SALE.equals(item.getType())).collect(Collectors.toMap(ReservationItemStock::getProductId, ReservationItemStock::getQuantity, Integer::sum));

        if (saleItems.isEmpty()) {
            return;
        }

        List<UUID> productIds = new ArrayList<>(saleItems.keySet());

        List<SaleProduct> saleProducts = repository.findAllByIdsIn(productIds);

        if (saleProducts.size() != productIds.size()) {
            throw new IllegalArgumentException("Some sale products not found during cancellation");
        }

        Map<UUID, SaleProduct> productMap = saleProducts.stream().collect(Collectors.toMap(SaleProduct::getId, Function.identity()));

        for (Map.Entry<UUID, Integer> entry : saleItems.entrySet()) {

            SaleProduct saleProduct = productMap.get(entry.getKey());

            if (saleProduct == null) {
                throw new ProductNotFoundException(entry.getKey());
            }

            saleProduct.replenishStock(entry.getValue());
        }

        repository.saveAll(saleProducts);
    }
}

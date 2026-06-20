package org.dtt.mscatalog.application.service;

import org.dtt.mscatalog.application.dto.request.Inventory.RentalProductRequest;
import org.dtt.mscatalog.application.dto.request.Inventory.RentalProductUpdateRequest;
import org.dtt.mscatalog.application.dto.request.ItemRequest;
import org.dtt.mscatalog.application.dto.response.ImageResponse;
import org.dtt.mscatalog.application.dto.response.Inventory.RentalProductResponse;
import org.dtt.mscatalog.application.exception.ProductAlreadyInRentalException;
import org.dtt.mscatalog.application.exception.ProductNotFoundException;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ActivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.DeactivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ReplenishStockUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.WithdrawStockUseCase;
import org.dtt.mscatalog.application.port.in.rentalProductUseCase.CreateRentalUseCase;
import org.dtt.mscatalog.application.port.in.rentalProductUseCase.UpdateRentalUseCase;
import org.dtt.mscatalog.application.port.out.ProductRepositoryPort;
import org.dtt.mscatalog.application.port.out.RentalProductRepositoryPort;
import org.dtt.mscatalog.domain.model.Enum.OwnerType;
import org.dtt.mscatalog.domain.model.Enum.ProductType;
import org.dtt.mscatalog.domain.model.Product;
import org.dtt.mscatalog.domain.model.RentalProduct;
import org.dtt.mscatalog.domain.model.ReservationItemStock;
import org.dtt.mscatalog.infrastructure.mapper.RentalProductMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class for managing rental products in the inventory system.
 * It provides functionalities to create, update, find, activate, deactivate,
 * replenish, and withdraw stock for rental products. This class performs
 * transactional operations and invalidates relevant cache entries
 * after certain operations to ensure data consistency.
 *
 * The service interacts with the repository for data persistence
 * and also integrates with external services like image management for handling
 * associated images of rental products.
 *
 * Inherits common behaviors from {@code BaseInventoryService}.
 * Implements multiple use cases required for managing rental products.
 */
@Service
@Transactional
public class RentalProductService
        extends BaseInventoryService<RentalProduct, RentalProductRepositoryPort>
        implements IProductBaseService<RentalProductResponse, RentalProductRequest, RentalProductUpdateRequest, UUID>,
        CreateRentalUseCase, UpdateRentalUseCase, ActivateInventoryItemUseCase, DeactivateInventoryItemUseCase,
        ReplenishStockUseCase, WithdrawStockUseCase {

    private final RentalProductMapper mapper;
    private final ImageService imageService;

    public RentalProductService(
            RentalProductRepositoryPort repository,
            ProductRepositoryPort productRepository,
            RentalProductMapper mapper,
            ImageService imageService
    ) {
        super(repository, productRepository);
        this.mapper = mapper;
        this.imageService = imageService;
    }

    /**
     * Creates a new rental product entry in the system and evicts relevant cache entries.
     *
     * @param request the request object containing the details of the rental product to be created
     * @return the response object containing the details of the created rental product along with associated images
     * @throws ProductAlreadyInRentalException if a rental product with the same product ID already exists
     * @throws NoSuchElementException if the product with the given ID does not exist in the product repository
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public RentalProductResponse create(RentalProductRequest request) {
        if (repository.existsByProductId(request.productId())) {
            throw new ProductAlreadyInRentalException();
        }
        RentalProduct rentalProduct = mapper.toDomain(request);
        Product product = productRepository
                .findById(request.productId())
                .orElseThrow();
        rentalProduct.assignProduct(product);

        List<ImageResponse> images = imageService.listImagesByOwnerId(rentalProduct.getProduct().getId(), OwnerType.PRODUCT);

        return mapper.toResponse(repository.save(rentalProduct), images);
    }

    /**
     * Updates an existing rental product identified by the provided ID with the details specified in the update request.
     * Invalidates the "catalog-list" and "catalog" cache entries upon successful update.
     *
     * @param id      the unique identifier of the rental product to update
     * @param request the update request containing new values for the rental product's properties
     * @return a {@code RentalProductResponse} object containing the updated rental product details
     * @throws ProductNotFoundException if a rental product with the specified ID is not found
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    @Override
    public RentalProductResponse update(UUID id, RentalProductUpdateRequest request) {

        RentalProduct rentalProduct = repository
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (request.weeklyPrice() != null) {
            rentalProduct.updateWeeklyPrice(request.weeklyPrice());
        }

        if (request.monthlyPrice() != null) {
            rentalProduct.updateMonthlyPrice(request.monthlyPrice());
        }

        if (request.securityDeposit() != null) {
            rentalProduct.updateSecurityDeposit(request.securityDeposit());
        }

        if (request.stock() != null) {
            rentalProduct.updateStock(request.stock());
        }

        List<ImageResponse> images = imageService.listImagesByOwnerId(rentalProduct.getProduct().getId(), OwnerType.PRODUCT);


        return mapper.toResponse(repository.save(rentalProduct), images);
    }

    /**
     * Retrieves a RentalProduct by its unique identifier and returns a corresponding response.
     *
     * @param id the unique identifier of the RentalProduct to retrieve
     * @return a RentalProductResponse object containing the RentalProduct details and associated images
     * @throws ProductNotFoundException if no RentalProduct is found with the given identifier
     */
    @Override
    public RentalProductResponse findById(UUID id) {

        RentalProduct rentalProduct = repository
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        List<ImageResponse> images = imageService.listImagesByOwnerId(rentalProduct.getProduct().getId(), OwnerType.PRODUCT);


        return mapper.toResponse(repository.save(rentalProduct), images);
    }

    /**
     * Processes the stock for a list of item requests, handling rental products,
     * updating their stock quantities, and returning the list of reservation item stocks.
     * The method also evicts specific cache entries to reflect the updated state.
     *
     * @param requests a list of item requests containing product IDs, types, and quantities
     * @return a list of {@link ReservationItemStock} representing the stock reservations for the processed items
     * @throws IllegalArgumentException if any products in the requests are not found in the repository
     * @throws ProductNotFoundException if a specific product requested for processing is not found
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public List<ReservationItemStock> processStock(
            List<ItemRequest> requests) {

        Map<UUID, ItemRequest> rentalItems = requests
                .stream()
                .filter(item -> item.type().equals(ProductType.RENTAL))
                .collect(Collectors.toMap(
                        ItemRequest::productId,
                        Function.identity()
                ));

        List<UUID> productIds = new ArrayList<>(rentalItems.keySet());

        List<RentalProduct> rentalProducts =
                repository.findAllByIdsIn(productIds);

        if (rentalProducts.size() != productIds.size()) {
            throw new IllegalArgumentException("Some products not found");
        }

        Map<UUID, RentalProduct> productMap =
                rentalProducts.stream()
                        .collect(Collectors.toMap(
                                RentalProduct::getId,
                                Function.identity()
                        ));

        List<ReservationItemStock> reservationItems =
                new ArrayList<>();

        for (Map.Entry<UUID, ItemRequest> entry : rentalItems.entrySet()) {

            RentalProduct rentalProduct =
                    productMap.get(entry.getKey());

            if (rentalProduct == null) {
                throw new ProductNotFoundException(
                        entry.getKey()
                );
            }

            rentalProduct.withdrawStock(
                    entry.getValue().quantity()
            );

            reservationItems.add(
                    ReservationItemStock.builder()
                            .productId(rentalProduct.getId())
                            .type(ProductType.RENTAL)
                            .quantity(entry.getValue().quantity())
                            .build()
            );
        }

        repository.saveAll(rentalProducts);

        return reservationItems;
    }

    /**
     * Cancels the stock for the given reservation items. This operation replenishes
     * the stock for rental products based on the quantities specified in the list of
     * reservation items. If any rental product is not found, an exception will be thrown.
     * The cache entries for "catalog-list" and "catalog" will be evicted to ensure data consistency.
     *
     * @param reservationItems a list of reservation items containing the product IDs, quantities,
     *                         and product types for which the stock should be canceled.
     *                         Only items of type {@code RENTAL} will be processed.
     * @throws IllegalArgumentException if some rental products are not found during the cancellation process.
     * @throws ProductNotFoundException if a specific rental product cannot be found using its product ID.
     */
    @CacheEvict(value = {"catalog-list", "catalog"}, allEntries = true)
    public void cancelStock(List<ReservationItemStock> reservationItems) {

        Map<UUID, Integer> rentalItems = reservationItems.stream()
                .filter(item -> ProductType.RENTAL.equals(item.getType()))
                .collect(Collectors.toMap(
                        ReservationItemStock::getProductId,
                        ReservationItemStock::getQuantity,
                        Integer::sum
                ));

        if (rentalItems.isEmpty()) {
            return;
        }

        List<UUID> productIds = new ArrayList<>(rentalItems.keySet());

        List<RentalProduct> rentalProducts = repository.findAllByIdsIn(productIds);

        if (rentalProducts.size() != productIds.size()) {
            throw new IllegalArgumentException("Some rental products not found during cancellation");
        }

        Map<UUID, RentalProduct> productMap = rentalProducts.stream()
                .collect(Collectors.toMap(
                        RentalProduct::getId,
                        Function.identity()
                ));

        for (Map.Entry<UUID, Integer> entry : rentalItems.entrySet()) {

            RentalProduct rentalProduct = productMap.get(entry.getKey());

            if (rentalProduct == null) {
                throw new ProductNotFoundException(entry.getKey());
            }

            rentalProduct.replenishStock(entry.getValue());
        }

        repository.saveAll(rentalProducts);
    }

}
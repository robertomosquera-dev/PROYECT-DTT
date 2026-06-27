package org.dtt.mscatalog.application.service;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.request.ReservationRequest;
import org.dtt.mscatalog.application.dto.response.ItemCatalogResponse;
import org.dtt.mscatalog.application.dto.response.ItemOrderResponse;
import org.dtt.mscatalog.application.dto.response.ReservationResponse;
import org.dtt.mscatalog.application.exception.ReservationByOrderNotFoundException;
import org.dtt.mscatalog.application.exception.ReservationNotFoundException;
import org.dtt.mscatalog.application.port.in.reservationStockUseCase.CreateReservationUseCase;
import org.dtt.mscatalog.application.port.in.reservationStockUseCase.GetReservedProductsByOrderUseCase;
import org.dtt.mscatalog.application.port.in.reservationStockUseCase.ProcessReservationUseCase;
import org.dtt.mscatalog.application.port.out.ReservationStockRepositoryPort;
import org.dtt.mscatalog.domain.model.Enum.StatusReservation;
import org.dtt.mscatalog.domain.model.ReservationItemStock;
import org.dtt.mscatalog.domain.model.ReservationStock;
import org.dtt.mscatalog.infrastructure.mapper.ReservationStockMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.ProductCatalogEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing reservation stock operations such as creating and processing reservations.
 * Implements {@link CreateReservationUseCase} and {@link ProcessReservationUseCase}.
 * This class integrates with multiple services to handle various product types (sale products, rental products, and product bundles),
 * and interacts with a persistence layer through a repository port.
 * <br>
 * Main functionalities provided:
 * - Creating a reservation and adding associated stock items.
 * - Processing reservations to either confirm or cancel them.
 */
@Service
@RequiredArgsConstructor
public class ReservationStockService implements CreateReservationUseCase, ProcessReservationUseCase, GetReservedProductsByOrderUseCase {

    private final ReservationStockRepositoryPort reservationStockRepositoryPort;
    private final ReservationStockMapper reservationStockMapper;


    private final SaleProductService saleProductService;
    private final RentalProductService rentalProductService;
    private final ProductBundleService productBundleService;


    private final ProductCatalogService productCatalogService;

    /**
     * Creates a new reservation based on the provided request.
     * This method processes the stock for sale products, rental products, and product bundles,
     * and then associates these items with the created reservation. Additionally, it retrieves
     * product details from the product catalog and builds a response including the reservation details.
     *
     * @param request the reservation request containing the user details, order ID,
     *                and list of items to be reserved.
     * @return the reservation response containing the reservation details,
     *         status, and product information.
     */
    @Transactional
    @Override
    public ReservationResponse createReservation(
            ReservationRequest request) {

        ReservationStock reservationStock =
                reservationStockMapper.toDomain(request);

        reservationStock.addItems(
                saleProductService.processStock(request.items())
        );
        reservationStock.addItems(
                rentalProductService.processStock(request.items())
        );
        reservationStock.addItems(
                productBundleService.processStock(request.items())
        );

        reservationStock = reservationStockRepositoryPort.save(reservationStock);

        List<ProductCatalogEntity> productCatalog = productCatalogService.listProductByIds(reservationStock.getProductsId());

        Map<UUID, ProductCatalogEntity> productCatMap = productCatalog
                .stream()
                .collect(Collectors.toMap(ProductCatalogEntity::getId, product -> product));

        List<ItemCatalogResponse> productResponses = reservationStock
                .getItems()
                .stream()
                .map(item -> {

                            ProductCatalogEntity product = productCatMap.get(item.getProductId());

                            return ItemCatalogResponse
                                    .builder()
                                    .productId(product.getId())
                                    .title(product.getName())
                                    .description(product.getDescription())
                                    .category(product.getCategorySlugs())
                                    .quantity(item.getQuantity())
                                    .unitPrice(product.getPrice())
                                    .pictureUrl(product.getUrl())
                                    .build();
                        }
                ).toList();

        return ReservationResponse.builder()
                .userId(reservationStock.getUserId())
                .orderId(reservationStock.getOrderId())
                .reservationId(reservationStock.getId())
                .status(reservationStock.getEstado())
                .products(productResponses)
                .build();
    }

    /**
     * Processes a reservation by either confirming or canceling it based on the given confirmation status.
     * The method retrieves the reservation using the provided reservation ID
     * and performs the appropriate action depending on the confirmation flag.
     *
     * @param reservationId the unique identifier of the reservation to be processed
     * @param isConfirmed   if true, the reservation will be confirmed; otherwise, it will be canceled
     * @throws IllegalArgumentException if the reservation with the specified ID is not found
     */
    @Override
    @Transactional
    public void processReservation(UUID reservationId, Boolean isConfirmed) {
        ReservationStock reservation = reservationStockRepositoryPort
                .findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        if (isConfirmed) {
            confirmReservation(reservation);
        } else {
            cancelReservation(reservation);
        }
    }

    /**
     * Confirms a reservation by changing its status to complete and saving the updated
     * reservation state in the repository.
     *
     * @param reservation the reservation to be confirmed, represented by a {@link ReservationStock} instance
     */
    private void confirmReservation(ReservationStock reservation) {
        reservation.changeStatus(StatusReservation.COMPLETE);
        reservationStockRepositoryPort.save(reservation);
    }

    /**
     * Cancels a reservation by updating its status to "CANCELED" and freeing up any associated stock.
     *
     * This method processes all items in the given reservation and cancels the related stock
     * for product bundles, sale products, and rental products. The updated reservation with the
     * canceled status is then saved back to the repository.
     *
     * @param reservation The reservation object that needs to be canceled. It contains the
     *                    reservation details and associated stock items that will be processed.
     */
    private void cancelReservation(ReservationStock reservation) {

        List<ReservationItemStock> items = reservation.getItems();

        productBundleService.cancelStock(items);
        saleProductService.cancelStock(items);
        rentalProductService.cancelStock(items);

        reservation.changeStatus(StatusReservation.CANCELED);

        reservationStockRepositoryPort.save(reservation);
    }

    @Override
    public List<ItemOrderResponse> getReservedProducts(UUID orderId) {

        ReservationStock reservationStock = reservationStockRepositoryPort
                .findByOrderId(orderId)
                .orElseThrow(() -> new ReservationByOrderNotFoundException(orderId));

        List<UUID> productIds = reservationStock.getItems().stream().map(ReservationItemStock::getProductId).toList();

        List<ProductCatalogEntity> products = productCatalogService.listProductByIds(productIds);

        return products
                .stream()
                .map(pc ->
                        ItemOrderResponse
                                .builder()
                                .id(pc.getId())
                                .name(pc.getName())
                                .type(pc.getSkuType())
                                .imageUrl(pc.getUrl())
                                .build()
                ).toList();
    }
}

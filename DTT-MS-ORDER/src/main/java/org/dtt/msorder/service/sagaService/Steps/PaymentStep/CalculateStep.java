package org.dtt.msorder.service.sagaService.Steps.PaymentStep;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.aop.TrackExecutionTime;
import org.dtt.msorder.dto.Request.ItemRequest;
import org.dtt.msorder.dto.Response.ItemCatalogResponse;
import org.dtt.msorder.dto.Response.ItemReservationResponse;
import org.dtt.msorder.dto.Response.ReservationResponse;
import org.dtt.msorder.service.sagaService.IStep;
import org.dtt.msorder.service.sagaService.SagaContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

//Step 5
@Component
@RequiredArgsConstructor
public class CalculateStep implements IStep {

    @TrackExecutionTime
    @Override
    public void execute(SagaContext context) throws Exception {

        validateContext(context);

        Map<UUID, ItemCatalogResponse> catalogMap = context.getReservationResponse()
                .products()
                .stream()
                .collect(Collectors.toMap(
                        ItemCatalogResponse::productId,
                        item -> item
                ));

        List<ItemReservationResponse> itemsReservation = context
                .getOrderRequest()
                .listItem()
                .stream()
                .map(itemRequest -> {

                    ItemCatalogResponse itemCatalog = catalogMap.get(itemRequest.productId());

                    return new ItemReservationResponse(
                            itemCatalog.productId(),
                            itemRequest.quantity(),
                            itemCatalog.unitPrice()
                    );
                })
                .toList();

        // Es el total de la suma de producto * cantidad. Se suma en una variable
        BigDecimal subtotal = itemsReservation.stream()
                .map(item -> item.unitPrice()
                        .multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal igv = subtotal.multiply(new BigDecimal("0.18"));

        BigDecimal total = subtotal.add(igv);

        context.setSubtotal(subtotal);
        context.setIgv(igv);
        context.setTotal(total);


        //Calcular el igv al producto que va a la pasarela

        List<ItemCatalogResponse>itemCatalogResponses =context
                .getReservationResponse()
                .products()
                .stream()
                .map(item ->{

                    BigDecimal priceWithIGV = item
                            .unitPrice()
                            .multiply(new BigDecimal("1.18"))
                            .setScale(2, RoundingMode.HALF_UP);

                    return ItemCatalogResponse.builder()
                            .productId(item.productId())
                            .title(item.title())
                            .description(item.description())
                            .category(item.category())
                            .quantity(item.quantity())
                            .unitPrice(priceWithIGV)
                            .pictureUrl(item.pictureUrl())
                            .build();

                })
                .toList();

        ReservationResponse reservationResponse = ReservationResponse.builder()
                .orderId(context.getReservationResponse().orderId())
                .userId(context.getReservationResponse().userId())
                .reservationId(context.getReservationResponse().reservationId())
                .status(context.getReservationResponse().status())
                .products(itemCatalogResponses)
                .build();

        context.setReservationResponse(reservationResponse);

    }

    @TrackExecutionTime
    @Override
    public void compensate(SagaContext context) {
        // No aplica
    }

    private void validateContext(SagaContext context) {

        if (context.getOrderRequest() == null) {
            throw new IllegalStateException("OrderRequest no disponible");
        }

        if (context.getOrderRequest().listItem() == null ||
                context.getOrderRequest().listItem().isEmpty()) {
            throw new IllegalStateException("Items no disponibles para cálculo");
        }
    }
}
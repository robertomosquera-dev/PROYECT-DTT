package org.dtt.msorder.service;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.dto.Response.*;
import org.dtt.msorder.mapper.OrderMapping;
import org.dtt.msorder.model.OrderItem;
import org.dtt.msorder.model.OrderStatus;
import org.dtt.msorder.model.PaymentStatus;
import org.dtt.msorder.model.PurchaseOrder;
import org.dtt.msorder.service.WebClientService.LogicService;
import org.dtt.msorder.service.WebClientService.UserService;
import org.dtt.msorder.service.sagaService.SagaContext;
import org.dtt.msorder.service.sagaService.SagaStepsService;
import org.dtt.msorder.service.sagaService.Steps.PaymentStep.*;
import org.dtt.msorder.dto.Request.OrderRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class OrderOrchestratorService {

    private final SagaStepsService sagaStepsService;
    private final BuildPayerStep buildPayerStep;
    private final CreateOrderStep createOrderStep;
    private final CreationReservationStep creationReservationStep;
    private final SaveItemStep saveItemStep;
    private final CalculateStep calculateStep;
    private final CreatePaymentStep createPaymentStep;
    private final PendingOrderStep pendingOrderStep;

    private final OrderMapping orderMapping;
    private final LogicService logicService;
    private final UserService userService;
    private final OrderService orderService;

    public OrderResponse createOrder(OrderRequest orderRequest) {
        if (orderRequest == null)
            throw new IllegalArgumentException("Order request is null");

        SagaContext context = new SagaContext();
        context.setOrderRequest(orderRequest);

        UserResponse user = userService.getUser();
        context.setUserId(user.userId());
        context.setUserResponse(user);

        return sagaStepsService.runSteps(
                List.of(
                        buildPayerStep,
                        createOrderStep,
                        creationReservationStep,
                        saveItemStep,
                        calculateStep,
                        createPaymentStep,
                        pendingOrderStep
                ),
                context
        );
    }

    public OrderDetailsResponse processOrder(UUID orderId, UUID userId, OrderStatus newStatus,String paymentId) {
        PurchaseOrder order = orderService.findByIdAndUser(orderId, userId);

        validateTransition(order.getOrderStatus(), newStatus);

        switch (newStatus) {
            case COMPLETED -> acceptOrder(order,paymentId);
            case CANCELLED -> cancelOrder(order);
        }

        order = orderService.updateOrder(order);
        return orderMapping.toDetailsResponse(order, getStockPerItem(order));
    }

    private void validateTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    "Invalid transition from " + currentStatus + " to " + newStatus
            );
        }
    }

    private void acceptOrder(PurchaseOrder order,String paymentId) {
        if (order.getReservationId() != null) {
            logicService.confirmReservation(order.getReservationId());
        }
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setPaymentStatus(PaymentStatus.APPROVED);
        order.setMpPaymentId(paymentId);
    }

    private void cancelOrder(PurchaseOrder order) {
        if (order.getReservationId() != null) {
            logicService.cancelReservation(order.getReservationId());
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELLED);
    }

    public OrderDTO findOrderById(UUID orderId) {
        UserResponse user = userService.getUser();

        PurchaseOrder order = orderService.findByIdAndUser(orderId, user.userId());

        Map<UUID, OrderItem> itemsMap = order.getItems().stream()
                .collect(Collectors.toMap(OrderItem::getProductId, Function.identity()));

        List<ItemResponse> itemsResponse = logicService.getItemsFromReservation(order.getId())
                .stream()
                .map(product -> buildItemResponse(product, itemsMap))
                .toList();

        return OrderDTO.builder()
                .orderId(order.getId())
                .mpPreferenceId(order.getMpPreferenceId())
                .initPoint(order.getInitPoint())
                .payerName(user.firstName())
                .payerEmail(user.email())
                .totalItems(getStockPerItem(order))
                .totalAmount(order.getTotal())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .items(itemsResponse)
                .build();
    }

    public OrderDetailsResponse findOrderById(UUID orderId, UUID userId) {
        if (orderId == null || userId == null)
            throw new IllegalArgumentException("Order id or user id is null");

        PurchaseOrder order = orderService.findByIdAndUser(orderId, userId);
        return orderMapping.toDetailsResponse(order, getStockPerItem(order));
    }

    public List<OrderDetailsResponse> findList() {
        return orderService.listOrder()
                .stream()
                .map(order -> orderMapping.toDetailsResponse(order, getStockPerItem(order)))
                .toList();
    }

    private ItemResponse buildItemResponse(ItemOrderResponse productDetail, Map<UUID, OrderItem> itemsMap) {
        OrderItem item = itemsMap.get(productDetail.id());

        if (item == null)
            throw new IllegalStateException("Producto no encontrado: " + productDetail.id());

        return ItemResponse.builder()
                .itemId(item.getId())
                .productId(productDetail.id())
                .type(productDetail.type())
                .name(productDetail.name())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .imageUrl(productDetail.imageUrl())
                .build();
    }

    private Integer getStockPerItem(PurchaseOrder order) {
        return order.getItems()
                .stream()
                .map(OrderItem::getQuantity)
                .reduce(0, Integer::sum);
    }
}
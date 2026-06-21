package org.dtt.msorder.service;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.dto.Request.OrderProductsRequest;
import org.dtt.msorder.dto.Request.OrderRequest;
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
import org.dtt.msorder.utils.JwtUtils;
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

    private final JwtUtils jwtUtils;

    public OrderResponse createOrder(OrderRequest orderRequest){

        if(orderRequest == null)
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

    public OrderDetailsResponse processOrder(UUID orderId, UUID userId, OrderStatus newStatus) {
        PurchaseOrder order = orderService.findByIdAndUser(orderId, userId);

        validateTransition(order.getOrderStatus(), newStatus);

        switch (newStatus) {
            case WAITING_PAYMENT -> waitingPaymentOrder(order);
            case COMPLETED -> acceptOrder(order);
            case CANCELLED -> cancelOrder(order);
        }

        order = orderService.updateOrder(order);

        Integer stockPerItem = getStockPerItem(order);

        return orderMapping.toDetailsResponse(order,stockPerItem);
    }


    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.WAITING_PAYMENT, OrderStatus.CANCELLED),
            OrderStatus.WAITING_PAYMENT, Set.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED),
            OrderStatus.COMPLETED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    private void validateTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        Set<OrderStatus> allowed = VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());

        if (!allowed.contains(newStatus)) {
            throw new IllegalStateException(
                    "Invalid transition from " + currentStatus + " to " + newStatus
            );
        }
    }

    private void acceptOrder(PurchaseOrder order){
        if (order.getReservationId() != null) {
            logicService.confirmReservation(order.getReservationId(),jwtUtils.getToken().getTokenValue());
        }
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setPaymentStatus(PaymentStatus.APPROVED);
    }

    private void cancelOrder(PurchaseOrder order){
        if (order.getReservationId() != null) {
            logicService.cancelReservation(order.getReservationId(),jwtUtils.getToken().getTokenValue());
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setPaymentStatus(PaymentStatus.CANCELLED);
    }

    private void waitingPaymentOrder(PurchaseOrder order) {
        order.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        order.setPaymentStatus(PaymentStatus.PENDING);
    }

    //Cambio OrderDTO
    public OrderDTO findOrderById(UUID orderId) {

        UserResponse user = userService.getUser();

        PurchaseOrder purchaseOrder = orderService.findByIdAndUser(
                orderId,
                user.userId()
        );

        List<OrderItem> orderItems = purchaseOrder.getItems();

        List<UUID> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .toList();

        Map<UUID, List<UUID>> orderProducts = Map.of(
                purchaseOrder.getId(),
                productIds
        );

        Map<UUID, ItemOrderResponse> itemsMap = logicService
                .getItemsFromReservation(orderProducts,jwtUtils.getToken().getTokenValue())
                .stream()
                .collect(Collectors.toMap(
                        ItemOrderResponse::id,
                        Function.identity()
                ));

        List<ItemResponse> itemsResponse = orderItems.stream()
                .map(item -> buildItemResponse(item, itemsMap))
                .toList();

        return OrderDTO.builder()
                .orderId(purchaseOrder.getId())
                .mpPreferenceId(purchaseOrder.getMpPreferenceId())
                .initPoint(purchaseOrder.getInitPoint())
                .payerName(user.firstName())
                .payerEmail(user.email())
                .totalItems(getStockPerItem(purchaseOrder))
                .totalAmount(purchaseOrder.getTotal())
                .orderStatus(purchaseOrder.getOrderStatus())
                .paymentStatus(purchaseOrder.getPaymentStatus())
                .items(itemsResponse)
                .build();
    }

    private ItemResponse buildItemResponse(
            OrderItem item,
            Map<UUID, ItemOrderResponse> itemsMap
    ) {

        ItemOrderResponse product = itemsMap.get(item.getProductId());

        if (product == null)
            throw new IllegalStateException("Producto no encontrado: " + item.getProductId());


        return ItemResponse.builder()
                .id(product.id())
                .type(product.tipo())
                .title(product.nombre())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .pictureUrl(product.imagenUrl())
                .build();
    }

    private Integer getStockPerItem(PurchaseOrder order){
        List<OrderItem> items = order.getItems();
        return items
                .stream()
                .map(OrderItem::getQuantity)
                .reduce(0, Integer::sum);
    }

    //Cambio OrderDTO
    public OrderDetailsResponse findOrderById(UUID orderId, UUID userId){
        if (orderId == null || userId == null){
            throw new IllegalArgumentException("Order id or user id is null");
        }
        PurchaseOrder order = orderService.findByIdAndUser(orderId, userId);
        Integer stockPerItem = getStockPerItem(order);
        return orderMapping.toDetailsResponse(order,stockPerItem);
    }

    public List<OrderDetailsResponse> findList() {
        return orderService.listOrder()
                .stream()
                .map(this::mapToDetails)
                .toList();
    }

    private OrderDetailsResponse mapToDetails(PurchaseOrder order) {

        Integer totalPerItem = getStockPerItem(order);

        return orderMapping.toDetailsResponse(order, totalPerItem);
    }
}

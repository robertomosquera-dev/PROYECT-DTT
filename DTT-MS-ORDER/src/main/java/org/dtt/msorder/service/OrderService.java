package org.dtt.msorder.service;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.dto.Request.OrderRequest;
import org.dtt.msorder.dto.Response.OrderResponse;
import org.dtt.msorder.model.Currency;
import org.dtt.msorder.model.OrderStatus;
import org.dtt.msorder.model.PaymentStatus;
import org.dtt.msorder.model.PurchaseOrder;
import org.dtt.msorder.repository.PurchaseOrderRepository;
import org.dtt.msorder.utils.OrderGenerateCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final PurchaseOrderRepository orderRepository;

    public PurchaseOrder saveOrderByDefault(UUID userId, String platform, Currency currency) {
        PurchaseOrder order = PurchaseOrder
                .builder()
                .orderCode(OrderGenerateCode.generate(userId))
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .userId(userId)
                .platform(platform)
                .currency(currency)
                .build();
        return orderRepository.save(order);
    }

    public void cancelOrder(UUID idOrder) {
        PurchaseOrder order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            return;
        }

        if (!order.getOrderStatus().canTransitionTo(OrderStatus.CANCELLED)) {
            throw new IllegalStateException(
                    "No se puede cancelar una orden en estado: " + order.getOrderStatus()
            );
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        if (order.getPaymentStatus() != PaymentStatus.APPROVED) {
            order.setPaymentStatus(PaymentStatus.CANCELLED);
        }
        orderRepository.save(order);
    }

    public List<PurchaseOrder> listOrder() {
        return orderRepository.findAll();
    }

    public PurchaseOrder updateOrder(PurchaseOrder order) {
        return orderRepository.save(order);
    }

    public PurchaseOrder findByIdAndUser(UUID orderId, UUID userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
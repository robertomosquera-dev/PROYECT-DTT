package org.dtt.msorder.service;


import org.dtt.msorder.dto.Request.OrderRequest;
import org.dtt.msorder.dto.Response.OrderResponse;
import org.dtt.msorder.model.Currency;
import org.dtt.msorder.model.OrderStatus;
import org.dtt.msorder.model.PaymentStatus;
import org.dtt.msorder.model.PurchaseOrder;
import org.dtt.msorder.repository.PurchaseOrderRepository;
import org.dtt.msorder.utils.OrderGenerateCode;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Oigan si alguien lee el codigo, se habra dado cuenta que no estoy usando un mapper en algunos metodos nativo de mapeo.
 * Le agradeceria que lo añaden para que sea mas legible el code.
 * Los ms ya tienen integrado MapStrup solo esta para que lo usen.
 */

@Service
@RequiredArgsConstructor
public class OrderService {


    private final PurchaseOrderRepository orderRepository;

    public PurchaseOrder saveOrderByDefault(UUID userId, String platform, Currency currency){
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

    //En caso de no usar que devuelve, convertilo en void (al terminar todo el proyecto), uso exclusivo para los pasos
    public void cancelOrder(UUID idOrder) {
        PurchaseOrder order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            return;
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        if (order.getPaymentStatus() != PaymentStatus.APPROVED) {
            order.setPaymentStatus(PaymentStatus.CANCELLED);
        }
        orderRepository.save(order);
    }

    public List<PurchaseOrder> listOrder(){
        return orderRepository.findAll();
    }

    // Se utilizara para el caso de una compesacion o un pago dentro de los steps
    public PurchaseOrder updateOrder(PurchaseOrder order){
        return orderRepository.save(order);
    }

    public PurchaseOrder update(PurchaseOrder order){
        return orderRepository.save(order);
    }

    public PurchaseOrder findByIdAndUser(UUID orderId, UUID userId){
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

}

package org.dtt.msorder.service.sagaService.Steps.PaymentStep;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.aop.TrackExecutionTime;
import org.dtt.msorder.model.Currency;
import org.dtt.msorder.model.OrderStatus;
import org.dtt.msorder.model.PurchaseOrder;
import org.dtt.msorder.service.OrderService;
import org.dtt.msorder.service.sagaService.IStep;
import org.dtt.msorder.service.sagaService.SagaContext;
import org.springframework.stereotype.Component;

import java.util.UUID;


//Step 2
@Component
@RequiredArgsConstructor
public class CreateOrderStep implements IStep {

    private final OrderService orderService;

    @TrackExecutionTime
    @Override
    public void execute(SagaContext context) {

        if (context.getUserId() == null) {
            throw new IllegalStateException("userId no disponible");
        }

        if (context.getOrderRequest() == null) {
            throw new IllegalStateException("orderRequest no disponible");
        }

        UUID userId = context.getUserId();
        String platform = context.getOrderRequest().platform();
        Currency currency = context.getOrderRequest().currency();

        PurchaseOrder order = orderService.saveOrderByDefault(userId, platform, currency,context.getUserResponse().email());

        context.setOrder(order);
    }

    @TrackExecutionTime
    @Override
    public void compensate(SagaContext context) {
        orderService.cancelOrder(context.getOrder().getId());
    }
}
package org.dtt.msorder.service.sagaService.Steps.PaymentStep;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.aop.TrackExecutionTime;
import org.dtt.msorder.model.PurchaseOrder;
import org.dtt.msorder.service.OrderService;
import org.dtt.msorder.service.sagaService.IStep;
import org.dtt.msorder.service.sagaService.SagaContext;
import org.springframework.stereotype.Component;

//Step 7
@Component
@RequiredArgsConstructor
public class PendingOrderStep implements IStep {

    private final OrderService orderService;

    @TrackExecutionTime
    @Override
    public void execute(SagaContext context) throws Exception {

        PurchaseOrder order = context.getOrder();

        order.setReservationId(context.getReservationResponse().reservationId());
        order.setSubtotal(context.getSubtotal());
        order.setTotal(context.getTotal());
        order.setIgv(context.getIgv());

        order.setMpPreferenceId(context.getPaymentResponse().mpPreferenceId());
        order.setInitPoint(context.getPaymentResponse().initPoint());
        order.setPaymentStatus(context.getOrder().getPaymentStatus());

        orderService.updateOrder(order);
    }

    @TrackExecutionTime
    @Override
    public void compensate(SagaContext context) {
        orderService.cancelOrder(context.getOrder().getId());
    }

}

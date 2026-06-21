package org.dtt.msorder.service.sagaService.Steps.PaymentStep;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.aop.TrackExecutionTime;
import org.dtt.msorder.dto.Response.ReservationResponse;
import org.dtt.msorder.model.OrderItem;
import org.dtt.msorder.model.PurchaseOrder;
import org.dtt.msorder.service.ItemService;
import org.dtt.msorder.service.sagaService.IStep;
import org.dtt.msorder.service.sagaService.SagaContext;
import org.springframework.stereotype.Component;

import java.util.List;

//Step 4
@Component
@RequiredArgsConstructor
public class SaveItemStep implements IStep {

    private final ItemService itemService;

    @TrackExecutionTime
    @Override
    public void execute(SagaContext context) throws Exception {
        ReservationResponse reservationResponse = context.getReservationResponse();
        PurchaseOrder order = context.getOrder(); //Entity real
        List<OrderItem> items = itemService.saveItems(order,reservationResponse.products());
        context.setItems(items);
    }

    @TrackExecutionTime
    @Override
    public void compensate(SagaContext context) {
        if (context.getItems().isEmpty()){
            throw new IllegalStateException("La lista no puede estar vacia");
        }
        itemService.deleteItems(context.getItems());
    }
}

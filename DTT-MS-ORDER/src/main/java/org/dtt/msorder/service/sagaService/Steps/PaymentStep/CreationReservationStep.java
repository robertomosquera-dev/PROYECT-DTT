package org.dtt.msorder.service.sagaService.Steps.PaymentStep;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.aop.TrackExecutionTime;
import org.dtt.msorder.dto.Request.ItemRequest;
import org.dtt.msorder.dto.Request.ReservationRequest;
import org.dtt.msorder.dto.Response.ReservationResponse;
import org.dtt.msorder.exception.ExepcionCatalog;
import org.dtt.msorder.service.WebClientService.LogicService;
import org.dtt.msorder.service.sagaService.IStep;
import org.dtt.msorder.service.sagaService.SagaContext;
import org.dtt.msorder.utils.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

//Step 3
@Component
@RequiredArgsConstructor
public class CreationReservationStep implements IStep {

    private final LogicService logicService;

    @TrackExecutionTime
    @Override
    public void execute(SagaContext context) throws Exception {

        validationContextForReservation(context);

        UUID userId = context.getUserId();

        UUID orderId = context.getOrder().getId();

        List<ItemRequest>items = context.getOrderRequest().listItem();

        ReservationRequest reservationRequest = ReservationRequest
                .builder()
                .orderId(orderId)
                .userId(userId)
                .items(items)
                .build();

        try {
            ReservationResponse reservationResponse =
                    logicService.productReservation(reservationRequest);
            context.setReservationResponse(reservationResponse);
        } catch (RuntimeException e) {
            throw new ExepcionCatalog(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @TrackExecutionTime
    @Override
    public void compensate(SagaContext context) {
        if (context.getReservationResponse() == null) {
            return;
        }

        UUID reservationId = context.getReservationResponse().reservationId();

        if (reservationId == null) {
            return;
        }

        logicService.cancelReservation(reservationId);
    }

    private void validationContextForReservation(SagaContext context){
        if (context.getUserId() == null){
            throw new IllegalStateException("User no disponible");
        }

        if (context.getOrder() == null) {
            throw new IllegalStateException("Order no disponible");
        }

        if (context.getOrderRequest() == null) {
            throw new IllegalStateException("OrderRequest no disponible");
        }

        if (context.getOrderRequest().listItem() == null ||
                context.getOrderRequest().listItem().isEmpty()) {
            throw new IllegalStateException("Items no disponible");
        }
    }

}

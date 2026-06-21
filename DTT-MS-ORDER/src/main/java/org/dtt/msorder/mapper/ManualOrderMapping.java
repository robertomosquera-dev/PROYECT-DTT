package org.dtt.msorder.mapper;

import org.dtt.msorder.dto.Request.PayerRequest;
import org.dtt.msorder.dto.Response.OrderResponse;
import org.dtt.msorder.dto.Response.UserResponse;
import org.dtt.msorder.service.sagaService.SagaContext;
import org.springframework.stereotype.Component;

@Component
public class ManualOrderMapping {
    public OrderResponse toResponse(SagaContext context){
        return OrderResponse
                .builder()
                .orderId(context.getOrder().getId())
                .mpPreferenceId(context.getPaymentResponse().mpPreferenceId())
                .initPoint(context.getPaymentResponse().initPoint())
                .payerName(context.getPayerRequest().name())
                .payerEmail(context.getPayerRequest().email())
                .totalItems(context.getItems().size())
                .totalAmount(context.getTotal())
                .orderStatus(context.getOrder().getOrderStatus())
                .paymentStatus(context.getOrder().getPaymentStatus())
                .build();
    }
}

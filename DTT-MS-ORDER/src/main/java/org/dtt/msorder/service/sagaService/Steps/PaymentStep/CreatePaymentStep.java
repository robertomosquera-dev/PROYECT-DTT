package org.dtt.msorder.service.sagaService.Steps.PaymentStep;

import lombok.RequiredArgsConstructor;
import org.dtt.msorder.aop.TrackExecutionTime;
import org.dtt.msorder.dto.Request.PaymentItemRequest;
import org.dtt.msorder.dto.Request.PaymentOrderRequest;
import org.dtt.msorder.dto.Response.PaymentResponse;
import org.dtt.msorder.model.OrderStatus;
import org.dtt.msorder.model.PaymentStatus;
import org.dtt.msorder.service.WebClientService.MpService;
import org.dtt.msorder.service.sagaService.IStep;
import org.dtt.msorder.service.sagaService.SagaContext;
import org.springframework.stereotype.Component;

import java.util.List;

//Step 6
@Component
@RequiredArgsConstructor
public class CreatePaymentStep implements IStep {

    private final MpService mpService;

    @TrackExecutionTime
    @Override
    public void execute(SagaContext context) throws Exception {

        List<PaymentItemRequest> items = context
                .getReservationResponse()
                .products()
                .stream()
                .map(item -> PaymentItemRequest
                        .builder()
                        .productId(item.productId())
                        .quantity(item.quantity())
                        .title(item.title())
                        .description(item.description())
                        .category(item.category())
                        .currency(context.getOrder().getCurrency().toString())
                        .unitPrice(item.unitPrice())
                        .pictureUrl(item.pictureUrl())
                        .build()
                ).toList();

        PaymentOrderRequest orderRequest = PaymentOrderRequest
                .builder()
                .orderId(context.getOrder().getId())
                .items(items)
                .payer(context.getPayerRequest())
                .platform(context.getOrder().getPlatform())
                .build();

        PaymentResponse paymentResponse = mpService.createPayment(orderRequest);

        context.setPaymentResponse(paymentResponse);
    }

    @TrackExecutionTime
    @Override
    public void compensate(SagaContext context) {
        //Cancelar la order de la pasarela de pago
        if (context.getOrder() != null) {
            context.getOrder().setOrderStatus(OrderStatus.FAILED);
            context.getOrder().setPaymentStatus(PaymentStatus.REJECTED);
        }
    }

}

package org.dtt.msorder.service.sagaService.Steps.PaymentStep;

import org.dtt.msorder.aop.TrackExecutionTime;
import org.dtt.msorder.dto.Request.PayerRequest;
import org.dtt.msorder.dto.Response.UserResponse;
import org.dtt.msorder.service.sagaService.IStep;
import org.dtt.msorder.service.sagaService.SagaContext;
import org.springframework.stereotype.Component;


//Step 1
@Component
public class BuildPayerStep implements IStep {

    @TrackExecutionTime
    @Override
    public void execute(SagaContext context) {

        UserResponse user = context.getUserResponse();

        if (user == null) {
            throw new IllegalStateException("User no disponible");
        }

        PayerRequest payer = PayerRequest.builder()
                .payerId(user.userId())
                .name(user.firstName())
                .surname(user.lastName())
                .email(user.email())
                .build();

        context.setPayerRequest(payer);
    }

    @TrackExecutionTime
    @Override
    public void compensate(SagaContext context) {
        context.setPayerRequest(null);
    }
}
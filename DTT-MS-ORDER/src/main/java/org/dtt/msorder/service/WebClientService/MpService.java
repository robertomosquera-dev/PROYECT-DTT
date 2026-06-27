package org.dtt.msorder.service.WebClientService;

import org.dtt.msorder.aop.TrackExecutionTime;
import org.dtt.msorder.client.MercadoPagoClient;
import org.dtt.msorder.dto.Request.PaymentOrderRequest;
import org.dtt.msorder.dto.Response.PaymentResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MpService {

    private final MercadoPagoClient mercadoPagoClient;

    @TrackExecutionTime
    public PaymentResponse createPayment(PaymentOrderRequest orderRequest){
        var res = mercadoPagoClient.createPayment(orderRequest);
        return res.data();
    }

}

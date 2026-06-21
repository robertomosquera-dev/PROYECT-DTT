package org.dtt.msorder.service.sagaService;



import org.dtt.msorder.dto.Request.OrderRequest;


import lombok.Getter;
import lombok.Setter;
import org.dtt.msorder.dto.Request.PayerRequest;
import org.dtt.msorder.dto.Response.PaymentResponse;
import org.dtt.msorder.dto.Response.ReservationResponse;
import org.dtt.msorder.dto.Response.UserResponse;
import org.dtt.msorder.model.OrderItem;
import org.dtt.msorder.model.PurchaseOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SagaContext {

    private OrderRequest orderRequest; //Cargado desde el inicio
    private UserResponse userResponse; //Cargado desde el inicio
    private UUID userId;               //Cargado desde el inicio

    private PayerRequest payerRequest; //Cargado desde el paso 1

    private PurchaseOrder order; //Cargado desde el paso 2

    private ReservationResponse reservationResponse; //Cargado desde el paso 3

    private List<OrderItem> items; //Lista de items, Cargado en el paso 4

    private BigDecimal total;       //Cargado en el paso 5
    private BigDecimal igv;         //Cargado en el paso 5
    private BigDecimal subtotal;    //Cargado en el paso 5

    private PaymentResponse paymentResponse; //Cargado en el paso 6

}
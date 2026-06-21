package org.dtt.msorder.model;

public enum OrderStatus {
    PENDING,           // recién creada
    WAITING_PAYMENT,   // esperando que el cliente pague
    COMPLETED,         // todo ok (pagado + confirmado)
    CANCELLED,         // cancelada
    FAILED             // falló algo en la saga
}
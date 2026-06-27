package org.dtt.msorder.model;

public enum PaymentStatus {
    PENDING,     // pago iniciado pero no confirmado
    APPROVED,    // pago exitoso
    REJECTED,    // pago fallido
    CANCELLED,   // pago cancelado
}

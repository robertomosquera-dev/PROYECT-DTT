package org.dtt.msorder.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "purchase_order")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;


    private String orderCode;

    private String mpPreferenceId; 
    // ID de la preferencia de pago (intención de pago)
    // ✔ Se obtiene al CREAR el pago (createPreference en PaymentService)

    private String mpPaymentId;
    // ID del pago real en Mercado Pago
    // ✔ Se obtiene desde el WEBHOOK (data.id)
    // ✔ Representa que el usuario ya realizó el pago


    private String initPoint;
    // URL de pago de Mercado Pago
    // ✔ Se obtiene al CREAR el pago (createPreference)
    // ✔ Se envía al frontend para redirigir al usuario


    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private String platform;
    private UUID userId;
    private String payerEmail;


    private UUID reservationId;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;

    @OneToMany(
        mappedBy = "order", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true
    )
    private List<OrderItem> items;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;    
}
package org.dtt.msorder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.dtt.msorder.dto.Request.OrderRequest;
import org.dtt.msorder.dto.Response.OrderDTO;
import org.dtt.msorder.dto.Response.OrderDetailsResponse;
import org.dtt.msorder.dto.Response.OrderResponse;
import org.dtt.msorder.model.OrderStatus;
import org.dtt.msorder.service.OrderOrchestratorService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/orders")
@Tag(
        name = "Orders",
        description = "Endpoints for order management and orchestration"
)
public class OrderController {

    private final OrderOrchestratorService orderOrchestratorService;

    @Operation(
            summary = "Create a new order",
            description = "Creates a new order and starts the orchestration saga process."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderResponse createOrder(
            @Valid @RequestBody OrderRequest orderRequest
    ) {
        return orderOrchestratorService.createOrder(orderRequest);
    }

    @Operation(
            summary = "Process order status",
            description = "Allows administrators to update the status of an order."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_SYSTEM')")
    @PatchMapping("/{orderId}/users/{userId}")
    public OrderDetailsResponse processOrder(
            @PathVariable UUID orderId,
            @PathVariable UUID userId,
            @RequestParam OrderStatus newStatus,
            @RequestParam(required = false) String paymentId
    ) {
        return orderOrchestratorService.processOrder(
                orderId,
                userId,
                newStatus,
                paymentId
        );
    }

    @Operation(
            summary = "Get authenticated user order",
            description = "Returns detailed information about an order belonging to the authenticated user."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public OrderDTO findByOrderId(@PathVariable UUID id) {
        return orderOrchestratorService.findOrderById(id);
    }

    @Operation(
            summary = "Get order by user",
            description = "Returns order details for administrators."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    @GetMapping("/{id}/users/{userId}")
    public OrderDetailsResponse findByUserId(
            @PathVariable UUID id,
            @PathVariable UUID userId
    ) {
        return orderOrchestratorService.findOrderById(id, userId);
    }

    @Operation(
            summary = "Get all orders",
            description = "Returns a list of all orders."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @GetMapping
    public List<OrderDetailsResponse> findAllOrders() {
        return orderOrchestratorService.findList();
    }
}
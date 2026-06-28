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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        description = "Endpoints for order management and orchestration. Roles: USER (create, view own orders), ADMIN (manage all orders), SUPER_ADMIN (full access), SYSTEM (internal processing)."
)
public class OrderController {

    private final OrderOrchestratorService orderOrchestratorService;

    @Operation(
            summary = "Create a new order",
            description = "Creates a new order and starts the orchestration saga process. Accessible by: USER, ADMIN, SUPER_ADMIN."
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return orderOrchestratorService.createOrder(orderRequest);
    }

    @Operation(
            summary = "Process order status",
            description = "Updates the status of an order (COMPLETED or CANCELLED). Requires paymentId when completing. Accessible by: ADMIN, SUPER_ADMIN, SYSTEM."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_SYSTEM')")
    @PatchMapping("/{orderId}/users/{userId}")
    public OrderDetailsResponse processOrder(
            @PathVariable UUID orderId,
            @PathVariable UUID userId,
            @RequestParam OrderStatus newStatus,
            @RequestParam(required = false) String paymentId
    ) {
        // Debug temporal
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("=== AUTH DEBUG ===");
        System.out.println("Principal: " + auth.getPrincipal());
        System.out.println("Authorities: " + auth.getAuthorities());
        System.out.println("=================");

        return orderOrchestratorService.processOrder(orderId, userId, newStatus, paymentId);
    }

    @Operation(
            summary = "Get order by id (own)",
            description = "Returns full order details including items and payment info. Only returns orders belonging to the authenticated user. Accessible by: USER, ADMIN, SUPER_ADMIN."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public OrderDTO findByOrderId(@PathVariable UUID id) {
        return orderOrchestratorService.findOrderById(id);
    }

    @Operation(
            summary = "Get my orders",
            description = "Returns all orders belonging to the authenticated user. Accessible by: USER, ADMIN, SUPER_ADMIN."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @GetMapping("/me")
    public List<OrderDetailsResponse> findMyOrders() {
        return orderOrchestratorService.findMyOrders();
    }

    @Operation(
            summary = "Cancel my order",
            description = "Cancels an order if its current status allows the transition to CANCELLED. Only affects orders belonging to the authenticated user. Accessible by: USER, ADMIN, SUPER_ADMIN."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelMyOrder(@PathVariable UUID id) {
        orderOrchestratorService.cancelMyOrder(id);
    }

    @Operation(
            summary = "Get order by user (admin)",
            description = "Returns order details for any user by order id and user id. Accessible by: ADMIN, SUPER_ADMIN."
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
            description = "Returns a full list of all orders in the system without filtering. Accessible by: ADMIN, SUPER_ADMIN."
    )
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_ADMIN')")
    @GetMapping
    public List<OrderDetailsResponse> findAllOrders() {
        return orderOrchestratorService.findList();
    }
}
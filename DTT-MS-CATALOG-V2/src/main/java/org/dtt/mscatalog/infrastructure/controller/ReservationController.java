package org.dtt.mscatalog.infrastructure.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.request.ReservationRequest;
import org.dtt.mscatalog.application.dto.response.ItemOrderResponse;
import org.dtt.mscatalog.application.dto.response.ReservationResponse;
import org.dtt.mscatalog.application.port.in.reservationStockUseCase.CreateReservationUseCase;
import org.dtt.mscatalog.application.port.in.reservationStockUseCase.GetReservedProductsByOrderUseCase;
import org.dtt.mscatalog.application.port.in.reservationStockUseCase.ProcessReservationUseCase;
import org.dtt.mscatalog.infrastructure.utils.ConsumerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','SYSTEM','USER')")
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final ProcessReservationUseCase processReservationUseCase;
    private final GetReservedProductsByOrderUseCase getReservedProductsByOrderUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumerResponse<ReservationResponse> createReservation(
            @RequestBody @Valid ReservationRequest request) {
        return ConsumerResponse.created(
                createReservationUseCase.createReservation(request)
        );
    }

    @PatchMapping("/{reservationId}/process")
    @ResponseStatus(HttpStatus.OK)
    public ConsumerResponse<Void> processReservation(
            @PathVariable UUID reservationId,
            @RequestParam Boolean isConfirmed) {
        processReservationUseCase.processReservation(reservationId, isConfirmed);
        return ConsumerResponse.success(
                null,
                isConfirmed ? "Reservation confirmed" : "Reservation cancelled"
        );
    }

    @GetMapping("/items/{orderId}")
    public ConsumerResponse<List<ItemOrderResponse>> getProductByOrderId(@PathVariable UUID orderId) {
        return ConsumerResponse.success(getReservedProductsByOrderUseCase.getReservedProducts(orderId));
    }
}
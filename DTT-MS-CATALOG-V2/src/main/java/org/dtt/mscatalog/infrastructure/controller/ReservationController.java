package org.dtt.mscatalog.infrastructure.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.request.ReservationRequest;
import org.dtt.mscatalog.application.dto.response.ReservationResponse;
import org.dtt.mscatalog.application.port.in.reservationStockUseCase.CreateReservationUseCase;
import org.dtt.mscatalog.application.port.in.reservationStockUseCase.ProcessReservationUseCase;
import org.dtt.mscatalog.infrastructure.utils.ConsumerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final ProcessReservationUseCase processReservationUseCase;

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
}
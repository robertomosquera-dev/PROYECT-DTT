package org.dtt.mscatalog.infrastructure.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.request.Inventory.RentalProductRequest;
import org.dtt.mscatalog.application.dto.request.Inventory.RentalProductUpdateRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.RentalProductResponse;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ActivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.DeactivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ReplenishStockUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.WithdrawStockUseCase;
import org.dtt.mscatalog.application.port.in.rentalProductUseCase.CreateRentalUseCase;
import org.dtt.mscatalog.application.port.in.rentalProductUseCase.UpdateRentalUseCase;
import org.dtt.mscatalog.infrastructure.utils.ConsumerResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/rental-products")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class RentalProductController {


    private final CreateRentalUseCase createRentalUseCase;
    private final UpdateRentalUseCase updateRentalUseCase;
    private final ActivateInventoryItemUseCase activateInventoryItemUseCase;
    private final DeactivateInventoryItemUseCase deactivateInventoryItemUseCase;
    private final ReplenishStockUseCase replenishStockUseCase;
    private final WithdrawStockUseCase withdrawStockUseCase;

    public RentalProductController(
            CreateRentalUseCase createRentalUseCase,
            UpdateRentalUseCase updateRentalUseCase,
            @Qualifier("rentalProductService") ActivateInventoryItemUseCase activateInventoryItemUseCase,
            @Qualifier("rentalProductService") DeactivateInventoryItemUseCase deactivateInventoryItemUseCase,
            @Qualifier("rentalProductService") ReplenishStockUseCase replenishStockUseCase,
            @Qualifier("rentalProductService") WithdrawStockUseCase withdrawStockUseCase
    ) {
        this.createRentalUseCase = createRentalUseCase;
        this.updateRentalUseCase = updateRentalUseCase;
        this.activateInventoryItemUseCase = activateInventoryItemUseCase;
        this.deactivateInventoryItemUseCase = deactivateInventoryItemUseCase;
        this.replenishStockUseCase = replenishStockUseCase;
        this.withdrawStockUseCase = withdrawStockUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumerResponse<RentalProductResponse> create(@RequestBody @Valid RentalProductRequest request) {
        return ConsumerResponse.created(createRentalUseCase.create(request));
    }

    @PatchMapping("/{id}")
    public ConsumerResponse<RentalProductResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid RentalProductUpdateRequest request) {
        return ConsumerResponse.success(updateRentalUseCase.update(id, request));
    }

    @PatchMapping("/{id}/replenish")
    public ConsumerResponse<Void> replenishStock(
            @PathVariable UUID id,
            @RequestParam Integer quantity) {
        replenishStockUseCase.replenishStock(id, quantity);
        return ConsumerResponse.success(null, "Stock replenished successfully");
    }

    @PatchMapping("/{id}/withdraw")
    public ConsumerResponse<Void> withdrawStock(
            @PathVariable UUID id,
            @RequestParam Integer quantity) {
        withdrawStockUseCase.withdrawStock(id, quantity);
        return ConsumerResponse.success(null, "Stock withdrawn successfully");
    }

    @PatchMapping("/{id}/activate")
    public ConsumerResponse<Void> activate(@PathVariable UUID id) {
        activateInventoryItemUseCase.activateProduct(id);
        return ConsumerResponse.success(null, "Product activated successfully");
    }

    @PatchMapping("/{id}/deactivate")
    public ConsumerResponse<Void> deactivate(@PathVariable UUID id) {
        deactivateInventoryItemUseCase.deactivateProduct(id);
        return ConsumerResponse.success(null, "Product deactivated successfully");
    }
}
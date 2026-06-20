package org.dtt.mscatalog.infrastructure.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.request.Inventory.SaleProductRequest;
import org.dtt.mscatalog.application.dto.request.Inventory.SaleProductUpdateRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.SaleProductResponse;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ActivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.DeactivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ReplenishStockUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.WithdrawStockUseCase;
import org.dtt.mscatalog.application.port.in.saleProductUseCase.CreateSaleUseCase;
import org.dtt.mscatalog.application.port.in.saleProductUseCase.UpdateSaleUseCase;
import org.dtt.mscatalog.infrastructure.utils.ConsumerResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/sale-products")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class SaleProductController {

    private final CreateSaleUseCase createSaleUseCase;
    private final UpdateSaleUseCase updateSaleUseCase;
    private final ActivateInventoryItemUseCase activateInventoryItemUseCase;
    private final DeactivateInventoryItemUseCase deactivateInventoryItemUseCase;
    private final ReplenishStockUseCase replenishStockUseCase;
    private final WithdrawStockUseCase withdrawStockUseCase;

    public SaleProductController(
            CreateSaleUseCase createSaleUseCase,
            UpdateSaleUseCase updateSaleUseCase,
            @Qualifier("saleProductService") ActivateInventoryItemUseCase activateInventoryItemUseCase,
            @Qualifier("saleProductService") DeactivateInventoryItemUseCase deactivateInventoryItemUseCase,
            @Qualifier("saleProductService") ReplenishStockUseCase replenishStockUseCase,
            @Qualifier("saleProductService") WithdrawStockUseCase withdrawStockUseCase
    ) {
        this.createSaleUseCase = createSaleUseCase;
        this.updateSaleUseCase = updateSaleUseCase;
        this.activateInventoryItemUseCase = activateInventoryItemUseCase;
        this.deactivateInventoryItemUseCase = deactivateInventoryItemUseCase;
        this.replenishStockUseCase = replenishStockUseCase;
        this.withdrawStockUseCase = withdrawStockUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumerResponse<SaleProductResponse> create(@RequestBody @Valid SaleProductRequest request) {
        return ConsumerResponse.created(createSaleUseCase.create(request));
    }

    @PatchMapping("/{id}")
    public ConsumerResponse<SaleProductResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid SaleProductUpdateRequest request) {
        return ConsumerResponse.success(updateSaleUseCase.update(id, request));
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
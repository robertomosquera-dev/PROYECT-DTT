package org.dtt.mscatalog.infrastructure.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.dtt.mscatalog.application.dto.request.Inventory.BundleItemRequest;
import org.dtt.mscatalog.application.dto.request.Inventory.ProductBundleRequest;
import org.dtt.mscatalog.application.dto.request.Inventory.ProductBundleUpdateRequest;
import org.dtt.mscatalog.application.dto.request.ProductImgRequest;
import org.dtt.mscatalog.application.dto.response.Inventory.ProductBundleResponse;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ActivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.DeactivateInventoryItemUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.ReplenishStockUseCase;
import org.dtt.mscatalog.application.port.in.inventoryUseCase.WithdrawStockUseCase;
import org.dtt.mscatalog.application.port.in.productBundleUseCase.*;
import org.dtt.mscatalog.infrastructure.utils.ConsumerResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/bundles")
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class ProductBundleController {

    private final CreateProductBundleUseCase createProductBundleUseCase;
    private final UpdateProductBundleUseCase updateProductBundleUseCase;
    private final ActivateInventoryItemUseCase activateInventoryItemUseCase;
    private final DeactivateInventoryItemUseCase deactivateInventoryItemUseCase;
    private final ReplenishStockUseCase replenishStockUseCase;
    private final WithdrawStockUseCase withdrawStockUseCase;
    private final AssignCategoryToBundleUseCase assignCategoryToBundleUseCase;
    private final AssignImgToBundleUserCase assignImgToBundleUserCase;
    private final CreateItemForBundle createItemForBundle;
    private final RemoveCategoryFromBundle removeCategoryFromBundle;
    private final RemoveImageFromBundle removeImageFromBundle;
    private final RemoveItemFromBundle removeItemFromBundle;

    public ProductBundleController(
            CreateProductBundleUseCase createProductBundleUseCase,
            UpdateProductBundleUseCase updateProductBundleUseCase,
            @Qualifier("productBundleService") ActivateInventoryItemUseCase activateInventoryItemUseCase,
            @Qualifier("productBundleService") DeactivateInventoryItemUseCase deactivateInventoryItemUseCase,
            @Qualifier("productBundleService") ReplenishStockUseCase replenishStockUseCase,
            @Qualifier("productBundleService") WithdrawStockUseCase withdrawStockUseCase,
            AssignCategoryToBundleUseCase assignCategoryToBundleUseCase,
            AssignImgToBundleUserCase assignImgToBundleUserCase,
            CreateItemForBundle createItemForBundle,
            RemoveCategoryFromBundle removeCategoryFromBundle,
            RemoveImageFromBundle removeImageFromBundle,
            RemoveItemFromBundle removeItemFromBundle) {
        this.createProductBundleUseCase = createProductBundleUseCase;
        this.updateProductBundleUseCase = updateProductBundleUseCase;
        this.activateInventoryItemUseCase = activateInventoryItemUseCase;
        this.deactivateInventoryItemUseCase = deactivateInventoryItemUseCase;
        this.replenishStockUseCase = replenishStockUseCase;
        this.withdrawStockUseCase = withdrawStockUseCase;
        this.assignCategoryToBundleUseCase = assignCategoryToBundleUseCase;
        this.assignImgToBundleUserCase = assignImgToBundleUserCase;
        this.createItemForBundle = createItemForBundle;
        this.removeCategoryFromBundle = removeCategoryFromBundle;
        this.removeImageFromBundle = removeImageFromBundle;
        this.removeItemFromBundle = removeItemFromBundle;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumerResponse<ProductBundleResponse> create(
            @RequestPart("data")
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @Valid ProductBundleRequest data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        List<ProductImgRequest> imgRequests = images != null
                ? images.stream()
                .map(img -> ProductImgRequest.builder().image(img).build())
                .toList()
                : List.of();

        return ConsumerResponse.created(createProductBundleUseCase.create(data, imgRequests));
    }

    @PutMapping("/{id}")
    public ConsumerResponse<ProductBundleResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid ProductBundleUpdateRequest request) {

        return ConsumerResponse.success(
                updateProductBundleUseCase.update(id, request)
        );
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

    @PostMapping("/{id}/categories/{categoryId}")
    public ConsumerResponse<ProductBundleResponse> assignCategory(
            @PathVariable UUID id,
            @PathVariable UUID categoryId) {

        return ConsumerResponse.success(
                assignCategoryToBundleUseCase.assignCategoryToBundle(id, categoryId)
        );
    }

    @DeleteMapping("/{id}/categories/{categoryId}")
    public ConsumerResponse<ProductBundleResponse> removeCategory(
            @PathVariable UUID id,
            @PathVariable UUID categoryId) {

        return ConsumerResponse.success(
                removeCategoryFromBundle.removeCategoryFromBundle(id, categoryId)
        );
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ConsumerResponse<ProductBundleResponse> assignImage(
            @PathVariable UUID id,
            @RequestPart("image") MultipartFile image) {

        ProductImgRequest imgRequest = ProductImgRequest.builder().image(image).build();
        return ConsumerResponse.success(
                assignImgToBundleUserCase.assignImgToBundle(id, imgRequest)
        );
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ConsumerResponse<ProductBundleResponse> removeImage(
            @PathVariable UUID id,
            @PathVariable UUID imageId) {

        return ConsumerResponse.success(
                removeImageFromBundle.removeImageFromBundle(id, imageId)
        );
    }

    @PostMapping("/{bundleId}/items")
    public ConsumerResponse<ProductBundleResponse> createItem(
            @PathVariable UUID bundleId,
            @RequestBody @Valid BundleItemRequest request) {

        return ConsumerResponse.success(
                createItemForBundle.createItemForBundle(request, bundleId)
        );
    }

    @DeleteMapping("/{bundleId}/items/{itemId}")
    public ConsumerResponse<ProductBundleResponse> removeItem(
            @PathVariable UUID bundleId,
            @PathVariable UUID itemId) {

        return ConsumerResponse.success(
                removeItemFromBundle.removeItemFromBundle(bundleId, itemId)
        );
    }
}
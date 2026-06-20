package org.dtt.mscatalog.infrastructure.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.ProductFilter;
import org.dtt.mscatalog.application.dto.request.*;
import org.dtt.mscatalog.application.dto.response.ProductDetailsResponse;
import org.dtt.mscatalog.application.dto.response.ProductResponse;
import org.dtt.mscatalog.application.port.in.productUseCase.*;
import org.dtt.mscatalog.infrastructure.utils.ConsumerResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final FindProductUseCase findProductUseCase;
    private final ListProductUseCase listProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final ChangeStatusProductUseCase changeStatusProductUseCase;
    private final AssignCategoryToProductUseCase assignCategoryToProductUseCase;
    private final AssignImgToProductUseCase assignImgToProductUseCase;
    private final RemoveCategoryFromProduct removeCategoryFromProduct;
    private final RemoveImageFromProduct removeImageFromProduct;

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumerResponse<ProductResponse> create(
            @RequestPart("name") String name,
            @RequestPart("description") @Nullable String description,
            @RequestParam("categoryIds") Set<UUID> categoryIds,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        ProductRequest request = ProductRequest.builder()
                .name(name)
                .description(description)
                .categoryIds(categoryIds)
                .images(images != null ? images.stream()
                        .map(img -> ProductImgRequest.builder().image(img).build()).toList() : List.of())
                .build();

        return ConsumerResponse.created(createProductUseCase.createProduct(request));
    }

    @GetMapping("/{id}")
    public ConsumerResponse<ProductResponse> findById(@PathVariable UUID id) {
        return ConsumerResponse.success(findProductUseCase.findById(id));
    }

    @GetMapping("/name/{name}")
    public ConsumerResponse<ProductResponse> findByName(@PathVariable String name) {
        return ConsumerResponse.success(findProductUseCase.findByName(name));
    }

    @GetMapping
    public ConsumerResponse<List<ProductDetailsResponse>> list(
            @RequestParam(required = false) StatusRequest status,
            @RequestParam(required = false) List<UUID> categoryIds,
            @RequestParam(required = false) List<String> categorySlugs,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ProductFilter productFilter = ProductFilter.builder()
                .status(status)
                .categoryIds(categoryIds)
                .categorySlugs(categorySlugs)
                .build();

        return ConsumerResponse.success(
                listProductUseCase.listProducts(productFilter, PageRequest.of(page, size))
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ConsumerResponse<ProductResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid ProductUpdateRequest request) {
        return ConsumerResponse.success(updateProductUseCase.update(id, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ConsumerResponse<Void> delete(@PathVariable UUID id) {
        deleteProductUseCase.deleteProduct(id);
        return ConsumerResponse.success(null, "product deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PatchMapping("/{id}/status")
    public ConsumerResponse<Void> changeStatus(
            @PathVariable UUID id,
            @RequestParam StatusRequest status) {
        changeStatusProductUseCase.changeStatusProduct(id, status);
        return ConsumerResponse.success(null, "status updated successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping("/{productId}/categories/{categoryId}")
    public ConsumerResponse<ProductResponse> addCategory(
            @PathVariable UUID productId,
            @PathVariable UUID categoryId) {
        return ConsumerResponse.success(
                assignCategoryToProductUseCase.assignCategoryToProductId(productId, categoryId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{productId}/categories/{categoryId}")
    public ConsumerResponse<ProductResponse> removeCategory(
            @PathVariable UUID productId,
            @PathVariable UUID categoryId) {
        return ConsumerResponse.success(
                removeCategoryFromProduct.removeCategoryFromProduct(productId, categoryId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping(value = "/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ConsumerResponse<ProductResponse> addImage(
            @PathVariable UUID productId,
            @ModelAttribute @Valid ProductImgRequest request
    ) {
        return ConsumerResponse.success(
                assignImgToProductUseCase.assignImgToProduct(productId, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{productId}/images/{imageId}")
    public ConsumerResponse<ProductResponse> removeImage(
            @PathVariable UUID productId,
            @PathVariable UUID imageId) {
        return ConsumerResponse.success(
                removeImageFromProduct.removeImageFromProduct(productId, imageId));
    }
}
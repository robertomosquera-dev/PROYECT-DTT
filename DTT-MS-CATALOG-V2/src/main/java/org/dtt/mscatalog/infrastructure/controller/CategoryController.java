package org.dtt.mscatalog.infrastructure.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.dto.CategoryFilter;
import org.dtt.mscatalog.application.dto.request.CategoryRequest;
import org.dtt.mscatalog.application.dto.request.StatusRequest;
import org.dtt.mscatalog.application.dto.request.UpdateCategoryBasicInfoRequest;
import org.dtt.mscatalog.application.dto.response.CategoryResponse;
import org.dtt.mscatalog.application.dto.response.CategoryTreeResponse;
import org.dtt.mscatalog.application.port.in.categoryUseCase.*;
import org.dtt.mscatalog.domain.model.Enum.CategoryStatus;
import org.dtt.mscatalog.infrastructure.utils.ConsumerResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CreateCategoryUseCase createUseCase;
    private final UpdateCategoryUseCase updateUseCase;
    private final DeleteCategoryUseCase deleteUseCase;
    private final FindCategoryUseCase findUseCase;
    private final ListCategoriesUseCase listUseCase;
    private final ChangeStatusCategoryUseCase statusUseCase;
    private final TreeCategoriesUseCase treeUseCase;
    private final UpdateImageCategoryUseCase updateImageUseCase;
    private final DeleteImgCategoryUseCase deleteImageUseCase;

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ConsumerResponse<CategoryResponse> updateImage(
            @PathVariable UUID id,
            @RequestPart("image") MultipartFile image) {

        return ConsumerResponse.success(
                updateImageUseCase.updateCategoryImage(id, image),
                "category image updated successfully"
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{id}/image")
    public ConsumerResponse<Void> deleteImage(
            @PathVariable UUID id) {

        deleteImageUseCase.deleteImage(id);

        return ConsumerResponse.success(
                null,
                "category image deleted successfully"
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ConsumerResponse<CategoryResponse> create(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @RequestPart("data") @Valid CategoryRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ConsumerResponse.created(
                createUseCase.createCategory(request, image)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PatchMapping("/{id}")
    public ConsumerResponse<CategoryResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCategoryBasicInfoRequest request) {

        return ConsumerResponse.success(
                updateUseCase.updateCategory(id, request),
                "category updated successfully"
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @PatchMapping("/{id}/status")
    public ConsumerResponse<Void> changeStatus(
            @PathVariable UUID id,
            @RequestParam StatusRequest status) {

        statusUseCase.changeStatusCategory(id, status);

        return ConsumerResponse.success(
                null,
                "category status updated successfully"
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ConsumerResponse<Void> delete(
            @PathVariable UUID id) {

        deleteUseCase.deleteCategory(id);

        return ConsumerResponse.success(
                null,
                "category deleted successfully"
        );
    }

    @GetMapping("/{id}")
    public ConsumerResponse<CategoryResponse> getById(
            @PathVariable UUID id) {

        return ConsumerResponse.success(
                findUseCase.findById(id)
        );
    }

    @GetMapping("/slug/{slug}")
    public ConsumerResponse<CategoryResponse> getBySlug(
            @PathVariable String slug) {

        return ConsumerResponse.success(
                findUseCase.findBySlug(slug)
        );
    }

    @GetMapping("/name/{name}")
    public ConsumerResponse<CategoryResponse> getByName(
            @PathVariable String name) {

        return ConsumerResponse.success(
                findUseCase.findByName(name)
        );
    }

    @GetMapping("/path")
    public ConsumerResponse<CategoryResponse> getByPath(
            @RequestParam String path) {

        return ConsumerResponse.success(
                findUseCase.findByPath(path)
        );
    }

    @GetMapping("/tree/{parentId}")
    public ConsumerResponse<CategoryTreeResponse> getTree(
            @PathVariable UUID parentId) {

        return ConsumerResponse.success(
                treeUseCase.getTreeCategoriesByParentId(parentId)
        );
    }

    @GetMapping
    public ConsumerResponse<List<CategoryResponse>> list(
            @RequestParam(required = false) UUID parentId,
            @RequestParam(required = false) CategoryStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        CategoryFilter filter = CategoryFilter.builder()
                .parentId(parentId)
                .status(status)
                .build();

        return ConsumerResponse.success(
                listUseCase.listCategories(
                        filter,
                        PageRequest.of(page, size)
                )
        );
    }
}
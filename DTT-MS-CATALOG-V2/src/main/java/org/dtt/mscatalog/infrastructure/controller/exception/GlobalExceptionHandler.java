package org.dtt.mscatalog.infrastructure.controller.exception;

import jakarta.persistence.PersistenceException;
import org.dtt.mscatalog.application.exception.*;
import org.dtt.mscatalog.domain.exception.*;
import org.dtt.mscatalog.infrastructure.utils.ConsumerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ConsumerResponse<Void>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ConsumerResponse.error(404, ex.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ConsumerResponse<Void>> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ConsumerResponse.error(404, ex.getMessage()));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ConsumerResponse<Void>> handleAuthorizationDenied(
            AuthorizationDeniedException ex
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ConsumerResponse.error(
                        403,
                        "You do not have permission to perform this action"
                ));
    }

    @ExceptionHandler(SlugAlreadyExistsException.class)
    public ResponseEntity<ConsumerResponse<Void>> handleSlugAlreadyExists(SlugAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ConsumerResponse.error(409, ex.getMessage()));
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ConsumerResponse<Void>> handleImageNotFound(ImageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ConsumerResponse.error(404, ex.getMessage()));
    }

    @ExceptionHandler({
            CategoryAlreadyEnabledException.class,
            CategoryAlreadyDisabledException.class,
            CategoryAlreadyDeletedException.class,
            InvalidSlugException.class,
            SelfParentCategoryException.class,
            InvalidImageOrderException.class,
            InvalidImageUrlException.class,
            CategoryAlreadyExistsException.class,
            CategoryNotFoundInProductException.class,
            ProductAlreadyDeletedException.class,
            ProductAlreadyDisabledException.class,
            ProductAlreadyEnabledException.class,
            ProductInvalidStatusException.class,
            ProductAlreadyActiveException.class,
            ProductAlreadyInactiveException.class,
            InventoryItemAlreadyDeletedException.class,
            InvalidStockException.class,
            StockNotInitializedException.class,
            InvalidProductException.class,
            InvalidNameException.class,
            InvalidDescriptionException.class,
            InvalidPriceException.class,
            InvalidRollsCountException.class
    })
    public ResponseEntity<ConsumerResponse<Void>> handleDomainExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ConsumerResponse.error(422, ex.getMessage()));
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ConsumerResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ConsumerResponse.error(400, ex.getMessage()));
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ConsumerResponse<Void>> handlePersistenceException(PersistenceException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ConsumerResponse.error(500, ex.getMessage()));
    }

    @ExceptionHandler({
            ProductAlreadyInSaleException.class,
            ProductAlreadyInRentalException.class,
            InsufficientStockException.class
    })
    public ResponseEntity<ConsumerResponse<Void>> handleConflictExceptions(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ConsumerResponse.error(409, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ConsumerResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        field -> field.getDefaultMessage() != null ? field.getDefaultMessage() : "Invalid field"
                ));

        return ResponseEntity.status(422)
                .body(ConsumerResponse.error(422, "Validation Error", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ConsumerResponse<Void>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ConsumerResponse.error(500, "Internal Server Error"));
    }
}
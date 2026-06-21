package org.dtt.msorder.exception;

import org.dtt.msorder.utils.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExepcionGlobalHandler {

    @ExceptionHandler(ExepcionCatalog.class)
    public ResponseEntity<ApiResponse<Void>> catalogError(ExepcionCatalog e) {
        return ResponseEntity
                .status(e.getHttp())
                .body(ApiResponse.error(e.getMessage(), e.getHttp().value()));
    }

}
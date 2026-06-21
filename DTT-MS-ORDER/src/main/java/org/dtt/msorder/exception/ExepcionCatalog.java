package org.dtt.msorder.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExepcionCatalog extends RuntimeException {

    private HttpStatus http;

    public ExepcionCatalog(String message,HttpStatus http) {
        super(message);
        this.http = http;
    }
}

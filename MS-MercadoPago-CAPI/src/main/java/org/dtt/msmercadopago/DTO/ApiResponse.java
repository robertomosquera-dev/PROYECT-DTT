package org.dtt.msmercadopago.DTO;

public record ApiResponse<T>(
        int statusCode,
        boolean success,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(String message, int statusCode, T data) {
        return new ApiResponse<>(statusCode, true, message, data);
    }

    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return new ApiResponse<>(statusCode, false, message, null);
    }
}

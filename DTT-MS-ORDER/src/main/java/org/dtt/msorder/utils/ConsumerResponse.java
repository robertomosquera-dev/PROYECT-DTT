package org.dtt.mscatalog.infrastructure.utils;

public record ConsumerResponse<T> (
        Boolean isError,
        Integer code,
        String message,
        T data
) implements IConsumerResponse<T> {
    public static <T> ConsumerResponse<T> success(T data) {
        return new ConsumerResponse<>(false, 200, "successful operation", data);
    }
    public static <T> ConsumerResponse<T> success(T data, String message) {
        return new ConsumerResponse<>(false, 200, message, data);
    }
    public static <T> ConsumerResponse<T> error(Integer code, String message) {
        return new ConsumerResponse<>(true, code, message, null);
    }
    public static <T> ConsumerResponse<T> error(Integer code, String message, T data) {
        return new ConsumerResponse<>(true, code, message, data);
    }
    public static <T> ConsumerResponse<T> created(T data) {
        return new ConsumerResponse<>(false, 201, "created resource", data);
    }
}

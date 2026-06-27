package org.dtt.msorder.utils;

public interface IConsumerResponse<T>{
    Boolean isError();
    Integer code();
    String message();
    T data();
}

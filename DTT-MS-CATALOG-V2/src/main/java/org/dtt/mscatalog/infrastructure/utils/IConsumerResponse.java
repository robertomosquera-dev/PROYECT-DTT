package org.dtt.mscatalog.infrastructure.utils;

public interface IConsumerResponse <T>{
    Boolean isError();
    Integer code();
    String message();
    T data();
}

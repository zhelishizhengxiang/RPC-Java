package com.simon.common.exception;

/**
 * @ClassName SerializeException
 * @Description 自定义的序列化异常
 * @Author Tong
 * @LastChangeDate 2024-12-02 19:18
 * @Version v1.0
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String message) {
        super(message);
    }
    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}

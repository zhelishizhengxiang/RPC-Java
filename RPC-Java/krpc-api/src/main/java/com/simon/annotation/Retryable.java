package com.simon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version 1.0
 * @ProjectName: RPC-Java
 * @Package: com.simon.annotation
 * @Description: 可重试注解
 * @Author: Simon
 * @CreateDate: 2025/10/19
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Retryable {
}

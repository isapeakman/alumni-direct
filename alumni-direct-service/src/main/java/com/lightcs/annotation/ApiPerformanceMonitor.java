package com.lightcs.annotation;

import java.lang.annotation.*;

/**
 * API性能监控注解
 * 用于标记需要监控调用耗时的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiPerformanceMonitor {

    /**
     * 服务类型标识
     */
    String serviceType() default "DEFAULT";
}
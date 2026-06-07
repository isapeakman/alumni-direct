package com.lightcs.annotation;

import java.lang.annotation.*;

/**
 * Token使用量监控注解
 * 用于标记需要统计Token消耗的LLM调用方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenUsageMonitor {
}
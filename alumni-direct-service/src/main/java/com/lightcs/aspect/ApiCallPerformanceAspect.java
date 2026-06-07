package com.lightcs.aspect;

import com.alibaba.fastjson2.JSON;
import com.lightcs.annotation.ApiPerformanceMonitor;
import com.lightcs.model.pojo.ApiCallLog;
import com.lightcs.service.ApiCallLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * API调用性能监控切面（基于注解）
 * 用于统计外部API调用的耗时
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiCallPerformanceAspect {

    private final ApiCallLogService apiCallLogService;

    /**
     * 监控带有@ApiPerformanceMonitor注解的方法
     */
    @Around("@annotation(com.lightcs.annotation.ApiPerformanceMonitor)")
    public Object monitorApiPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // 获取注解参数
        ApiPerformanceMonitor annotation = signature.getMethod().getAnnotation(ApiPerformanceMonitor.class);
        String serviceType = annotation != null ? annotation.serviceType() : "DEFAULT";

        try {
            log.info("🚀 [{}监控] 开始调用 {}.{} | 参数数量: {}",
                    serviceType, className, methodName, joinPoint.getArgs().length);

            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            int responseLength = 0;
            if (result instanceof String) {
                responseLength = ((String) result).length();
            }

            log.info("✅ [{}监控] {}.{} 执行成功 | 耗时: {}ms | 返回长度: {} 字符",
                    serviceType, className, methodName, duration, responseLength);

            // 性能告警：超过3秒的调用
            if (duration > 3000) {
                log.warn("⚠️ [{}告警] {}.{} 耗时较长: {}ms，建议检查网络或优化",
                        serviceType, className, methodName, duration);
            }

            // 保存API调用日志
            ApiCallLog apiCallLog = ApiCallLog.builder()
                    .serviceName(serviceType + ":" + className)
                    .methodName(methodName)
                    .duration(duration)
                    .success(true)
                    .requestParams(truncateParams(joinPoint.getArgs()))
                    .responseLength(responseLength)
                    .build();
            apiCallLogService.saveApiCallLog(apiCallLog);

            return result;

        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.error("❌ [{}监控] {}.{} 执行失败 | 耗时: {}ms | 异常: {}",
                    serviceType, className, methodName, duration, throwable.getMessage());

            // 保存失败的API调用日志
            ApiCallLog apiCallLog = ApiCallLog.builder()
                    .serviceName(serviceType + ":" + className)
                    .methodName(methodName)
                    .duration(duration)
                    .success(false)
                    .errorMessage(throwable.getMessage())
                    .requestParams(truncateParams(joinPoint.getArgs()))
                    .build();
            apiCallLogService.saveApiCallLog(apiCallLog);

            throw throwable;
        }
    }

    /**
     * 截断参数，避免日志过大
     */
    private String truncateParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        try {
            String json = JSON.toJSONString(args);
            if (json.length() > 2000) {
                return json.substring(0, 2000) + "...(truncated)";
            }
            return json;
        } catch (Exception e) {
            return "[参数序列化失败]";
        }
    }
}
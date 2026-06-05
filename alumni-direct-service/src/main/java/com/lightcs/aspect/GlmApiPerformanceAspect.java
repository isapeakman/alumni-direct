package com.lightcs.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * GLM API调用性能监控切面
 * 用于统计所有GLM API调用的耗时
 */
@Slf4j
@Aspect
@Component
public class GlmApiPerformanceAspect {

    /**
     * 环绕通知：监控GlmApiService所有公共方法的执行时间
     */
    @Around("execution(public * com.lightcs.component.GlmApiService.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            // 记录开始日志
            log.info("🚀 [性能监控] 开始调用 {}.{} | 参数数量: {}",
                    className, methodName, joinPoint.getArgs().length);

            // 执行目标方法
            Object result = joinPoint.proceed();

            // 计算耗时
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录成功日志
            if (result instanceof String) {
                String strResult = (String) result;
                log.info("✅ [性能监控] {}.{} 执行成功 | 耗时: {}ms | 返回长度: {} 字符",
                        className, methodName, duration, strResult.length());
            } else {
                log.info("✅ [性能监控] {}.{} 执行成功 | 耗时: {}ms",
                        className, methodName, duration);
            }

            // 性能告警：超过3秒的调用
            if (duration > 3000) {
                log.warn("⚠️ [性能告警] {}.{} 耗时较长: {}ms，建议检查网络或优化提示词",
                        className, methodName, duration);
            }

            return result;

        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录失败日志
            log.error("❌ [性能监控] {}.{} 执行失败 | 耗时: {}ms | 异常: {}",
                    className, methodName, duration, throwable.getMessage());

            throw throwable;
        }
    }
}

package com.lightcs.aspect;

import com.lightcs.model.dto.ApiCallResult;
import com.lightcs.model.pojo.GlmTokenLog;
import com.lightcs.service.ApiCallLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * Token使用量统计切面（基于注解）
 * 用于统计LLM模型的Token消耗
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class TokenUsageAspect {

    private final ApiCallLogService apiCallLogService;

    /**
     * 监控带有@TokenUsageMonitor注解的方法
     */
    @Around("@annotation(com.lightcs.annotation.TokenUsageMonitor)")
    public Object monitorTokenUsage(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            Object result = joinPoint.proceed();

            // 如果返回的是ApiCallResult，提取Token信息并保存
            if (result instanceof ApiCallResult) {
                ApiCallResult apiResult = (ApiCallResult) result;

                // 打印Token用量日志
                log.info("[{}] Token 用量 -> Prompt: {}, Generation: {}, Total: {}",
                        className,
                        apiResult.getPromptTokens(),
                        apiResult.getCompletionTokens(),
                        apiResult.getTotalTokens());

                // 保存Token日志（只有当有Token信息时才保存）
                if (apiResult.getPromptTokens() != null || apiResult.getCompletionTokens() != null) {
                    GlmTokenLog tokenLog = GlmTokenLog.builder()
                            .promptTokens(apiResult.getPromptTokens())
                            .completionTokens(apiResult.getCompletionTokens())
                            .totalTokens(apiResult.getTotalTokens())
                            .model(apiResult.getModel())
                            .build();
                    apiCallLogService.saveGlmTokenLog(tokenLog);
                }
            }

            return result;

        } catch (Throwable throwable) {
            log.error("❌ [Token统计] {}.{} 执行失败 | 异常: {}",
                    className, methodName, throwable.getMessage());
            throw throwable;
        }
    }
}
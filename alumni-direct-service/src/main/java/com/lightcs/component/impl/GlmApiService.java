package com.lightcs.component.impl;

import com.lightcs.annotation.ApiPerformanceMonitor;
import com.lightcs.annotation.TokenUsageMonitor;
import com.lightcs.component.LLMApiStrategy;
import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.BusinessException;
import com.lightcs.model.dto.ApiCallResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;

/**
 * GLM API服务类
 * 实现LLMApiStrategy接口，提供简历解析和AI面试两个核心业务方法
 */
@Slf4j
@Service
public class GlmApiService implements LLMApiStrategy {

    private final ChatClient resumeParseChatClient;
    private final ChatClient aiInterviewChatClient;
    private final ChatClient aiInterviewEvaluationChatClient;

    private final ZhiPuAiChatModel chatModel;

    /**
     * Constructor for GlmApiService class.
     * Initializes the service with required chat clients and chat model.
     *
     * @param resumeParseChatClient           ChatClient instance for parsing resumes
     * @param aiInterviewChatClient           ChatClient instance for AI interview functionality
     * @param aiInterviewEvaluationChatClient
     * @param chatModel                       ZhiPuAiChatModel instance for chat operations
     */
    public GlmApiService(ChatClient resumeParseChatClient,
                         ChatClient aiInterviewChatClient,
                         ChatClient aiInterviewEvaluationChatClient, ZhiPuAiChatModel chatModel) {
        // Initialize resume parsing chat client
        this.resumeParseChatClient = resumeParseChatClient;
        // Initialize AI interview chat client
        this.aiInterviewChatClient = aiInterviewChatClient;
        this.aiInterviewEvaluationChatClient = aiInterviewEvaluationChatClient;
        // Initialize chat model
        this.chatModel = chatModel;
    }

    // ============ 业务方法（返回 String，供业务层调用） ============

    @Override
    public String parseResume(String resumeText) {
        log.info("调用GLM进行简历解析");
        // 获取代理对象，调用被切面修饰的方法
        GlmApiService glmApiService = (GlmApiService) AopContext.currentProxy();
        return glmApiService.parseResumeWithToken(resumeText).getContent();
    }

    @Override
    public String interview(String userInput) {
        log.info("调用GLM进行AI模拟面试（使用预设系统提示词）");
        // 获取代理对象，调用被切面修饰的方法
        GlmApiService glmApiService = (GlmApiService) AopContext.currentProxy();
        return glmApiService.interviewWithToken(userInput).getContent();
    }

    @Override
    public String evaluateInterview(String userInput) {
        log.info("调用GLM进行AI面试评价");
        // 获取代理对象，调用被切面修饰的方法
        GlmApiService glmApiService = (GlmApiService) AopContext.currentProxy();
        return glmApiService.evaluateInterviewWithToken(userInput).getContent();
    }

    @ApiPerformanceMonitor(serviceType = "GLM-INTERVIEW-EVALUATE")
    @TokenUsageMonitor
    public ApiCallResult evaluateInterviewWithToken(String userInput) {
        return callWithClient(aiInterviewEvaluationChatClient, userInput, "interview-evaluate");
    }


    @Override
    public String getProvider() {
        return "GLM-4.5-air";
    }

    /**
     * 简历解析（带Token统计）- 被切面修饰
     */
    @ApiPerformanceMonitor(serviceType = "GLM-RESUME-PARSE")
    @TokenUsageMonitor
    public ApiCallResult parseResumeWithToken(String resumeText) {
        return callWithClient(resumeParseChatClient, resumeText, "resume-parse");
    }

    /**
     * AI面试（带Token统计）- 使用预设系统提示词
     */
    @ApiPerformanceMonitor(serviceType = "GLM-INTERVIEW")
    @TokenUsageMonitor
    public ApiCallResult interviewWithToken(String userInput) {
        return callWithClient(aiInterviewChatClient, userInput, "ai-interview");
    }


    // ============ 内部辅助方法 ============

    private ApiCallResult callWithClient(ChatClient client, String prompt, String scenario) {
        try {
            ChatResponse response = client.prompt()
                    .user(prompt)
                    .call()
                    .chatResponse();
            String content = response.getResult().getOutput().getText();
            ApiCallResult result = extractTokenUsage(response, content);
            log.info("GLM {}调用成功，返回长度: {}, 输入Token: {}, 输出Token: {}",
                    scenario, content.length(), result.getPromptTokens(), result.getCompletionTokens());
            return result;
        } catch (Exception e) {
            log.error("调用GLM {}失败", scenario, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用GLM失败: " + e.getMessage());
        }
    }

    /**
     * 获取Token使用情况
     *
     * @param response
     * @param content
     * @return
     */
    private ApiCallResult extractTokenUsage(ChatResponse response, String content) {
        var metadata = response.getMetadata();
        if (metadata != null && metadata.getUsage() != null) {
            var usage = metadata.getUsage();
            return ApiCallResult.builder()
                    .content(content)
                    .promptTokens(usage.getPromptTokens())
                    .completionTokens(usage.getCompletionTokens())
                    .totalTokens(usage.getTotalTokens())
                    .model(this.getProvider())
                    .build();
        }
        return ApiCallResult.builder()
                .content(content)
                .promptTokens(null)
                .completionTokens(null)
                .totalTokens(null)
                .model(this.getProvider())
                .build();
    }


}

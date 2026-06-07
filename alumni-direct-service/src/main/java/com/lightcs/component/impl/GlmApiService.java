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
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.stereotype.Service;

/**
 * GLM API服务类
 * 用于调用GLM大模型进行文本处理和JSON结构化
 */
@Slf4j
@Service
public class GlmApiService implements LLMApiStrategy {

    private final ChatClient chatClient;
    private final ZhiPuAiChatModel chatModel;
    private static final String RESUME_PARSE_TEMPLATE = "resume-parse.txt";

    public GlmApiService(ChatClient.Builder chatClientBuilder,
                         ZhiPuAiChatModel chatModel) {
        this.chatClient = chatClientBuilder.build();
        this.chatModel = chatModel;
    }

    /**
     * 实现抽象方法：调用GLM API
     */
    @Override
    public String callApi(String prompt) {
        return callApiWithTokenInfo(prompt).getContent();
    }

    /**
     * 调用GLM API并返回Token信息（使用Spring AI标准API）
     */
    @Override
    @ApiPerformanceMonitor(serviceType = "GLM")
    @TokenUsageMonitor
    public ApiCallResult callApiWithTokenInfo(String prompt) {
        try {
            log.info("调用GLM API");

            // 使用Spring AI标准API获取完整响应
            ChatResponse response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .chatResponse();

            String content = response.getResult().getOutput().getText();

            // 获取Token用量
            ApiCallResult result = extractTokenUsage(response, content);

            log.info("GLM API调用成功，返回结果长度: {}, 输入Token: {}, 输出Token: {}, 总Token: {}",
                    content.length(), result.getPromptTokens(), result.getCompletionTokens(), result.getTotalTokens());

            return result;
        } catch (Exception e) {
            log.error("调用GLM API失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用GLM API失败: " + e.getMessage());
        }
    }

    /**
     * 提取Token使用信息
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

    /**
     * 实现抽象方法：加载提示词模板
     */
    @Override
    public String loadPromptTemplate() {
        return RESUME_PARSE_TEMPLATE;
    }

    /**
     * 获取当前使用的LLM提供商名称
     */
    @Override
    public String getProvider() {
        return "GLM-4.5-air";
    }

    /**
     * 带系统提示词的LLM调用（GLM特有功能）
     */
    public String chatWithSystem(String systemPrompt, String userPrompt) {
        return chatWithSystemWithTokenInfo(systemPrompt, userPrompt).getContent();
    }

    /**
     * 带系统提示词的LLM调用并返回Token信息
     */
    @ApiPerformanceMonitor(serviceType = "GLM")
    @TokenUsageMonitor
    public ApiCallResult chatWithSystemWithTokenInfo(String systemPrompt, String userPrompt) {
        try {
            log.info("调用GLM API（带系统提示）");

            ChatResponse response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .chatResponse();

            String content = response.getResult().getOutput().getText();
            ApiCallResult result = extractTokenUsage(response, content);

            log.info("GLM API调用成功，输入Token: {}, 输出Token: {}, 总Token: {}",
                    result.getPromptTokens(), result.getCompletionTokens(), result.getTotalTokens());

            return result;
        } catch (Exception e) {
            log.error("调用GLM API失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用GLM API失败: " + e.getMessage());
        }
    }

    /**
     * 直接调用LLM（可指定模型和参数）
     */
    public String directCall(String message, String model, Double temperature) {
        return directCallWithTokenInfo(message, model, temperature).getContent();
    }

    /**
     * 直接调用LLM并返回Token信息
     */
    @ApiPerformanceMonitor(serviceType = "GLM")
    @TokenUsageMonitor
    public ApiCallResult directCallWithTokenInfo(String message, String model, Double temperature) {
        try {
            String targetModel = model != null ? model : "glm-4.6";
            log.info("直接调用GLM模型: {}, temperature: {}", targetModel, temperature);

            ChatResponse response = chatClient.prompt()
                    .user(message)
                    .options(ZhiPuAiChatOptions.builder()
                            .model(targetModel)
                            .temperature(temperature != null ? temperature : 0.7)
                            .build())
                    .call()
                    .chatResponse();

            String content = response.getResult().getOutput().getText();
            ApiCallResult result = extractTokenUsage(response, content);
            result.setModel(targetModel);

            log.info("GLM模型调用成功，输入Token: {}, 输出Token: {}, 总Token: {}",
                    result.getPromptTokens(), result.getCompletionTokens(), result.getTotalTokens());

            return result;
        } catch (Exception e) {
            log.error("调用GLM模型失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用GLM模型失败: " + e.getMessage());
        }
    }
}
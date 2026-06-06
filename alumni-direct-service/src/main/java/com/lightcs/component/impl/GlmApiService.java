package com.lightcs.component.impl;

import com.lightcs.component.LLMApiStrategy;
import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
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
        try {
            log.info("调用GLM API");
            // 使用Spring AI的ChatClient调用GLM API
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("GLM API调用成功，返回结果长度: {}", response.length());
            return response;
        } catch (Exception e) {
            log.error("调用GLM API失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用GLM API失败: " + e.getMessage());
        }
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
        return "GLM";
    }

    /**
     * 带系统提示词的LLM调用（GLM特有功能）
     *
     * @param systemPrompt 系统提示词（设定角色、规则等）
     * @param userPrompt   用户提示词（具体问题或任务）
     * @return LLM响应内容
     */
    public String chatWithSystem(String systemPrompt, String userPrompt) {
        try {
            log.info("调用GLM API（带系统提示）");

            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            log.info("GLM API调用成功");
            return response;
        } catch (Exception e) {
            log.error("调用GLM API失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用GLM API失败: " + e.getMessage());
        }
    }

    /**
     * 直接调用LLM（可指定模型和参数）（GLM特有功能）
     *
     * @param message     消息内容
     * @param model       模型名称，默认glm-4.6
     * @param temperature 温度参数，默认0.7
     * @return LLM响应内容
     */
    public String directCall(String message, String model, Double temperature) {
        try {
            log.info("直接调用GLM模型: {}, temperature: {}", model, temperature);

            ChatResponse response = chatModel.call(
                    new Prompt(
                            message,
                            ZhiPuAiChatOptions.builder()
                                    .model(model != null ? model : "glm-4.6")
                                    .temperature(temperature != null ? temperature : 0.7)
                                    .build()
                    )
            );

            String result = response.getResult().getOutput().getText();
            log.info("GLM模型调用成功");
            return result;
        } catch (Exception e) {
            log.error("调用GLM模型失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "调用GLM模型失败: " + e.getMessage());
        }
    }
}

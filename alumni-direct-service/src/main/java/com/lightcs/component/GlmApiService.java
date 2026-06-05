package com.lightcs.component;

import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.BusinessException;
import com.lightcs.provider.PromptTemplateService;
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
public class GlmApiService {

    private final ChatClient chatClient;
    private final ZhiPuAiChatModel chatModel;
    private final PromptTemplateService promptTemplateService;

    public GlmApiService(ChatClient.Builder chatClientBuilder,
                         ZhiPuAiChatModel chatModel,
                         PromptTemplateService promptTemplateService) {
        this.chatClient = chatClientBuilder.build();
        this.chatModel = chatModel;
        this.promptTemplateService = promptTemplateService;
    }

    /**
     * 调用GLM API进行简历结构化
     *
     * @param resumeText 简历文本内容
     * @return 结构化的JSON字符串
     */
    public String parseResumeToJson(String resumeText) {
        String prompt = buildResumeParsePrompt(resumeText);
        return callGlmApi(prompt);
    }

    /**
     * 构建简历解析Prompt
     */
    private String buildResumeParsePrompt(String resumeText) {
        try {
            // 从模板文件加载提示词
            String template = promptTemplateService.loadPrompt("resume-parse.txt");
            // 填充变量
            return promptTemplateService.fillTemplate(template,
                    java.util.Map.of("resume_text", resumeText));
        } catch (Exception e) {
            log.error("加载简历解析提示词模板失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "加载提示词模板失败: " + e.getMessage());
        }
    }

    /**
     * 调用GLM API
     */
    private String callGlmApi(String prompt) {
        try {
            log.info("调用GLM API进行简历解析");

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
     * 调用GLM API（带系统提示）
     *
     * @param systemPrompt 系统提示
     * @param userPrompt   用户提示
     * @return AI响应内容
     */
    public String callWithSystemPrompt(String systemPrompt, String userPrompt) {
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
     * 直接调用GLM模型（可指定模型和参数）
     *
     * @param message     消息内容
     * @param model       模型名称，默认glm-4.6
     * @param temperature 温度参数，默认0.7
     * @return AI响应内容
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

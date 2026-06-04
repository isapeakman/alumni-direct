package com.lightcs.component;

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
        return """
                你是一位专业的简历解析专家。以下文本是通过OCR技术从简历图片中识别出来的，可能存在识别错误。
                            
                **重要任务**：
                1. **首先纠正OCR识别错误**：根据上下文和常识修正明显的识别错误（如：5pringBoot→SpringBoot, My5QL→MySQL, c0m→com等）
                2. **然后提取结构化信息**：将纠正后的内容解析为JSON格式
                            
                **常见OCR错误示例**：
                - 数字与字母混淆：0→O, 1→l/I, 5→S, 8→B
                - 特殊字符错误：井→并, +-→+, |→/
                - 大小写混乱：bUg→bug, pringBoot→SpringBoot
                - 邮箱域名错误：c0m→com, cn→con
                            
                **需要提取的字段**：
                - name: 姓名
                - phone: 手机号（注意纠正可能的数字识别错误）
                - email: 邮箱（特别注意域名部分的准确性）
                - location: 所在城市
                - education: 学历（如：本科、硕士、博士）
                - major: 专业
                - experienceYears: 工作年限（如：3年经验）
                - desiredPosition: 期望职位
                - desiredSalary: 期望薪资
                - educationExperience: 教育经历数组，每个元素包含：school（学校）、degree（学历）、major（专业）、startDate（开始日期）、endDate（结束日期）、description（描述）
                - workExperience: 工作经历数组，每个元素包含：company（公司）、position（职位）、startDate（开始日期）、endDate（结束日期）、description（描述）
                - projectExperience: 项目经验数组，每个元素包含：projectName（项目名称）、role（角色）、startDate（开始日期）、endDate（结束日期）、description（描述）、technologies（技术栈，注意纠正技术名词）
                - skills: 技能列表数组（注意纠正技术名词，如SpringBoot、MySQL、Redis等）
                - certificates: 证书列表数组
                - selfEvaluation: 自我评价
                            
                **输出要求**：
                1. 必须输出严格的JSON格式
                2. 不要包含任何Markdown标记（如```json）
                3. 不要有任何额外解释文字
                4. 如果某个字段无法识别，设置为null或空数组
                5. 技术名词必须使用正确的大小写（如SpringBoot而非springboot）
                            
                **OCR识别的原始文本**：
                %s
                            
                请输出纠正并结构化后的JSON：
                """.formatted(resumeText);
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
            throw new RuntimeException("调用GLM API失败: " + e.getMessage(), e);
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
            throw new RuntimeException("调用GLM API失败: " + e.getMessage(), e);
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
            throw new RuntimeException("调用GLM模型失败: " + e.getMessage(), e);
        }
    }


}

package com.lightcs.component;

import com.lightcs.provider.PromptTemplateService;
import lombok.extern.slf4j.Slf4j;

/**
 * LLM服务抽象基类（模板方法模式）
 * 定义简历解析的通用流程，子类只需实现具体的API调用
 */
@Slf4j
public abstract class AbstractLLMService {

    protected PromptTemplateService promptTemplateService;

    /**
     * 构造函数：注入 PromptTemplateService
     */
    public AbstractLLMService(PromptTemplateService promptTemplateService) {
        if (promptTemplateService == null) {
            throw new IllegalArgumentException("PromptTemplateService 不能为 null");
        }
        this.promptTemplateService = promptTemplateService;
        log.info("AbstractLLMService 初始化成功，provider: {}", this.getClass().getSimpleName());
    }

    /**
     * 模板方法：解析简历文本为结构化JSON
     * 这是一个完整的业务流程，子类不需要重写
     *
     * @param resumeText 简历文本内容
     * @return 结构化的JSON字符串
     */
    public String parseResumeToJson(String resumeText) {
        log.info("[{}] 开始解析简历", getProvider());

        // 1.获取提示词文件路径
        String template = loadPromptTemplate();
        // 2.加载提示词
        String prompt = promptTemplateService.getResumeParsePrompt(template, resumeText);
        // 3：根据提示词调用LLM API
        String jsonResult = callApi(prompt);

        log.info("[{}] 简历解析完成", getProvider());
        return jsonResult;
    }

    /**
     * 基础方法：简单聊天
     *
     * @param userPrompt 用户提示词
     * @return LLM响应内容
     */
    public String chat(String userPrompt) {
        return callApi(userPrompt);
    }

    /**
     * 获取当前使用的LLM提供商名称
     *
     * @return 提供商名称（如：GLM、OpenAI、Qwen等）
     */
    public abstract String getProvider();


    /**
     * 调用LLM API的核心方法
     * 子类需要实现具体的API调用逻辑
     *
     * @param prompt 提示词
     * @return LLM响应内容
     */
    public abstract String callApi(String prompt);

    /**
     * 加载提示词模板
     * 子类可以指定使用哪个模板文件
     *
     * @return 模板文件名
     */
    protected abstract String loadPromptTemplate();
}

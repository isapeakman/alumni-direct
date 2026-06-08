package com.lightcs.component;

public interface LLMApiStrategy {

    /**
     * 简历解析 - 使用预设的简历解析系统提示词
     *
     * @param resumeText OCR识别的简历文本
     * @return 结构化JSON字符串
     */
    String parseResume(String resumeText);

    /**
     * AI模拟面试 - 使用预设的面试系统提示词
     *
     * @param userInput 用户输入（问题或回答）
     * @return 面试回复文本
     */
    String interview(String userInput);

    /**
     * AI模拟面试 - 系统提示词和用户提示词分开传入
     *
     * @param systemPrompt 系统提示词（角色定义、职责、约束等）
     * @param userPrompt   用户提示词（简历信息、对话历史、用户回答等）
     * @return 面试回复文本
     */
    String interviewWithSystemPrompt(String systemPrompt, String userPrompt);

    /**
     * 获取当前使用的LLM提供商名称
     *
     * @return 提供商名称（如：GLM、OpenAI、Qwen等）
     */
    String getProvider();
}

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
     * 获取当前使用的LLM提供商名称
     *
     * @return 提供商名称（如：GLM、OpenAI、Qwen等）
     */
    String getProvider();
}

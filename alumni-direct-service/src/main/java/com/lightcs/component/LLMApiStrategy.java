package com.lightcs.component;

public interface LLMApiStrategy {
    /**
     * 调用LLM API的核心方法
     * 子类需要实现具体的API调用逻辑
     *
     * @param prompt 提示词
     * @return LLM响应内容
     */
    String callApi(String prompt);

    /**
     * 获取当前使用的LLM提供商名称
     *
     * @return 提供商名称（如：GLM、OpenAI、Qwen等）
     */
    String getProvider();

    /**
     * 加载提示词模板
     * 子类可以指定使用哪个模板文件
     *
     * @return 模板文件名
     */
    String loadPromptTemplate();
}

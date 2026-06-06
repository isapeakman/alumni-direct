package com.lightcs.provider;

import com.lightcs.component.LLMApiStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 简历解析模板服务
 */
@Service
@Slf4j
public class ResumeParseTemplateService {
    private final PromptTemplateService promptTemplateService;

    public ResumeParseTemplateService(PromptTemplateService promptTemplateService) {
        this.promptTemplateService = promptTemplateService;
    }

    /**
     * 模板方法：解析简历文本为结构化JSON
     * 这是一个完整的业务流程
     *
     * @param resumeText 简历文本内容
     * @return 结构化的JSON字符串
     */
    public final String parseResumeToJson(String resumeText, LLMApiStrategy strategy) {
        log.info("[{}] 开始解析简历", strategy.getProvider());
        // 1.获取提示词文件路径
        String template = strategy.loadPromptTemplate();
        // 2.加载提示词
        String prompt = promptTemplateService.getResumeParsePrompt(template, resumeText);
        // 3：根据提示词调用LLM API
        String jsonResult = strategy.callApi(prompt);

        log.info("[{}] 简历解析完成", strategy.getProvider());
        return jsonResult;
    }
}

package com.lightcs.provider;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 提示词服务类
 */
@Service
public class PromptTemplateService {

    private static final String PROMPT_BASE_PATH = "prompts/";
    private static final String PROMPT_INTERVIEW = PROMPT_BASE_PATH + "resume-prompt.st";

//    // 使用 @Value 注解加载资源文件
//    @Value("classpath:/prompts/resume-prompt.st")
//    private Resource interviewPromptResource;


    /**
     * 读取提示词模板文件
     */
    public String loadPrompt(String relativePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(PROMPT_BASE_PATH + relativePath);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * 填充模板变量
     *
     * @param template  模板内容
     * @param variables 变量映射表
     * @return 填充后的提示词
     */
    public String fillTemplate(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    /**
     * 获取带岗位信息的面试提示词
     */
    public String getJobSpecificPrompt(String jobTitle) throws IOException {
        String template = loadPrompt(PROMPT_INTERVIEW);
        Map<String, String> variables = new HashMap<>();
        variables.put("job_title", jobTitle);
        return fillTemplate(template, variables);
    }
}

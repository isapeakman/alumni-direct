package com.lightcs.provider;

import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PromptTemplateService {
    /**
     * 提示词文件夹路径
     */
    private static final String PROMPT_BASE_PATH = "prompts/";
    private static final String PROMPT_INTERVIEW = "resume-prompt.st";


    /**
     * 读取提示词模板文件
     */
    public String loadPrompt(String relativePath) {
        ClassPathResource resource = new ClassPathResource(PROMPT_BASE_PATH + relativePath);
        try {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("加载提示词模板失败: {}", relativePath);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "加载提示词模板失败: " + relativePath);
        }
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
    public String getJobSpecificPrompt(String jobTitle, String resumeText) throws IOException {
        String template = loadPrompt(PROMPT_INTERVIEW);
        Map<String, String> variables = new HashMap<>();
        variables.put("job_title", jobTitle);
        variables.put("resume_info", resumeText);
        return fillTemplate(template, variables);
    }

    /**
     * 获取简历解析提示词
     *
     * @param promptFilePath 提示词文件路径
     * @param ocrText        OCR识别文本
     * @return String 提示词文本
     * @throws IOException
     */
    public String getResumeParsePrompt(String promptFilePath, String ocrText) {
        String template = loadPrompt(promptFilePath);
        Map<String, String> variables = new HashMap<>();
        variables.put("resume_text", ocrText);
        return fillTemplate(template, variables);
    }

    /**
     * 获取简历生成提示词
     *
     * @param jobPosition 求职岗位
     * @param resumeInfo  简历信息（JSON格式）
     * @return 填充后的提示词
     */
    public String getResumeGeneratePrompt(String jobPosition, String resumeInfo) {
        String template = loadPrompt("resume-generate.txt");
        Map<String, String> variables = new HashMap<>();
        variables.put("job_position", jobPosition);
        variables.put("resume_info", resumeInfo);
        return fillTemplate(template, variables);
    }
}

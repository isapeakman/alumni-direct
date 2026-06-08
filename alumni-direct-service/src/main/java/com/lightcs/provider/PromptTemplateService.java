package com.lightcs.provider;

import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提示词服务类
 * 系统提示词加载到内存中，个人信息动态追加
 */
@Service
@Slf4j
public class PromptTemplateService {
    /**
     * 提示词文件夹路径
     */
    private static final String PROMPT_BASE_PATH = "prompts/";
    private static final String PROMPT_INTERVIEW_SYSTEM = "interview-system.txt";
    private static final String PROMPT_INTERVIEW_QUESTION = "interview-question.txt";
    private static final String PROMPT_INTERVIEW_SUMMARY = "interview-summary.txt";
    private static final String PROMPT_INTERVIEW_SUMMARY_CONTENT = "interview-summary-content.txt";
    private static final String PROMPT_RESUME_PARSE = "resume-parse.txt";

    /**
     * 内存缓存 - 存储已加载的提示词模板
     */
    private final ConcurrentHashMap<String, String> promptCache = new ConcurrentHashMap<>();

    /**
     * 面试系统提示词（单独存放，不随用户输入变化）
     */
    private String interviewSystemPrompt;

    /**
     * 服务启动时预加载所有提示词模板到内存
     */
    @PostConstruct
    public void init() {
        log.info("开始预加载提示词模板到内存...");
        try {
            preloadPrompt(PROMPT_INTERVIEW_SYSTEM);
            preloadPrompt(PROMPT_INTERVIEW_QUESTION);
            preloadPrompt(PROMPT_INTERVIEW_SUMMARY);
            preloadPrompt(PROMPT_RESUME_PARSE);

            // 预加载面试系统提示词（单独的系统提示词文件）
            interviewSystemPrompt = loadPromptFromCache(PROMPT_INTERVIEW_SYSTEM);

            log.info("提示词模板预加载完成，共 {} 个模板", promptCache.size());
        } catch (Exception e) {
            log.error("预加载提示词模板失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "初始化提示词服务失败");
        }
    }

    /**
     * 预加载单个提示词模板
     */
    private void preloadPrompt(String relativePath) {
        String content = loadPrompt(relativePath);
        promptCache.put(relativePath, content);
        log.debug("已预加载提示词模板: {}", relativePath);
    }

    /**
     * 加载简历解析提示词
     */
    public String loadResumeParseSystemPrompt() {
        return loadPromptFromCache(PROMPT_RESUME_PARSE);
    }

    /**
     * 加载面试系统提示词
     */
    public String loadInterviewSystemPrompt() {
        return loadPromptFromCache(PROMPT_INTERVIEW_SYSTEM);
    }

    /**
     * 加载面试评估提示词
     */
    public String loadInterviewEvaluationPrompt() {
        return loadPromptFromCache(PROMPT_INTERVIEW_SUMMARY);
    }

    /**
     * 从缓存读取提示词模板，如果缓存中不存在则从文件加载
     */
    public String loadPromptFromCache(String relativePath) {
        return promptCache.computeIfAbsent(relativePath, this::loadPrompt);
    }

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
     * 系统提示词已在内存中，动态追加岗位和简历信息
     */
    public String getJobSpecificPrompt(String jobTitle, String resumeText) {
        Map<String, String> variables = new HashMap<>();
        variables.put("job_title", jobTitle);
        variables.put("resume_info", resumeText);
        return fillTemplate(interviewSystemPrompt, variables);
    }

    /**
     * 获取简历解析提示词
     *
     * @param promptFilePath 提示词文件路径
     * @param ocrText        OCR识别文本
     * @return String 提示词文本
     */
    public String getResumeParsePrompt(String promptFilePath, String ocrText) {
        String template = loadPromptFromCache(promptFilePath);
        Map<String, String> variables = new HashMap<>();
        variables.put("resume_text", ocrText);
        return fillTemplate(template, variables);
    }

    /**
     * 获取面试系统提示词
     * 系统提示词在服务启动时预加载到内存，不随用户输入变化
     *
     * @return 系统提示词内容
     */
    public String getInterviewSystemPrompt() {
        return interviewSystemPrompt;
    }

    /**
     * 获取面试用户提示词
     * 包含简历信息、对话历史和用户回答，作为用户输入部分发送给AI
     *
     * @param resumeContent 简历内容（JSON格式）
     * @param history       对话历史
     * @param userAnswer    用户当前回答
     * @return 填充后的用户提示词
     */
    public String getInterviewUserPrompt(String resumeContent, String history, String userAnswer) {
        String template = loadPromptFromCache(PROMPT_INTERVIEW_QUESTION);
        Map<String, String> variables = new HashMap<>();
        variables.put("resume_content", resumeContent);
        variables.put("conversation_history", history);
        variables.put("user_answer", userAnswer);
        return fillTemplate(template, variables);
    }

    /**
     * 兼容旧接口 - 获取面试问题提示词（已废弃，使用 getInterviewSystemPrompt + getInterviewUserPrompt 替代）
     */
    @Deprecated
    public String getInterviewPrompt(String resumeContent, String history, String userAnswer) {
        return getInterviewUserPrompt(resumeContent, history, userAnswer);
    }

    /**
     * 获取面试总结提示词
     *
     * @param resumeContent 简历内容（JSON格式）
     * @param history       对话历史
     * @return 填充后的提示词
     */
    public String getInterviewSummaryPrompt(String resumeContent, String history) {
        String template = loadPromptFromCache(PROMPT_INTERVIEW_SUMMARY_CONTENT);
        Map<String, String> variables = new HashMap<>();
        variables.put("resume_content", resumeContent);
        variables.put("conversation_history", history);
        return fillTemplate(template, variables);
    }

    /**
     * 获取缓存中的提示词模板数量
     */
    public int getCachedPromptCount() {
        return promptCache.size();
    }

    /**
     * 清除缓存（用于测试或重新加载）
     */
    public void clearCache() {
        promptCache.clear();
        log.info("提示词缓存已清空");
    }
}

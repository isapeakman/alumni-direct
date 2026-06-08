package com.lightcs.service.impl;

import com.lightcs.component.LLMApiStrategy;
import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.BusinessException;
import com.lightcs.mapper.InterviewMessageMapper;
import com.lightcs.mapper.InterviewSessionMapper;
import com.lightcs.model.dto.InterviewMessageDTO;
import com.lightcs.model.dto.InterviewSessionDTO;
import com.lightcs.model.pojo.InterviewMessage;
import com.lightcs.model.pojo.InterviewSession;
import com.lightcs.provider.PromptTemplateService;
import com.lightcs.service.InterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 面试服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewSessionMapper sessionMapper;
    private final InterviewMessageMapper messageMapper;
    private final LLMApiStrategy llmApiStrategy;
    private final PromptTemplateService promptTemplateService;

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_ENDED = "ENDED";
    private static final String ROLE_USER = "user";
    private static final String ROLE_ASSISTANT = "assistant";

    @Override
    @Transactional
    public InterviewSessionDTO startInterview(String resumeId, String resumeContent) {
        log.info("开始新面试，简历ID: {}", resumeId != null ? resumeId : "未提供");

        // 创建会话
        InterviewSession session = new InterviewSession();
        session.setResumeId(resumeId);
        session.setResumeContent(resumeContent);
        session.setStatus(STATUS_ACTIVE);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        sessionMapper.insert(session);

        // 生成第一个问题
        String firstQuestion = generateFirstQuestion(session.getId(), resumeContent);
        session.setCurrentQuestion(firstQuestion);
        sessionMapper.updateById(session);

        // 保存第一条消息（AI的第一个问题）
        saveMessage(session.getId(), ROLE_ASSISTANT, firstQuestion);

        return buildSessionDTO(session, List.of(InterviewMessageDTO.builder()
                .role(ROLE_ASSISTANT)
                .content(firstQuestion)
                .sequence(1)
                .createdAt(LocalDateTime.now())
                .build()));
    }

    @Override
    @Transactional
    public InterviewMessageDTO sendMessage(String sessionId, String userAnswer) {
        log.info("发送消息，会话ID: {}", sessionId);

        // 验证会话
        InterviewSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在");
        }
        if (!STATUS_ACTIVE.equals(session.getStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "会话已结束");
        }

        // 保存用户消息
        int userSeq = saveMessage(sessionId, ROLE_USER, userAnswer);

        // 生成AI回复
        String aiReply = generateReply(sessionId, session.getResumeContent(), userAnswer);
        saveMessage(sessionId, ROLE_ASSISTANT, aiReply);

        // 更新会话
        session.setCurrentQuestion(aiReply);
        session.setUpdatedAt(LocalDateTime.now());
        sessionMapper.updateById(session);

        return InterviewMessageDTO.builder()
                .role(ROLE_ASSISTANT)
                .content(aiReply)
                .sequence(userSeq + 1)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    public InterviewSessionDTO getSession(String sessionId) {
        InterviewSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在");
        }

        List<InterviewMessage> messages = messageMapper.selectBySessionId(sessionId);
        List<InterviewMessageDTO> messageDTOs = messages.stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());

        return buildSessionDTO(session, messageDTOs);
    }

    @Override
    @Transactional
    public String endInterview(String sessionId) {
        log.info("结束面试，会话ID: {}", sessionId);

        InterviewSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在");
        }

        // 获取所有消息
        List<InterviewMessage> messages = messageMapper.selectBySessionId(sessionId);
        String history = buildHistory(messages);

        // 生成总结
        String summary = generateSummary(session.getResumeContent(), history);

        // 计算会话时长
        LocalDateTime endedAt = LocalDateTime.now();
        long duration = java.time.Duration.between(session.getCreatedAt(), endedAt).getSeconds();

        // 更新会话状态
        session.setStatus(STATUS_ENDED);
        session.setDuration((int) duration);
        session.setEndedAt(endedAt);
        session.setUpdatedAt(endedAt);
        sessionMapper.updateById(session);

        return summary;
    }

    /**
     * 生成第一个问题
     */
    private String generateFirstQuestion(String sessionId, String resumeContent) {
        // 获取系统提示词（角色定义、职责、约束等）
        String systemPrompt = promptTemplateService.getInterviewSystemPrompt();
        // 获取用户提示词（简历信息、对话历史、用户回答等）
        String userPrompt = promptTemplateService.getInterviewUserPrompt(resumeContent, "", "");
        // 调用AI，系统提示词和用户提示词分开传入
        return llmApiStrategy.interviewWithSystemPrompt(systemPrompt, userPrompt);
    }

    /**
     * 生成AI回复
     */
    private String generateReply(String sessionId, String resumeContent, String userAnswer) {
        List<InterviewMessage> messages = messageMapper.selectBySessionId(sessionId);
        String history = buildHistory(messages);

        // 获取系统提示词（角色定义、职责、约束等）
        String systemPrompt = promptTemplateService.getInterviewSystemPrompt();
        // 获取用户提示词（简历信息、对话历史、用户回答等）
        String userPrompt = promptTemplateService.getInterviewUserPrompt(resumeContent, history, userAnswer);
        // 调用AI，系统提示词和用户提示词分开传入
        return llmApiStrategy.interviewWithSystemPrompt(systemPrompt, userPrompt);
    }

    /**
     * 生成面试总结
     */
    private String generateSummary(String resumeContent, String history) {
        String prompt = promptTemplateService.getInterviewSummaryPrompt(resumeContent, history);
        return llmApiStrategy.interview(prompt);
    }

    /**
     * 构建对话历史
     */
    private String buildHistory(List<InterviewMessage> messages) {
        return messages.stream()
                .map(m -> m.getRole() + ": " + m.getContent())
                .collect(Collectors.joining("\n"));
    }

    /**
     * 保存消息
     */
    private int saveMessage(String sessionId, String role, String content) {
        Integer maxSeq = messageMapper.selectMaxSequence(sessionId);
        int sequence = maxSeq + 1;

        InterviewMessage message = new InterviewMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setSequence(sequence);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);

        return sequence;
    }

    /**
     * 构建会话DTO
     */
    private InterviewSessionDTO buildSessionDTO(InterviewSession session, List<InterviewMessageDTO> messages) {
        return InterviewSessionDTO.builder()
                .id(session.getId())
                .resumeId(session.getResumeId())
                .status(session.getStatus())
                .currentQuestion(session.getCurrentQuestion())
                .messages(messages)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    /**
     * 转换为消息DTO
     */
    private InterviewMessageDTO toMessageDTO(InterviewMessage message) {
        return InterviewMessageDTO.builder()
                .id(message.getId())
                .role(message.getRole())
                .content(message.getContent())
                .sequence(message.getSequence())
                .createdAt(message.getCreatedAt())
                .build();
    }
}

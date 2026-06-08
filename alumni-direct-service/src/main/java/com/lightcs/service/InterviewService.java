package com.lightcs.service;

import com.lightcs.model.dto.InterviewMessageDTO;
import com.lightcs.model.dto.InterviewSessionDTO;

/**
 * 面试服务接口
 */
public interface InterviewService {

    /**
     * 开始新面试
     *
     * @param resumeId      简历ID（可选）
     * @param resumeContent 简历内容（JSON格式）
     * @return 面试会话信息（包含第一个问题）
     */
    InterviewSessionDTO startInterview(String resumeId, String resumeContent);

    /**
     * 发送消息（用户回答）
     *
     * @param sessionId  会话ID
     * @param userAnswer 用户回答
     * @return 面试消息（包含AI回复）
     */
    InterviewMessageDTO sendMessage(String sessionId, String userAnswer);

    /**
     * 获取会话详情
     *
     * @param sessionId 会话ID
     * @return 会话详情
     */
    InterviewSessionDTO getSession(String sessionId);

    /**
     * 结束面试
     *
     * @param sessionId 会话ID
     * @return 面试总结
     */
    String endInterview(String sessionId);
}

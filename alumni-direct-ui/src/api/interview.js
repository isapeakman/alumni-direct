import request from '@/utils/request'

// 面试相关接口超时时间（AI接口响应较慢，设置较长超时）
const INTERVIEW_TIMEOUT = 60000 // 60秒

/**
 * 开始新面试
 * @param {String} resumeContent - 简历内容（JSON格式）
 * @param {String} [resumeId] - 简历ID（可选）
 * @returns {Promise} 返回面试会话信息
 */
export function startInterview(resumeContent, resumeId) {
    return request({
        url: '/ad/interview/start',
        method: 'post',
        data: resumeContent,
        params: resumeId ? {resumeId} : undefined,
        headers: {
            'Content-Type': 'application/json'
        },
        timeout: INTERVIEW_TIMEOUT
    })
}

/**
 * 发送面试消息（用户回答）
 * @param {String} sessionId - 会话ID
 * @param {String} userAnswer - 用户回答
 * @returns {Promise} 返回AI回复
 */
export function sendInterviewMessage(sessionId, userAnswer) {
    return request({
        url: `/ad/interview/${sessionId}/message`,
        method: 'post',
        data: userAnswer,
        headers: {
            'Content-Type': 'application/json'
        },
        timeout: INTERVIEW_TIMEOUT
    })
}

/**
 * 获取面试会话详情
 * @param {String} sessionId - 会话ID
 * @returns {Promise} 返回会话详情
 */
export function getInterviewSession(sessionId) {
    return request({
        url: `/ad/interview/${sessionId}`,
        method: 'get',
        timeout: INTERVIEW_TIMEOUT
    })
}

/**
 * 结束面试
 * @param {String} sessionId - 会话ID
 * @returns {Promise} 返回面试总结
 */
export function endInterview(sessionId) {
    return request({
        url: `/ad/interview/${sessionId}/end`,
        method: 'post',
        timeout: INTERVIEW_TIMEOUT
    })
}

/**
 * 获取面试会话列表
 * @returns {Promise} 返回会话列表
 */
export function listSessions() {
    return request({
        url: '/ad/interview/list',
        method: 'get',
        timeout: INTERVIEW_TIMEOUT
    })
}
import request from '@/utils/request'

/**
 * 异步提交简历解析任务
 * @param {File} file - 简历文件
 * @returns {Promise} 返回任务ID
 */
export function submitResumeParseTask(file) {
    const formData = new FormData()
    formData.append('file', file)

    return request({
        url: '/ad/ai/resume/parse/async',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        timeout: 30000 // 设置30秒超时，避免文件上传时超时
    })
}

/**
 * 查询简历解析任务状态
 * @param {String} taskId - 任务ID
 * @returns {Promise} 返回任务状态
 */
export function getResumeParseTaskStatus(taskId) {
    return request({
        url: `/ad/ai/resume/parse/task/${taskId}`,
        method: 'get'
    })
}

/**
 * 同步简历解析（旧接口，保留兼容）
 * @param {File} file - 简历文件
 * @returns {Promise} 返回解析结果
 */
export function parseResumeSync(file) {
    const formData = new FormData()
    formData.append('file', file)

    return request({
        url: '/ad/ai/resume/parse',
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        timeout: 60000 // 设置60秒超时
    })
}

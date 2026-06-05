package com.lightcs.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 简历解析异步执行器
 * 专门负责异步执行简历解析任务
 */
public interface ResumeParseExecutor {

    /**
     * 异步执行简历解析任务
     *
     * @param taskId 任务ID
     * @param file   简历文件
     */
    void executeAsync(String taskId, MultipartFile file);
}

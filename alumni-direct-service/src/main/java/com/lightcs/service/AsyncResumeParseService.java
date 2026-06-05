package com.lightcs.service;

import com.lightcs.model.vo.AsyncTaskStatusVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 异步简历解析服务
 */
public interface AsyncResumeParseService {

    /**
     * 提交简历解析任务（异步）
     *
     * @param file 简历文件
     * @return 任务ID
     */
    String submitParseTask(MultipartFile file);

    /**
     * 查询任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态
     */
    AsyncTaskStatusVO getTaskStatus(String taskId);
}

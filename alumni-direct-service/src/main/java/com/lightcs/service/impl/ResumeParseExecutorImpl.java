package com.lightcs.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lightcs.model.vo.AsyncTaskStatusVO;
import com.lightcs.model.vo.ResumeDTO;
import com.lightcs.provider.ResumeParseService;
import com.lightcs.service.ResumeParseExecutor;
import com.lightcs.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

/**
 * 简历解析异步执行器实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeParseExecutorImpl implements ResumeParseExecutor {

    private final ResumeParseService resumeParseService;
    private final RedisUtil redisUtil;

    private static final String TASK_KEY_PREFIX = "resume:task:";

    /**
     * 异步执行简历解析任务
     * 此方法会被 Spring AOP 代理，确保真正的异步执行
     */
    @Override
    @Async("taskExecutor")
    public void executeAsync(String taskId, MultipartFile file) {
        log.info("🚀 [异步线程: {}] 开始执行简历解析任务: {}",
                Thread.currentThread().getName(), taskId);

        try {
            // 更新状态为处理中 - OCR阶段
            updateTaskStatus(taskId, "PROCESSING", 10, "正在识别文件内容（OCR）...", null, null);

            log.info("📄 [任务: {}] 开始OCR识别和GLM结构化...", taskId);

            // 执行简历解析（包含OCR和GLM调用）
            ResumeDTO result = resumeParseService.parseResume(file);

            log.info("✅ [任务: {}] 解析成功，姓名: {}", taskId, result.getName());

            // 更新状态为完成
            updateTaskStatus(taskId, "COMPLETED", 100, "解析完成", result, null);

            log.info("🎉 [任务: {}] 简历解析任务全部完成", taskId);

        } catch (Exception e) {
            log.error("❌ [任务: {}] 简历解析任务失败", taskId, e);

            // 更新状态为失败
            updateTaskStatus(taskId, "FAILED", 0, "解析失败", null, e.getMessage());
        }
    }

    /**
     * 更新任务状态到Redis
     */
    private void updateTaskStatus(String taskId, String status, Integer progress,
                                  String stage, Object result, String errorMessage) {
        AsyncTaskStatusVO taskStatus = AsyncTaskStatusVO.builder()
                .taskId(taskId)
                .status(status)
                .progress(progress)
                .stage(stage)
                .result(result)
                .errorMessage(errorMessage)
                .completeTime("COMPLETED".equals(status) || "FAILED".equals(status)
                        ? System.currentTimeMillis() : null)
                .build();

        String key = TASK_KEY_PREFIX + taskId;
        String taskJson = JSON.toJSONString(taskStatus);
        redisUtil.set(key, taskJson, 30, TimeUnit.MINUTES);

        log.debug("📊 [任务: {}] 状态更新: {} - {}% - {}", taskId, status, progress, stage);
    }
}

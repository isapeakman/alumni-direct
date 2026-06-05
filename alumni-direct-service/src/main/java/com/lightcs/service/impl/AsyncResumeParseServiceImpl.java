package com.lightcs.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lightcs.model.vo.AsyncTaskStatusVO;
import com.lightcs.service.AsyncResumeParseService;
import com.lightcs.service.ResumeParseExecutor;
import com.lightcs.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 异步简历解析服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncResumeParseServiceImpl implements AsyncResumeParseService {

    private final ResumeParseExecutor resumeParseExecutor;
    private final RedisUtil redisUtil;

    private static final String TASK_KEY_PREFIX = "resume:task:";
    private static final long TASK_EXPIRE_TIME = 30; // 任务保留30分钟

    @Override
    public String submitParseTask(MultipartFile file) {
        // 生成任务ID
        String taskId = UUID.randomUUID().toString().replace("-", "");

        // 创建初始任务状态
        AsyncTaskStatusVO taskStatus = AsyncTaskStatusVO.builder()
                .taskId(taskId)
                .status("PENDING")
                .progress(0)
                .stage("等待处理")
                .createTime(System.currentTimeMillis())
                .build();

        // 保存任务状态到Redis
        String key = TASK_KEY_PREFIX + taskId;
        String taskJson = JSON.toJSONString(taskStatus);
        redisUtil.set(key, taskJson, TASK_EXPIRE_TIME, TimeUnit.MINUTES);

        log.info("提交简历解析任务: {}", taskId);

        // 通过独立的Executor服务异步执行解析任务（确保走Spring AOP代理）
        resumeParseExecutor.executeAsync(taskId, file);

        return taskId;
    }

    @Override
    public AsyncTaskStatusVO getTaskStatus(String taskId) {
        String key = TASK_KEY_PREFIX + taskId;
        String taskJson = (String) redisUtil.get(key);

        if (taskJson == null) {
            log.warn("任务不存在或已过期: {}", taskId);
            return AsyncTaskStatusVO.builder()
                    .taskId(taskId)
                    .status("FAILED")
                    .errorMessage("任务不存在或已过期")
                    .build();
        }

        return JSON.parseObject(taskJson, AsyncTaskStatusVO.class);
    }
}

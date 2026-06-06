package com.lightcs.constants;/**
 * @Author: peak-like
 * @CreateTime: 2025-01-07
 * @Description: Redis常量
 * @Version: 1.0
 */

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-07
 * @Description: Redis常量
 * @Version: 1.0
 */
public interface RedisConstant {
    String TOKEN_PREFIX = "user:token:";
    String TASK_KEY_PREFIX = "resume:parse:task:";
    long TASK_EXPIRE_TIME = 30; // 任务保留30分钟
}

package com.lightcs.service;

import com.lightcs.model.pojo.ApiCallLog;
import com.lightcs.model.pojo.GlmTokenLog;

/**
 * API调用日志服务接口
 */
public interface ApiCallLogService {

    /**
     * 保存API调用日志
     *
     * @param log 日志实体
     * @return 日志ID
     */
    Long saveApiCallLog(ApiCallLog log);

    /**
     * 保存GLM Token日志
     *
     * @param log 日志实体
     */
    void saveGlmTokenLog(GlmTokenLog log);
}
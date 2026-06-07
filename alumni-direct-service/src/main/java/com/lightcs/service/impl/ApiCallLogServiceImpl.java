package com.lightcs.service.impl;

import com.lightcs.mapper.ApiCallLogMapper;
import com.lightcs.mapper.GlmTokenLogMapper;
import com.lightcs.model.pojo.ApiCallLog;
import com.lightcs.model.pojo.GlmTokenLog;
import com.lightcs.service.ApiCallLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * API调用日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiCallLogServiceImpl implements ApiCallLogService {

    private final ApiCallLogMapper apiCallLogMapper;
    private final GlmTokenLogMapper glmTokenLogMapper;

    @Override
    public Long saveApiCallLog(ApiCallLog apiCallLog) {
        try {
            apiCallLog.setCreatedAt(LocalDateTime.now());
            apiCallLogMapper.insert(apiCallLog);
            return apiCallLog.getId();
        } catch (Exception e) {
            log.error("保存API调用日志失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void saveGlmTokenLog(GlmTokenLog glmTokenLog) {
        try {
            glmTokenLog.setCreatedAt(LocalDateTime.now());
            glmTokenLogMapper.insert(glmTokenLog);
        } catch (Exception e) {
            log.error("保存GLM Token日志失败: {}", e.getMessage());
        }
    }
}
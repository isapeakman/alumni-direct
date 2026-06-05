package com.lightcs.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 异步任务状态VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskStatusVO {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务状态：PENDING-等待中, PROCESSING-处理中, COMPLETED-已完成, FAILED-失败
     */
    private String status;

    /**
     * 进度百分比（0-100）
     */
    private Integer progress;

    /**
     * 当前阶段描述
     */
    private String stage;

    /**
     * 结果数据（完成时才有）
     */
    private Object result;

    /**
     * 错误信息（失败时才有）
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 完成时间
     */
    private Long completeTime;
}

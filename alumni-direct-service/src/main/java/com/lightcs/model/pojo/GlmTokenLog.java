package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * GLM Token消耗日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("glm_token_log")
public class GlmTokenLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的API调用日志ID
     */
    private Long apiCallLogId;

    /**
     * 输入token数
     */
    private Integer promptTokens;

    /**
     * 输出token数
     */
    private Integer completionTokens;

    /**
     * 总token数
     */
    private Integer totalTokens;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
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
 * API调用耗时日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("api_call_log")
public class ApiCallLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 耗时（毫秒）
     */
    private Long duration;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应长度
     */
    private Integer responseLength;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
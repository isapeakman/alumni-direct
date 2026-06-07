package com.lightcs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API调用结果封装类
 * 用于封装API调用的响应内容和Token消耗信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallResult {

    /**
     * 响应内容
     */
    private String content;

    /**
     * 输入Token数
     */
    private Integer promptTokens;

    /**
     * 输出Token数
     */
    private Integer completionTokens;

    /**
     * 总Token数
     */
    private Integer totalTokens;

    /**
     * 使用的模型名称
     */
    private String model;
}
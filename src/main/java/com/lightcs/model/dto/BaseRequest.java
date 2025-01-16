package com.lightcs.model.dto;

import lombok.Data;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */
@Data
public class BaseRequest {
    private Integer current = 1;
    private Integer pageSize = 10;
    private String orderBy;
    private String orderType;
}

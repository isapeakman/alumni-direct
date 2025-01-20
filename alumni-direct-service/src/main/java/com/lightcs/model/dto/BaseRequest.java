package com.lightcs.model.dto;

import lombok.Data;

import static com.lightcs.constants.Common.DEFAULT_ORDER_BY;
import static com.lightcs.constants.Common.DEFAULT_ORDER_TYPE;

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
    private String orderBy = DEFAULT_ORDER_BY;      // 排序字段
    private String orderType = DEFAULT_ORDER_TYPE;  // 排序方式
}

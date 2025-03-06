package com.lightcs.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-06
 * @Description:
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobRequest extends BaseRequest {
    private Integer status;
}

package com.lightcs.model.dto.job;

import com.lightcs.model.dto.BaseRequest;
import lombok.Data;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-25
 * @Description:
 * @Version: 1.0
 */
@Data
public class JobApplyRecordCardRequest extends BaseRequest {
    Integer jobId;
    Integer status;
}

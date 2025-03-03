package com.lightcs.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-26
 * @Description:
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobApplyRecordVO extends JobCardVO {
    /**
     * 职位申请状态
     */
    private Integer status;
    private String note;
    private Date apply_time;
    private String resume;
}

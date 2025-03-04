package com.lightcs.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobCardRequest extends BaseRequest {
    /**
     * 职位名称
     */
    private String title;

    /**
     * 职位类型: 0全职、1实习、2兼职
     */
    private Integer jobType;
    /**
     * 工作地点
     */
    private String location;
    /**
     * 最低薪资
     */
    private Integer minSalary;
    /**
     * 最高薪资
     */
    private Integer maxSalary;
    /**
     * 分类id
     */
    private Integer categoryId;
}

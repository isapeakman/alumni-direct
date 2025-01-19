package com.lightcs.model.dto.job;

import lombok.Data;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */
@Data
public class JobAdd {
    /**
     * 职位名称
     */
    private String title;

    /**
     * 职位类型: 0全职、1实习、2兼职
     */
    private Integer jobType;

    /**
     * 职位描述
     */
    private String jobDesc;

    /**
     * 工作地点
     */
    private String location;

    /**
     * 最小薪资
     */
    private Integer minSalary;
    /**
     * 最大薪资
     */
    private Integer maxSalary;
    /**
     * 企业名称
     */
    private String companyName;
}

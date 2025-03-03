package com.lightcs.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JobCardVO {
    private Integer id;
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
     * 最小薪资
     */
    private Integer minSalary;
    /**
     * 最大薪资
     */
    private Integer maxSalary;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 招聘者id
     */
    private Integer createId;
    /**
     * 招聘者名称
     */
    private String recruiterName;
    /**
     * 招聘者头像
     */
    private String recruiterAvatar;
}

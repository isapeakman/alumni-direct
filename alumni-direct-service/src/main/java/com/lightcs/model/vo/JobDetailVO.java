package com.lightcs.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-05
 * @Description: 职位详情VO
 * @Version: 1.0
 */
@Data
public class JobDetailVO {
    /**
     * 职位id
     */
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

    /**
     * 创建用户
     */
    private Integer createId;
    /**
     * 招聘者
     */
    private String recruiterName;
    /**
     * 招聘者头像
     */
    private String recruiterAvatar;
    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;
}

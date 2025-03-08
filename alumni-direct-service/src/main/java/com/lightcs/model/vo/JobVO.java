package com.lightcs.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-17
 * @Description: 职位VO
 * @Version: 1.0
 */
@Data
public class JobVO {
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
     * 状态  0 待审批 1待发布 2 已发布 3 关闭 4审核未通过
     */
    private Integer status;
    /**
     * 发布时间
     */
    private Date publishTime;
    /**
     * 分类ids
     */
    private List<Integer> categoryIds;
}

package com.lightcs.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-23
 * @Description:
 * @Version: 1.0
 */
@Data
public class UserIntentionRequest {
    private Integer id;
    /**
     * 职位类型 0 全职 1 实习 2 兼职
     */
    private Integer type;

    /**
     * 职位分类ID
     */
    private List<Integer> categoryId;

    /**
     * 最低薪资,单位k
     */
    private Integer minSalary;

    /**
     * 最高薪资,单位K
     */
    private Integer maxSalary;

}

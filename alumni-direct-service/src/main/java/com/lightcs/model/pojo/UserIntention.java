package com.lightcs.model.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户求职意愿表
 *
 * @TableName user_intention
 */
@TableName(value = "user_intention")
@Data
public class UserIntention implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 职位类型 0 全职 1 实习 2 兼职
     */
    private Integer type;

    /**
     * 职位分类ID  用逗号分割
     */
    private String categoryId;

    /**
     * 最低薪资,单位k
     */
    private Integer minSalary;

    /**
     * 最高薪资,单位K
     */
    private Integer maxSalary;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
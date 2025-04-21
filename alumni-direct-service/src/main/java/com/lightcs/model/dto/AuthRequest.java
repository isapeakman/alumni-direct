package com.lightcs.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-21
 * @Description:
 * @Version: 1.0
 */
@Data
public class AuthRequest {
    /**
     * 认证材料
     */
    private String certification;

    /**
     * 认证材料2
     */
    private String certification2;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 入学年份
     */
    private Date yearAdmission;

    /**
     * 毕业年份
     */
    private Date yearGraduated;

    /**
     * 专业
     */
    private String major;

    /**
     * 手机号
     */
    private String phone;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

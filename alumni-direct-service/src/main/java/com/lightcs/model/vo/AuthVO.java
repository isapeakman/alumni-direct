package com.lightcs.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-21
 * @Description:
 * @Version: 1.0
 */
@Data
public class AuthVO {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 认证材料
     */
    private String certification;

    /**
     * 认证材料2
     */
    private String certification2;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 审批状态:0未审批 1 已审批
     */
    private Integer approvalStatus;

    /**
     * 审批结果 0 未通过 1 通过
     */
    private Integer approvalResult;

    /**
     * 审批意见
     */
    private String note;

    /**
     * 审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approvalTime;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 入学时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date yearAdmission;

    /**
     * 毕业时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
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

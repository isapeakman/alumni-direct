package com.lightcs.model.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户校友认证审批表
 *
 * @TableName user_certification_approval
 */
@TableName(value = "user_certification_approval")
@Data
public class UserCertificationApproval implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Integer id;

    /**
     * 申请人ID
     */
    private Integer userId;

    /**
     * 认证材料
     */
    private String certification;

    /**
     *
     */
    private String certification2;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 审核人ID
     */
    private Integer approvalUserId;

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
    private Date approvalTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 学号
     */
    private String studentId;

    /**
     *
     */
    private Date yearAdmission;

    /**
     * 毕业年份
     */
    private Date yearGraduated;

    /**
     *
     */
    private String major;

    /**
     *
     */
    private String phone;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
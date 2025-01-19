package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 职位审批表
 *
 * @TableName job_approval_record
 */
@TableName(value = "job_approval_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApprovalRecord implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 职位id
     */
    private Integer jobId;

    /**
     * 审批人id
     */
    private Integer approvalUserId;

    /**
     * 描述信息
     */
    private String note;

    /**
     * 审批状态 0 未审批 1已审批
     */
    private Integer approvalStatus;

    /**
     * 审批结果 0 未通过 1通过
     */
    private Integer approvalResult;

    /**
     * 审批时间
     */
    private Date approvalTime;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
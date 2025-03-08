package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 职位审批表
 * @TableName job_approval_record
 */
@TableName(value = "job_approval_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobApprovalRecord implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * ****职位id
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approvalTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 职位名称(冗余)
     */
    private String title;

    /**
     * 职位类型(冗余)
     */
    private Integer jobType;

    /**
     * 职位描述(冗余)
     */
    private String jobDesc;

    /**
     * 地点(冗余)
     */
    private String location;

    /**
     * 薪资范围(冗余)
     */
    private String salaryRange;

    /**
     * 公司名(冗余)
     */
    private String companyName;

    /**
     * 发起审批人id(冗余)
     */
    private Integer createId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
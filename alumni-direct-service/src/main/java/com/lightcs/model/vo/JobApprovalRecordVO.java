package com.lightcs.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class JobApprovalRecordVO {
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
}

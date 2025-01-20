package com.lightcs.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JobApprovalRecordRequest extends BaseRequest {
    /**
     * 审批状态 0 未审批 1已审批
     */
    private Integer approvalStatus;

    /**
     * 审批结果 0 未通过 1通过
     */
    private Integer approvalResult;

}

package com.lightcs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.JobApprovalRecordRequest;
import com.lightcs.model.vo.JobApprovalRecordVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.PaginationBuilder;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.JobApprovalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.lightcs.constants.Common.UPDATE_SUCCESS;
import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;

@Tag(name = "职位审批记录")
@RestController
@RequestMapping("/jobApprovalRecord")
public class JobApprovalRecordController {

    @Autowired
    private JobApprovalRecordService jobApprovalRecordService;
    @Operation(summary = "获取所有职位审批记录")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public BaseResponse<Map<String, Object>> getAllJobApprovalRecords(JobApprovalRecordRequest recordRequest) {
        Page<JobApprovalRecordVO> records = jobApprovalRecordService.getAllApprovalRecord(recordRequest);
        return PaginationBuilder.build(records);
    }
    @Operation(summary = "审批职位记录")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/judge")
    public BaseResponse<String> judgeJobApprovalRecord(Integer recordId,String note,Integer approvalResult) {
        ThrowUtils.throwIf(recordId == null, PARAMS_ERROR, "审批记录id不能为空");
        ThrowUtils.throwIf(approvalResult == null, PARAMS_ERROR, "审批结果不能为空");
        ThrowUtils.throwIf(approvalResult != 0 && approvalResult != 1, PARAMS_ERROR, "审批结果只能为0或1");
        jobApprovalRecordService.updateRecordById(recordId, note, approvalResult);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }
}
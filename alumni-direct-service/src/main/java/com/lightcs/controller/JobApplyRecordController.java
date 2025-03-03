package com.lightcs.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightcs.model.dto.job.JobApplyRecordCardRequest;
import com.lightcs.model.vo.JobApplyRecordCardVO;
import com.lightcs.model.vo.JobApplyRecordVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.PaginationBuilder;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.JobApplyRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.lightcs.constants.Common.UPDATE_SUCCESS;

@Tag(name = "职位申请记录")
@RestController
@RequestMapping("/jobApplyRecord")
public class JobApplyRecordController {

    @Autowired
    private JobApplyRecordService jobApplyRecordService;


    //<editor-fold desc="招聘者">
    @Operation(summary = "获取用户申请卡片")
    @GetMapping("/cards")
    public BaseResponse<Map<String, Object>> getJobApplyRecordCards(JobApplyRecordCardRequest cardRequest) {
        Page<JobApplyRecordCardVO> jobApplyRecordCards = jobApplyRecordService.getJobApplyRecordCards(cardRequest);
        return PaginationBuilder.build(jobApplyRecordCards);
    }

    /**
     * 接收或拒绝申请卡片
     *
     * @param recordId 记录id
     * @param status   修改为的状态
     * @param note     备注
     * @return
     */
    @Operation(summary = "接收或拒绝申请卡片")
    @GetMapping("/judge")
    public BaseResponse<String> judgeJobApplyRecord(Integer recordId, Integer status, String note) {
        jobApplyRecordService.judgeJobApplyRecord(recordId, status, note);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    //</editor-fold>
    @Operation(summary = "获取当前用户职位申请记录")
    @GetMapping("/records")
    public BaseResponse<Map<String, Object>> getJobApplyRecords(Integer status, Integer current, Integer pageSize) {
        Page<JobApplyRecordVO> jobApplyRecords = jobApplyRecordService.getJobApplyRecords(status, current, pageSize);
        return PaginationBuilder.build(jobApplyRecords);
    }

}

package com.lightcs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.JobCardRequest;
import com.lightcs.model.dto.job.JobAdd;
import com.lightcs.model.dto.job.JobUpdate;
import com.lightcs.model.vo.JobCardVO;
import com.lightcs.model.vo.JobDetailVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.PaginationBuilder;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.lightcs.constants.Common.*;
import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description: 职位控制器
 * @Version: 1.0
 */
@Tag(name = "职位操作")
@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @Operation(summary = "添加职位")
    @PostMapping("/add")
    public BaseResponse<String> add(JobAdd jobAdd) {
        ThrowUtils.throwIf(jobAdd.getTitle().isBlank(), PARAMS_ERROR, "职位名称不能为空");
        ThrowUtils.throwIf(jobAdd.getJobType() == null, PARAMS_ERROR, "职位类型不能为空");
        ThrowUtils.throwIf(jobAdd.getMinSalary() == null || jobAdd.getMaxSalary() == null, PARAMS_ERROR, "薪资范围不能为空");
        ThrowUtils.throwIf(jobAdd.getMinSalary() > jobAdd.getMaxSalary(), PARAMS_ERROR, "最小薪资不能大于最大薪资");

        jobService.save(jobAdd);
        return ResultBuilder.success(ADD_SUCCESS);
    }

    @Operation(summary = "更新职位")
    @PutMapping("/update")
    public BaseResponse<String> update(JobUpdate jobUpdate) {
        ThrowUtils.throwIf(jobUpdate.getId() == null, PARAMS_ERROR, "职位id不能为空");
        ThrowUtils.throwIf(jobUpdate.getTitle().isBlank(), PARAMS_ERROR, "职位名称不能为空");
        ThrowUtils.throwIf(jobUpdate.getJobType() == null, PARAMS_ERROR, "职位类型不能为空");
        ThrowUtils.throwIf(jobUpdate.getMinSalary() == null || jobUpdate.getMaxSalary() == null, PARAMS_ERROR, "薪资范围不能为空");
        ThrowUtils.throwIf(jobUpdate.getMinSalary() > jobUpdate.getMaxSalary(), PARAMS_ERROR, "最小薪资不能大于最大薪资");

        jobService.update(jobUpdate);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    @Operation(summary = "发布职位")
    @GetMapping("/publish")
    public BaseResponse<String> publish(Integer jobId) {
        ThrowUtils.throwIf(jobId == null, PARAMS_ERROR, "职位id不能为空");
        jobService.publish(jobId);
        return ResultBuilder.success(PUBLISH_SUCCESS);
    }

    @Operation(summary = "下线发布职位")
    @GetMapping("/offline")
    public BaseResponse<String> offline(Integer jobId) {
        ThrowUtils.throwIf(jobId == null, PARAMS_ERROR, "职位id不能为空");
        jobService.offline(jobId);
        return ResultBuilder.success(OFFLINE_SUCCESS);
    }

    @Operation(summary = "关闭职位")
    @GetMapping("/close")
    public BaseResponse<String> close(Integer jobId) {
        ThrowUtils.throwIf(jobId == null, PARAMS_ERROR, "职位id不能为空");
        jobService.close(jobId);
        return ResultBuilder.success(CLOSE_SUCCESS);
    }

    //region 用户

    @Operation(summary = "获取职位卡片")
    @GetMapping("/cards")
    public BaseResponse<Map<String, Object>> getCardsConditional(JobCardRequest jobCardRequest) {

        if (jobCardRequest.getMinSalary() != null && jobCardRequest.getMaxSalary() != null) {
            ThrowUtils.throwIf(jobCardRequest.getMinSalary() > jobCardRequest.getMaxSalary(), PARAMS_ERROR, "最小薪资不能大于最大薪资");
        }

        Page<JobCardVO> data = jobService.selectCards(jobCardRequest);
        return PaginationBuilder.build(data);
    }

    @Operation(summary = "获取职位详情")
    @GetMapping("/detail")
    public BaseResponse<JobDetailVO> detail(@RequestParam(value = "jobId") Integer id) {
        ThrowUtils.throwIf(id == null, PARAMS_ERROR, "职位id不能为空");
        return ResultBuilder.success(jobService.detail(id));
    }
    //endregion

}

package com.lightcs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.JobCardRequest;
import com.lightcs.model.dto.job.JobAdd;
import com.lightcs.model.vo.JobCardVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.PaginationBuilder;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.lightcs.constants.Common.ADD_SUCCESS;
import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */

@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @RequestMapping("/list")
    public String list() {

        return "job list";
    }

    @PostMapping("/add")
    public BaseResponse<String> add(JobAdd jobAdd) {
        ThrowUtils.throwIf(jobAdd.getTitle().isBlank(), PARAMS_ERROR, "职位名称不能为空");
        ThrowUtils.throwIf(jobAdd.getJobType() == null, PARAMS_ERROR, "职位类型不能为空");
        if (jobAdd.getMinSalary() != null && jobAdd.getMaxSalary() != null) {
            ThrowUtils.throwIf(jobAdd.getMinSalary() > jobAdd.getMaxSalary(), PARAMS_ERROR, "最小薪资不能大于最大薪资");
        }
        jobService.save(jobAdd);
        return ResultBuilder.success(ADD_SUCCESS);
    }

    @RequestMapping("/update")
    public String update() {
        return "job update";
    }

    @RequestMapping("/delete")
    public String delete() {
        return "job delete";
    }

    @GetMapping("/cards")
    public BaseResponse<Map<String, Object>> getCards(JobCardRequest jobCardRequest) {

        if (jobCardRequest.getMinSalary() != null && jobCardRequest.getMaxSalary() != null) {
            ThrowUtils.throwIf(jobCardRequest.getMinSalary() > jobCardRequest.getMaxSalary(), PARAMS_ERROR, "最小薪资不能大于最大薪资");
        }

        Page<JobCardVO> data = jobService.selectCards(jobCardRequest);
        return PaginationBuilder.build(data);
    }
}

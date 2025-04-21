package com.lightcs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.BaseRequest;
import com.lightcs.model.pojo.JobFair;
import com.lightcs.model.vo.JobFairVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.PaginationBuilder;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.JobFairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.lightcs.constants.Common.*;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-27
 * @Description:
 * @Version: 1.0
 */
@Tag(name = "活动管理")
@RestController
@RequestMapping("/jobFair")
@Slf4j
public class JobFairController {
    @Autowired
    private JobFairService jobFairService;

    //region 用户
    @Operation(summary = "查询活动")
    @GetMapping("/cards")
    public BaseResponse<Map<String, Object>> cards(BaseRequest request) {
        Page<JobFair> jobFairPage = new Page<>(request.getCurrent(), request.getPageSize());
        QueryWrapper<JobFair> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("start_time");
        List<JobFair> list = jobFairService.list(jobFairPage, wrapper);

        // 将 已分页的 数据转换为 VO
        List<JobFairVO> jobFairVOList = list.stream().map(jobFair -> {
            JobFairVO jobFairVO = new JobFairVO();
            jobFairVO.setId(jobFair.getId());
            jobFairVO.setType(jobFair.getType());
            jobFairVO.setOrganizer(jobFair.getOrganizer());
            jobFairVO.setCompany(jobFair.getCompany());
            jobFairVO.setStartTime(jobFair.getStartTime());
            jobFairVO.setEndTime(jobFair.getEndTime());
            jobFairVO.setLocation(jobFair.getLocation());
            jobFairVO.setImageUrl(jobFair.getImageUrl());
            jobFairVO.setDescription(jobFair.getDescription());
            jobFairVO.setName(jobFair.getName());
            return jobFairVO;
        }).toList();
        // 将 VO 转换为 分页对象
        Page<JobFairVO> jobFairVOPage = new Page<>();
        jobFairVOPage.setRecords(jobFairVOList);
        jobFairVOPage.setTotal(jobFairPage.getTotal());
        jobFairVOPage.setCurrent(jobFairPage.getCurrent());
        jobFairVOPage.setSize(jobFairPage.getSize());
        return PaginationBuilder.build(jobFairVOPage);
    }
    //endregion

    //region 管理员
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "新增活动")
    @PostMapping("/add")
    public BaseResponse<String> add(@RequestBody JobFair jobFair) {
        ThrowUtils.throwIf(jobFair.getType() == null, ErrorCode.PARAMS_ERROR, "活动类型不能为空");
        boolean res = jobFairService.save(jobFair);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, ADD_FAIL);
        return ResultBuilder.success(ADD_SUCCESS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "修改活动")
    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody JobFair jobFair) {
        ThrowUtils.throwIf(jobFair.getId() == null, ErrorCode.PARAMS_ERROR, "活动id不能为空");
        boolean res = jobFairService.updateById(jobFair);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, UPDATE_FAIL);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "删除活动")
    @PostMapping("/delete/{id}")
    public BaseResponse<String> delete(@PathVariable Integer id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "活动id不能为空");
        boolean res = jobFairService.removeById(id);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, DELETE_FAIL);
        return ResultBuilder.success(DELETE_SUCCESS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "查询活动列表")
    @GetMapping("/list")
    public BaseResponse<Map<String, Object>> list(BaseRequest request) {
        Page<JobFair> jobFairPage = new Page<>(request.getCurrent(), request.getPageSize());
        QueryWrapper<JobFair> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("start_time");
        List<JobFair> list = jobFairService.list(jobFairPage, wrapper);
        jobFairPage.setRecords(list);
        return PaginationBuilder.build(jobFairPage);
    }
    //endregion

}

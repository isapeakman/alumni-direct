package com.lightcs.controller;

import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.job.JobCategoryAdd;
import com.lightcs.model.dto.job.JobCategoryUpdate;
import com.lightcs.model.pojo.JobCategory;
import com.lightcs.model.vo.JobCategoryVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.JobCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.lightcs.constants.UserConstant.*;
import static com.lightcs.enums.ErrorCode.OPERATION_ERROR;
import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-13
 * @Description: 职位分类
 * @Version: 1.0
 */
@RestController
@RequestMapping("/jobCategory")
public class JobCategoryController {
    @Autowired
    private JobCategoryService jobCategoryService;

    //region admin
    @PostMapping("/add")
    public BaseResponse<String> addJobCategory(@RequestBody JobCategoryAdd jobCategoryAdd) {
        if (jobCategoryAdd == null) {
            return ResultBuilder.fail("职位分类参数不能为空");
        }
        if (jobCategoryAdd.getCategoryName() == null) {
            return ResultBuilder.fail("职位分类名称不能为空");
        }
        boolean res = jobCategoryService.save(JobCategory.builder()
                .categoryName(jobCategoryAdd.getCategoryName())
                .description(jobCategoryAdd.getDescription())
                .parentId(jobCategoryAdd.getParentId())
                .build());
        ThrowUtils.throwIf(res, OPERATION_ERROR, ADD_FAIL);
        return ResultBuilder.success(ADD_SUCCESS);
    }

    @PutMapping("/update")
    public BaseResponse<String> updateJobCategory(@RequestBody JobCategoryUpdate jobCategoryUpdate) {
        if (jobCategoryUpdate == null) {
            return ResultBuilder.fail("职位分类参数不能为空");
        }
        if (jobCategoryUpdate.getId() == null) {
            return ResultBuilder.fail("职位分类id不能为空");
        }
        if (jobCategoryUpdate.getCategoryName() == null) {
            return ResultBuilder.fail("职位分类名称不能为空");
        }
        boolean res = jobCategoryService.updateById(JobCategory.builder()
                .categoryName(jobCategoryUpdate.getCategoryName())
                .description(jobCategoryUpdate.getDescription())
                .parentId(jobCategoryUpdate.getParentId())
                .id(jobCategoryUpdate.getId())
                .build());
        ThrowUtils.throwIf(res, OPERATION_ERROR, UPDATE_FAIL);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    @DeleteMapping("/delete")
    public BaseResponse<String> deleteJobCategory(@RequestParam Long id) {
        if (id == null) {
            return ResultBuilder.fail("职位分类id不能为空");
        }
        long count = jobCategoryService.count();
        ThrowUtils.throwIf(count == 0, PARAMS_ERROR, "职位分类id不存在");
        boolean res = jobCategoryService.removeById(id);
        ThrowUtils.throwIf(res, OPERATION_ERROR, DELETE_FAIL);
        return ResultBuilder.success(DELETE_SUCCESS);
    }

    @GetMapping("/list")
    public BaseResponse<List<JobCategory>> listJobCategory() {
        List<JobCategory> list = jobCategoryService.list();
        return ResultBuilder.success(list);
    }
    //endregion


    //region user
    @GetMapping("/listVO")
    public BaseResponse<List<JobCategoryVO>> listJobCategoryVO() {
        List<JobCategory> list = jobCategoryService.list();

        // 返回JobCategoryVO list 用于前端展示
        List<JobCategoryVO> voList = list.stream().map(jobCategory -> JobCategoryVO.builder()
                .id(jobCategory.getId())
                .categoryName(jobCategory.getCategoryName())
                .parentId(jobCategory.getParentId()).build()).collect(Collectors.toList());

        return ResultBuilder.success(voList);
    }
    //endregion


}

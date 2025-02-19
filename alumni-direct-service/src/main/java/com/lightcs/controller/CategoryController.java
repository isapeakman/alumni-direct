package com.lightcs.controller;

import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.job.CategoryAdd;
import com.lightcs.model.dto.job.CategoryUpdate;
import com.lightcs.model.pojo.Category;
import com.lightcs.model.vo.JobCategoryVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.lightcs.constants.Common.*;
import static com.lightcs.enums.ErrorCode.OPERATION_ERROR;
import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-13
 * @Description: 职位分类
 * @Version: 1.0
 */
@Tag(name = "职位分类")
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    //region admin
    @Operation(summary = "添加职位分类")
    @PostMapping("/add")
    public BaseResponse<String> addJobCategory(@RequestBody CategoryAdd categoryAdd) {
        if (categoryAdd == null) {
            return ResultBuilder.fail("职位分类参数不能为空");
        }
        if (categoryAdd.getCategoryName() == null) {
            return ResultBuilder.fail("职位分类名称不能为空");
        }
        boolean res = categoryService.save(Category.builder()
                .categoryName(categoryAdd.getCategoryName())
                .description(categoryAdd.getDescription())
                .parentId(categoryAdd.getParentId())
                .build());
        ThrowUtils.throwIf(res, OPERATION_ERROR, ADD_FAIL);
        return ResultBuilder.success(ADD_SUCCESS);
    }

    @Operation(summary = "更新职位分类")
    @PutMapping("/update")
    public BaseResponse<String> updateJobCategory(@RequestBody CategoryUpdate categoryUpdate) {
        if (categoryUpdate == null) {
            return ResultBuilder.fail("职位分类参数不能为空");
        }
        if (categoryUpdate.getId() == null) {
            return ResultBuilder.fail("职位分类id不能为空");
        }
        if (categoryUpdate.getCategoryName() == null) {
            return ResultBuilder.fail("职位分类名称不能为空");
        }
        boolean res = categoryService.updateById(Category.builder()
                .categoryName(categoryUpdate.getCategoryName())
                .description(categoryUpdate.getDescription())
                .parentId(categoryUpdate.getParentId())
                .id(categoryUpdate.getId())
                .build());
        ThrowUtils.throwIf(res, OPERATION_ERROR, UPDATE_FAIL);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    @Operation(summary = "删除职位分类")
    @DeleteMapping("/delete")
    public BaseResponse<String> deleteJobCategory(@RequestParam Long id) {
        if (id == null) {
            return ResultBuilder.fail("职位分类id不能为空");
        }
        long count = categoryService.count();
        ThrowUtils.throwIf(count == 0, PARAMS_ERROR, "职位分类id不存在");
        boolean res = categoryService.removeById(id);
        ThrowUtils.throwIf(res, OPERATION_ERROR, DELETE_FAIL);
        return ResultBuilder.success(DELETE_SUCCESS);
    }

    @Operation(summary = "获取职位分类树")
    @GetMapping("/getTree")
    public BaseResponse<List<Map<String, Object>>> listJobCategory() {
        List<Map<String, Object>> categoryTree = categoryService.getCategoryTree();
        return ResultBuilder.success(categoryTree);
    }
    //endregion


    //region user
    @Operation(summary = "获取职位分类VO列表")
    @GetMapping("/listVO")
    public BaseResponse<List<JobCategoryVO>> listJobCategoryVO() {
        List<Category> list = categoryService.list();

        // 返回JobCategoryVO list 用于前端展示
        List<JobCategoryVO> voList = list.stream().map(jobCategory -> JobCategoryVO.builder()
                .id(jobCategory.getId())
                .categoryName(jobCategory.getCategoryName())
                .parentId(jobCategory.getParentId()).build()).collect(Collectors.toList());

        return ResultBuilder.success(voList);
    }
    //endregion


}

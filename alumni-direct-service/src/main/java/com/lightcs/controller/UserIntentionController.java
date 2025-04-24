package com.lightcs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.UserIntentionRequest;
import com.lightcs.model.pojo.UserIntention;
import com.lightcs.model.vo.UserIntentionVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.UserIntentionService;
import com.lightcs.utils.CurrentUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-26
 * @Description: 用户意向控制器
 * @Version: 1.0
 */
@Tag(name = "用户意向")
@RestController
@RequestMapping("/user/intention")
public class UserIntentionController {
    @Autowired
    private UserIntentionService userIntentionService;

    @Operation(summary = "获取用户意向")
    @GetMapping("/get")
    public BaseResponse<List<UserIntentionVO>> getUserIntention() {
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        QueryWrapper<UserIntention> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUserId);
        wrapper.orderByDesc("update_time");
        wrapper.last("limit 1");
        List<UserIntention> list = userIntentionService.list(wrapper);
        // 将 UserIntention的 categoryId 由字符串转换为 List<Integer>
        ArrayList<UserIntentionVO> userIntentionVOS = new ArrayList<>();
        for (UserIntention userIntention : list) {
            UserIntentionVO userIntentionVO = new UserIntentionVO();
            BeanUtils.copyProperties(userIntention, userIntentionVO);
            String categoryId = userIntention.getCategoryId();
            if (categoryId != null) {
                String[] split = categoryId.split(",");
                ArrayList<Integer> integers = new ArrayList<>();
                for (String s : split) {
                    integers.add(Integer.valueOf(s));
                }
                userIntentionVO.setCategoryId(integers);
            }
            userIntentionVOS.add(userIntentionVO);
        }
        return ResultBuilder.success(userIntentionVOS);
    }

    @Operation(summary = "保存用户意向")
    @PostMapping("/save")
    public BaseResponse<String> saveUserIntention(@RequestBody UserIntentionRequest intentionRequest) {
        // 参数校验
        ThrowUtils.throwIf(intentionRequest.getType() == null, ErrorCode.PARAMS_ERROR, "职位类型不能为空");
        ThrowUtils.throwIf(intentionRequest.getCategoryId() == null, ErrorCode.PARAMS_ERROR, "职位分类不能为空");
        ThrowUtils.throwIf(intentionRequest.getMinSalary() == null, ErrorCode.PARAMS_ERROR, "最低薪资不能为空");
        ThrowUtils.throwIf(intentionRequest.getMaxSalary() == null, ErrorCode.PARAMS_ERROR, "最高薪资不能为空");
        ThrowUtils.throwIf(intentionRequest.getMinSalary() > intentionRequest.getMaxSalary(), ErrorCode.PARAMS_ERROR, "最低薪资不能大于最高薪资");
        // 判断用户意向是否存在
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        Integer id = intentionRequest.getId();

        if (id != null) {
            QueryWrapper<UserIntention> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", currentUserId);
            wrapper.eq("id", id);
            UserIntention userIntention = userIntentionService.getOne(wrapper);
            ThrowUtils.throwIf(userIntention == null, ErrorCode.PARAMS_ERROR, "用户意向不存在");
        }

        // 复制属性
        UserIntention userIntention = new UserIntention();
        BeanUtils.copyProperties(intentionRequest, userIntention);
        userIntention.setUserId(currentUserId);

        List<Integer> categoryId = intentionRequest.getCategoryId();
        // 将 List<Integer> 转换为 String,以逗号分隔
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer integer : categoryId) {
            stringBuilder.append(integer).append(",");
        }
        // 去掉最后一个逗号
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        userIntention.setCategoryId(stringBuilder.toString());

        boolean save = userIntentionService.saveOrUpdate(userIntention);//保存或更新
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "保存用户意向失败");
        return ResultBuilder.success("保存用户意向成功");
    }

}

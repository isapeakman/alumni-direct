package com.lightcs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.AuthRequest;
import com.lightcs.model.dto.BaseRequest;
import com.lightcs.model.pojo.UserCertificationApproval;
import com.lightcs.model.vo.AuthVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.PaginationBuilder;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.AuthenticationService;
import com.lightcs.utils.CurrentUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.lightcs.constants.Common.APPLY_SUCCESS;
import static com.lightcs.constants.JobConstant.PASS;
import static com.lightcs.constants.JobConstant.REJECTED;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-27
 * @Description:
 * @Version: 1.0
 */
@Tag(name = "校友认证")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "申请认证")
    @PostMapping("/apply")
    public BaseResponse<String> apply(@RequestBody AuthRequest authRequest) {
        //参数校验
        ThrowUtils.throwIf(StringUtils.isBlank(authRequest.getName()), ErrorCode.PARAMS_ERROR, "姓名不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(authRequest.getStudentId()), ErrorCode.PARAMS_ERROR, "学号不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(authRequest.getPhone()), ErrorCode.PARAMS_ERROR, "手机号不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(authRequest.getMajor()), ErrorCode.PARAMS_ERROR, "专业不能为空");
        ThrowUtils.throwIf(authRequest.getYearAdmission() == null, ErrorCode.PARAMS_ERROR, "入学年份不能为空");
        ThrowUtils.throwIf(authRequest.getYearGraduated() == null, ErrorCode.PARAMS_ERROR, "毕业年份不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(authRequest.getCertification()), ErrorCode.PARAMS_ERROR, "请上传认证材料");

        UserCertificationApproval certificationApproval = new UserCertificationApproval();
        BeanUtils.copyProperties(authRequest, certificationApproval);
        // 补充参数
        certificationApproval.setUserId(CurrentUserUtil.getCurrentUserId());
        certificationApproval.setApplyTime(new Date());
        // 保存 或者更新
        QueryWrapper<UserCertificationApproval> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", CurrentUserUtil.getCurrentUserId());
        UserCertificationApproval existingApproval = authenticationService.getOne(wrapper);
        // 如果已经申请过认证，且状态为未审批，则不允许重复申请
        ThrowUtils.throwIf(existingApproval != null && existingApproval.getApprovalStatus() == 0, ErrorCode.PARAMS_ERROR, "已申请认证，请勿重复申请");

        boolean res = false;
        if (existingApproval != null) {
            // 更新
            certificationApproval.setId(existingApproval.getId());
            certificationApproval.setApprovalStatus(0);// 重新申请时，审批状态置为未审批
            certificationApproval.setApprovalResult(0);// 重新申请时，审批结果置为未通过
            certificationApproval.setNote(null);// 重新申请时，审批备注置为null
            res = authenticationService.updateById(certificationApproval);
        } else {
            // 新增
            res = authenticationService.save(certificationApproval);
        }

        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "申请失败");
        return ResultBuilder.success(APPLY_SUCCESS);
    }

    @Operation(summary = "获取认证信息")
    @GetMapping("/get")
    public BaseResponse<AuthVO> getAuthDetail() {
        Integer userId = CurrentUserUtil.getCurrentUserId();
        // 查询当前用户的最新认证信息
        QueryWrapper<UserCertificationApproval> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("apply_time");
        wrapper.last("limit 1");
        UserCertificationApproval certificationApproval = authenticationService.getOne(wrapper);
        ThrowUtils.throwIf(certificationApproval == null, ErrorCode.NOT_FOUND_ERROR, "认证信息不存在");

        AuthVO authVO = new AuthVO();
        BeanUtils.copyProperties(certificationApproval, authVO);

        return ResultBuilder.success(authVO);
    }

    @Operation(summary = "审批认证")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/approve")
    public BaseResponse<String> approve(Integer id, String note, Integer approvalResult) {
        //参数校验
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR, "id不能为空");
        ThrowUtils.throwIf(approvalResult == null, ErrorCode.PARAMS_ERROR, "审批结果不能为空");
        ThrowUtils.throwIf(!approvalResult.equals(REJECTED) && !approvalResult.equals(PASS), ErrorCode.PARAMS_ERROR, "审批结果不合法");
        // 更新审批结果
        authenticationService.updateRecordById(id, note, approvalResult);
        return ResultBuilder.success("审批成功");
    }

    @Operation(summary = "获取认证列表")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/list")
    public BaseResponse<Map<String, Object>> list(BaseRequest baseRequest) {
        Integer current = baseRequest.getCurrent();
        Integer size = baseRequest.getPageSize();
        Page<UserCertificationApproval> approvalPage = new Page<>(current, size);
        QueryWrapper<UserCertificationApproval> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("apply_time");
        List<UserCertificationApproval> list = authenticationService.list(approvalPage, wrapper);
        approvalPage.setRecords(list);
        return PaginationBuilder.build(approvalPage);
    }
}

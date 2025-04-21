package com.lightcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.JobMapper;
import com.lightcs.mapper.UserCertificationApprovalMapper;
import com.lightcs.mapper.UserMapper;
import com.lightcs.model.pojo.Job;
import com.lightcs.model.pojo.User;
import com.lightcs.model.pojo.UserCertificationApproval;
import com.lightcs.service.AuthenticationService;
import com.lightcs.utils.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

import static com.lightcs.constants.JobConstant.*;
import static com.lightcs.enums.ErrorCode.OPERATION_ERROR;
import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-21
 * @Description:
 * @Version: 1.0
 */
@Service
public class AuthenticationServiceImpl extends ServiceImpl<UserCertificationApprovalMapper, UserCertificationApproval> implements AuthenticationService {
    @Autowired
    private UserCertificationApprovalMapper certificationApprovalMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JobMapper jobMapper;

    @Transactional
    @Override
    public void updateRecordById(Integer recordId, String note, Integer approvalResult) {
        Integer approvalUserId = CurrentUserUtil.getCurrentUserId();
        // 参数校验
        UserCertificationApproval approval = certificationApprovalMapper.selectById(recordId);
        ThrowUtils.throwIf(approval == null, PARAMS_ERROR, "审批记录不存在");

        // 更新审批记录
        int res = certificationApprovalMapper.updateById(UserCertificationApproval.builder()
                .id(recordId).note(note).approvalResult(approvalResult)
                .approvalUserId(approvalUserId)  // 审批人id
                .approvalStatus(APPROVED)       // 审批状态设置为已审批
                .approvalTime(new Date()) // 审批时间
                .build());

        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "更新审批记录失败");

        // 如果是审批通过 则更新  用户校友字段 和 职位校友字段
        if (Objects.equals(approvalResult, PASS)) {
            // 更新用户校友字段
            int isUpdate = userMapper.updateById(User.builder()
                    .userId(approval.getUserId())   // 申请人ID
                    .isAlumni(IS_ALUMNI) // 设置校友字段为1
                    .build());
            ThrowUtils.throwIf(isUpdate == 0, OPERATION_ERROR, "更新用户校友字段失败");

            // 更新 校友用户 所有职位的 校友字段
            QueryWrapper<Job> jobQueryWrapper = new QueryWrapper<>();
            jobQueryWrapper.eq("create_id", approval.getUserId());// 校友用户ID
            int isJobUpdate = jobMapper.update(Job.builder().isAlumni(IS_ALUMNI).build(), jobQueryWrapper);
            ThrowUtils.throwIf(isJobUpdate == 0, OPERATION_ERROR, "更新校友用户职位的校友字段失败");
        }

    }
}

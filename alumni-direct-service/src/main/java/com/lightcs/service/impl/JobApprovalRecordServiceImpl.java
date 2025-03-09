package com.lightcs.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.JobApprovalRecordMapper;
import com.lightcs.mapper.JobMapper;
import com.lightcs.model.dto.JobApprovalRecordRequest;
import com.lightcs.model.pojo.Job;
import com.lightcs.model.pojo.JobApprovalRecord;
import com.lightcs.model.vo.JobApprovalRecordVO;
import com.lightcs.service.JobApprovalRecordService;
import com.lightcs.utils.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.lightcs.constants.JobConstant.*;
import static com.lightcs.enums.ErrorCode.OPERATION_ERROR;

@Service
public class JobApprovalRecordServiceImpl extends ServiceImpl<JobApprovalRecordMapper, JobApprovalRecord> implements JobApprovalRecordService {

    @Autowired
    private JobApprovalRecordMapper jobApprovalRecordMapper;
    @Autowired
    private JobMapper jobMapper;

    /**
     * 获取所有审批记录
     *
     * @param cardRequest 请求参数
     * @return 分页数据
     */
    @Override
    public Page<JobApprovalRecordVO> getAllApprovalRecord(JobApprovalRecordRequest cardRequest) {
        Integer approvalResult = cardRequest.getApprovalResult();
        Integer approvalStatus = cardRequest.getApprovalStatus();
        Integer current = cardRequest.getCurrent();
        Integer pageSize = cardRequest.getPageSize();
        Page<JobApprovalRecordVO> page = new Page<>(current, pageSize);
        String orderBy = "create_time";
        if (Objects.equals(approvalStatus, NOT_APPROVED)) {//未审批
            orderBy = "create_time";
        } else if (Objects.equals(approvalStatus, APPROVED)) {//已审批
            orderBy = "approval_time";
        }
        String orderType = "desc";

        String sorter = orderBy + " " + orderType;

        List<JobApprovalRecordVO> jobApprovalRecords = jobApprovalRecordMapper.selectAllJobApprovalRecords(page, approvalStatus, approvalResult, sorter);
        page.setRecords(jobApprovalRecords);
        return page;
    }

    @Override
    @Transactional
    public void updateRecordById(Integer recordId, String note, Integer approvalResult) {
        Integer approvalUserId = CurrentUserUtil.getCurrentUserId();
        // 更新审批记录
        int res = jobApprovalRecordMapper.updateById(JobApprovalRecord.builder().id(recordId).note(note).approvalResult(approvalResult)
                .approvalUserId(approvalUserId)
                .approvalStatus(APPROVED)       // 审批状态设置为已审批
                .build());

        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "更新审批记录失败");

        JobApprovalRecord record = jobApprovalRecordMapper.selectById(recordId);
        Integer jobId = record.getJobId();
        //更新职位 状态
        int isUpdated = 0;
        if (Objects.equals(approvalResult, PASS)) {//审批通过-->待发布
            isUpdated = jobMapper.updateById(Job.builder().id(jobId).status(STATUS_WAITING).build());
        } else if (Objects.equals(approvalResult, REJECTED)) {//审批未通过-->审核未通过
            isUpdated = jobMapper.updateById(Job.builder().id(jobId).status(STATUS_FAIL).build());
        }
        ThrowUtils.throwIf(isUpdated == 0, OPERATION_ERROR, "更新职位状态失败");
        // todo 发送消息

    }
}
package com.lightcs.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.JobApprovalRecordMapper;
import com.lightcs.model.dto.JobApprovalRecordRequest;
import com.lightcs.model.pojo.JobApprovalRecord;
import com.lightcs.service.JobApprovalRecordService;
import com.lightcs.utils.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lightcs.constants.Common.DEFAULT_ORDER_BY;
import static com.lightcs.constants.JobConstant.APPROVED;
import static com.lightcs.enums.ErrorCode.OPERATION_ERROR;

@Service
public class JobApprovalRecordServiceImpl extends ServiceImpl<JobApprovalRecordMapper, JobApprovalRecord> implements JobApprovalRecordService {

    @Autowired
    private JobApprovalRecordMapper jobApprovalRecordMapper;

    /**
     * 获取所有审批记录
     * @param cardRequest 请求参数
     * @return 分页数据
     */
    @Override
    public Page<JobApprovalRecord> getAllApprovalRecord(JobApprovalRecordRequest cardRequest) {
        Integer approvalResult = cardRequest.getApprovalResult();
        Integer approvalStatus = cardRequest.getApprovalStatus();
        Integer current = cardRequest.getCurrent();
        Integer pageSize = cardRequest.getPageSize();
        Page<JobApprovalRecord> page = new Page<>(current, pageSize);

        String orderBy = "create_time";
        String orderType = cardRequest.getOrderType().equals(DEFAULT_ORDER_BY) ? DEFAULT_ORDER_BY : "asc";
        String sorter = orderBy + " " + orderType;

        List<JobApprovalRecord> jobApprovalRecords = jobApprovalRecordMapper.selectAllJobApprovalRecords(page, approvalResult, approvalStatus, sorter);
        page.setRecords(jobApprovalRecords);
        return page;
    }

    @Override
    public void updateRecordById(Integer recordId, String note, Integer approvalResult) {
        Integer approvalUserId = CurrentUserUtil.getCurrentUserId();
        int res = jobApprovalRecordMapper.updateById(JobApprovalRecord.builder().id(recordId).note(note).approvalResult(approvalResult)
                .approvalUserId(approvalUserId)
                .approvalStatus(APPROVED)       // 审批状态设置为已审批
                .build());
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR,"更新审批记录失败");
    }
}
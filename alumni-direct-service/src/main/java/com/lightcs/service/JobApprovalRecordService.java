package com.lightcs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightcs.model.dto.JobApprovalRecordRequest;
import com.lightcs.model.pojo.JobApprovalRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface JobApprovalRecordService extends IService<JobApprovalRecord> {
    Page<JobApprovalRecord> getAllApprovalRecord(JobApprovalRecordRequest cardRequest);

    void updateRecordById(Integer recordId, String note, Integer approvalResult);
}
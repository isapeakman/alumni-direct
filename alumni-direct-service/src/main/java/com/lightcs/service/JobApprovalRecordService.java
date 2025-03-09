package com.lightcs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lightcs.model.dto.JobApprovalRecordRequest;
import com.lightcs.model.pojo.JobApprovalRecord;
import com.lightcs.model.vo.JobApprovalRecordVO;

public interface JobApprovalRecordService extends IService<JobApprovalRecord> {
    Page<JobApprovalRecordVO> getAllApprovalRecord(JobApprovalRecordRequest cardRequest);

    void updateRecordById(Integer recordId, String note, Integer approvalResult);
}
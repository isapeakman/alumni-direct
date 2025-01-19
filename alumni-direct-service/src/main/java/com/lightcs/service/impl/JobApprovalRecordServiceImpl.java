package com.lightcs.service.impl;

import com.lightcs.model.pojo.JobApprovalRecord;
import com.lightcs.mapper.JobApprovalRecordMapper;
import com.lightcs.service.JobApprovalRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class JobApprovalRecordServiceImpl extends ServiceImpl<JobApprovalRecordMapper, JobApprovalRecord> implements JobApprovalRecordService {
}
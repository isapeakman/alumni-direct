package com.lightcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightcs.model.pojo.JobApprovalRecord;

import java.util.List;

public interface JobApprovalRecordMapper extends BaseMapper<JobApprovalRecord> {
    /**
     * 获取所有职位审批记录
     * @param page 分页
     * @param approvalStatus 审批状态
     * @param approvalResult 审批结果
     * @param sorter 排序
     * @return
     */
    List<JobApprovalRecord> selectAllJobApprovalRecords(Page<JobApprovalRecord> page,Integer approvalStatus, Integer approvalResult, String sorter);
}
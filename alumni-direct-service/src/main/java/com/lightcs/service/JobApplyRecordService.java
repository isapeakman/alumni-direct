package com.lightcs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lightcs.model.dto.job.JobApplyRecordCardRequest;
import com.lightcs.model.pojo.JobApplyRecord;
import com.lightcs.model.vo.JobApplyRecordCardVO;
import com.lightcs.model.vo.JobApplyRecordVO;

/**
 * @author Administrator
 * @description 针对表【job_apply_record(职位申请表)】的数据库操作Service
 * @createDate 2025-01-24 15:47:07
 */
public interface JobApplyRecordService extends IService<JobApplyRecord> {

    @Deprecated
    Page<JobApplyRecordCardVO> getJobApplyRecordCards(JobApplyRecordCardRequest jobId);

    /**
     * 审核职位申请记录
     *
     * @param recordId 记录id
     * @param status   状态
     * @param note     备注
     */
    @Deprecated
    void judgeJobApplyRecord(Integer recordId, Integer status, String note);


    void saveJobApplyRecord(JobApplyRecord jobApplyRecord);

    /**
     * 获取当前用户的职位申请记录
     *
     * @param current  当前页码
     * @param pageSize 每页大小
     * @return 职位申请记录列表
     */
    Page<JobApplyRecordVO> getJobApplyRecords(Integer current, Integer pageSize);
}

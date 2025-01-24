package com.lightcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightcs.model.pojo.JobApplyRecord;
import com.lightcs.model.vo.JobApplyRecordCardVO;

import java.util.List;

/**
* @author Administrator
* @description 针对表【job_apply_record(职位申请表)】的数据库操作Service
* @createDate 2025-01-24 15:47:07
*/
public interface JobApplyRecordService extends IService<JobApplyRecord> {

    List<JobApplyRecordCardVO> getJobApplyRecordCards();
}

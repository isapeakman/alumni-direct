package com.lightcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lightcs.model.pojo.JobApplyRecord;
import com.lightcs.model.vo.JobApplyRecordCardVO;

import java.util.List;

/**
* @author Administrator
* @description 针对表【job_apply_record(职位申请表)】的数据库操作Mapper
* @createDate 2025-01-24 15:47:07
* @Entity com.lightcs.model.pojo.JobApplyRecord
*/
public interface JobApplyRecordMapper extends BaseMapper<JobApplyRecord> {

    List<JobApplyRecordCardVO> getJobApplyRecordCards(Integer jobId);
}





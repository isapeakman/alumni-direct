package com.lightcs.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.mapper.JobApplyRecordMapper;
import com.lightcs.model.vo.JobApplyRecordCardVO;
import generator.domain.JobApplyRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【job_apply_record(职位申请表)】的数据库操作Service实现
* @createDate 2025-01-24 15:47:07
*/
@Service
public class JobApplyRecordServiceImpl extends ServiceImpl<JobApplyRecordMapper, JobApplyRecord>
    implements JobApplyRecordService{

    @Autowired
    private JobApplyRecordMapper jobApplyRecordMapper;
    @Override
    public List<JobApplyRecordCardVO> getJobApplyRecordCards() {
        jobApplyRecordMapper.getJobApplyRecordCards();
        return null;
    }
}





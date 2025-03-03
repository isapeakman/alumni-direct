package com.lightcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.JobApplyRecordMapper;
import com.lightcs.model.dto.job.JobApplyRecordCardRequest;
import com.lightcs.model.pojo.JobApplyRecord;
import com.lightcs.model.vo.JobApplyRecordCardVO;
import com.lightcs.model.vo.JobApplyRecordVO;
import com.lightcs.service.JobApplyRecordService;
import com.lightcs.utils.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lightcs.enums.ErrorCode.OPERATION_ERROR;
import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;

/**
 * @author Administrator
 * @description 针对表【job_apply_record(职位申请表)】的数据库操作Service实现
 * @createDate 2025-01-24 15:47:07
 */
@Service
public class JobApplyRecordServiceImpl extends ServiceImpl<JobApplyRecordMapper, JobApplyRecord> implements JobApplyRecordService {

    @Autowired
    private JobApplyRecordMapper jobApplyRecordMapper;


    public Page<JobApplyRecordCardVO> getJobApplyRecordCards(JobApplyRecordCardRequest cardRequest) {
        Integer current = cardRequest.getCurrent();
        Integer pageSize = cardRequest.getPageSize();
        Page<JobApplyRecordCardVO> page = new Page<>(current, pageSize);
        List<JobApplyRecordCardVO> cards = jobApplyRecordMapper.getJobApplyRecordCards(page, cardRequest.getJobId(), cardRequest.getStatus(), CurrentUserUtil.getCurrentUserId());
        page.setRecords(cards);
        return page;
    }

    @Override
    public void judgeJobApplyRecord(Integer recordId, Integer status, String note) {
        // 不存在该记录
        QueryWrapper<JobApplyRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", recordId);
        queryWrapper.eq("recruiter_id", CurrentUserUtil.getCurrentUserId());
        Long isExist = jobApplyRecordMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(isExist == 0, PARAMS_ERROR, "记录不存在");
        //构建对象
        JobApplyRecord jobApplyRecord = JobApplyRecord.builder().id(recordId).status(status).note(note).build();
        int res = jobApplyRecordMapper.updateById(jobApplyRecord);
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "修改记录失败");
    }

    @Override
    public Page<JobApplyRecordVO> getJobApplyRecords(Integer current, Integer pageSize, Integer status) {
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        Page<JobApplyRecordVO> page = new Page<>(current, pageSize);
        List<JobApplyRecordVO> records = jobApplyRecordMapper.getJobApplyRecords(page, status, currentUserId);
        page.setRecords(records);
        return page;
    }
}





package com.lightcs.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.JobMapper;
import com.lightcs.model.dto.JobCardRequest;
import com.lightcs.model.dto.job.JobAdd;
import com.lightcs.model.pojo.Job;
import com.lightcs.model.vo.JobCardVO;
import com.lightcs.service.JobService;
import com.lightcs.utils.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lightcs.constants.JobConstant.STATUS_PENDING;
import static com.lightcs.enums.ErrorCode.OPERATION_ERROR;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {
    @Autowired
    private JobMapper jobMapper;

    @Override
    public void save(JobAdd jobAdd) {
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        Integer minSalary = jobAdd.getMinSalary();
        Integer maxSalary = jobAdd.getMaxSalary();

        String salaryRange = minSalary + "-" + maxSalary;

        Job job = Job.builder().jobDesc(jobAdd.getJobDesc()).jobType(jobAdd.getJobType()).location(jobAdd.getLocation())
                .salaryRange(salaryRange).title(jobAdd.getTitle()).createId(currentUserId).companyName(jobAdd.getCompanyName())
                .status(STATUS_PENDING)  // 设置职位状态:待审核
                .build();
        int res = jobMapper.insert(job);
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "添加职位失败");
    }

    /**
     * 查询职位卡片
     *
     * @param cardRequest
     * @return
     */
    @Override
    public Page<JobCardVO> selectCards(JobCardRequest cardRequest) {
        Integer current = cardRequest.getCurrent();
        Integer pageSize = cardRequest.getPageSize();
        Page<JobCardVO> page = new Page<>(current, pageSize);

        List<JobCardVO> jobCardVOS = jobMapper.selectCards(page, cardRequest.getTitle(), cardRequest.getJobType(), cardRequest.getLocation(),
                cardRequest.getMinSalary(), cardRequest.getMaxSalary());

        page.setRecords(jobCardVOS);
        return page;
    }
}

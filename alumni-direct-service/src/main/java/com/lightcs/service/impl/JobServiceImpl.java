package com.lightcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.JobApprovalRecordMapper;
import com.lightcs.mapper.JobCategoryMapper;
import com.lightcs.mapper.JobMapper;
import com.lightcs.mapper.UserMapper;
import com.lightcs.model.dto.JobCardRequest;
import com.lightcs.model.dto.JobRequest;
import com.lightcs.model.dto.job.JobAdd;
import com.lightcs.model.dto.job.JobUpdate;
import com.lightcs.model.pojo.Job;
import com.lightcs.model.pojo.JobApprovalRecord;
import com.lightcs.model.pojo.User;
import com.lightcs.model.vo.JobCardVO;
import com.lightcs.model.vo.JobDetailVO;
import com.lightcs.model.vo.JobVO;
import com.lightcs.service.JobService;
import com.lightcs.utils.CurrentUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.lightcs.constants.Common.*;
import static com.lightcs.constants.JobConstant.*;
import static com.lightcs.enums.ErrorCode.NOT_FOUND_ERROR;
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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JobCategoryMapper jobCategoryMapper;
    @Autowired
    private JobApprovalRecordMapper jobApprovalRecordMapper;

    @Override
    @Transactional
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

        // 生成 审核记录
        Integer jobId = job.getId();
        jobApprovalRecordMapper.insert(JobApprovalRecord.builder().jobId(jobId).build());
    }

    @Override
    public void update(JobUpdate jobUpdate) {
        // 该用户下是否存在该待发布职位
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        Integer jobId = jobUpdate.getId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("create_id", currentUserId);
        queryWrapper.eq("id", jobId);
        queryWrapper.eq("status", STATUS_WAITING);
        Long isExist = jobMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(isExist == 0, NOT_FOUND_ERROR, "更新职位不存在");

        // 构造Job
        Integer minSalary = jobUpdate.getMinSalary();
        Integer maxSalary = jobUpdate.getMaxSalary();
        String salaryRange = minSalary + "-" + maxSalary;
        Job job = Job.builder().id(jobUpdate.getId())
                .jobDesc(jobUpdate.getJobDesc()).jobType(jobUpdate.getJobType()).location(jobUpdate.getLocation())
                .salaryRange(salaryRange).title(jobUpdate.getTitle()).companyName(jobUpdate.getCompanyName())
                .status(STATUS_PENDING)  // 调整职位状态为: 待审核
                .build();

        // 更新职位
        int res = jobMapper.updateById(job);
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "更新职位失败");
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
        // 根据分类id查询职位
        List<Integer> jobIdList = null;
        if (cardRequest.getCategoryId() != null) {
            jobIdList = jobCategoryMapper.selectJobIdByCategoryId(cardRequest.getCategoryId());
            // 没有符合的职位
            if (jobIdList == null || jobIdList.isEmpty()) {
                return page;
            }
        }

        // 查询职位卡片
        List<JobCardVO> jobCardVOS = jobMapper.selectCards(page, cardRequest.getTitle(), cardRequest.getJobType(), cardRequest.getLocation(),
                cardRequest.getMinSalary(), cardRequest.getMaxSalary(), STATUS_OPENED, jobIdList);
        // 根据创建人id查询 创建人头像
        jobCardVOS.forEach(jobCardVO -> {
            Integer createId = jobCardVO.getCreateId();
            String avatar = userMapper.selectAvatarById(createId);
            jobCardVO.setRecruiterAvatar(avatar);
        });
        page.setRecords(jobCardVOS);
        return page;
    }

    /**
     * 获取职位详情
     *
     * @param id 职位id
     * @return JobVO
     */
    @Override
    public JobDetailVO detail(Integer id) {
        //查询职位详情
        JobDetailVO jobDetailVO = jobMapper.detail(id, STATUS_OPENED);
        ThrowUtils.throwIf(jobDetailVO == null, NOT_FOUND_ERROR, "职位不存在");
        //获取发布人信息
        Integer createId = jobDetailVO.getCreateId();
        User user = userMapper.selectById(createId);
        jobDetailVO.setRecruiterName(user.getNickname());
        jobDetailVO.setRecruiterAvatar(user.getUserAvatar());
        return jobDetailVO;
    }

    /**
     * 发布职位
     *
     * @param jobId 职位id
     */
    @Override
    public void publish(Integer jobId) {
        //该用户 存在 该职位 并处于待发布状态
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", jobId);
        queryWrapper.eq("create_id", currentUserId);
        queryWrapper.eq("status", STATUS_WAITING);
        Long isExist = jobMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(isExist == 0, NOT_FOUND_ERROR, "职位不存在或状态异常");

        // 更新职位状态为已发布
        Job job = new Job();
        job.setStatus(STATUS_OPENED);
        int res = jobMapper.update(job, queryWrapper);
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, PUBLISH_FAIL);
    }

    /**
     * 下架职位
     *
     * @param jobId 职位Id
     */
    @Override
    public void offline(Integer jobId) {
        //该用户 存在 该职位 并处于已发布状态
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", jobId);
        queryWrapper.eq("create_id", currentUserId);
        queryWrapper.eq("status", STATUS_OPENED);
        Long isExist = jobMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(isExist == 0, NOT_FOUND_ERROR, "职位不存在或状态异常");

        // 更新职位状态为已发布
        Job job = new Job();
        job.setStatus(STATUS_WAITING);
        int res = jobMapper.update(job, queryWrapper);
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, OFFLINE_FAIL);
    }

    /**
     * 关闭职位
     *
     * @param jobId 职位Id
     */
    @Override
    public void close(Integer jobId) {
        // 该用户 存在 该职位 并处于 待发布/已发布状态
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", jobId);
        queryWrapper.eq("create_id", currentUserId);
        queryWrapper.in("status", Arrays.asList(STATUS_WAITING, STATUS_OPENED));
        Long isExist = jobMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(isExist == 0, NOT_FOUND_ERROR, "职位不存在或状态异常");

        // 更新职位状态为已关闭
        Job job = new Job();
        job.setStatus(STATUS_CLOSED);
        int res = jobMapper.update(job, queryWrapper);
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, CLOSE_FAIL);
    }

    @Override
    public Page<JobVO> listByCreatedId(JobRequest jobRequest) {
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        Integer current = jobRequest.getCurrent();
        Integer pageSize = jobRequest.getPageSize();
        Page<JobVO> page = new Page<>(current, pageSize);
        List<JobVO> currentUserJobList = jobMapper.selectByUserId(page, currentUserId, jobRequest.getStatus());
        page.setRecords(currentUserJobList);
        return page;
    }


}

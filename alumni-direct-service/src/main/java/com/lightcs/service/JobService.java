package com.lightcs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lightcs.model.dto.JobCardRequest;
import com.lightcs.model.dto.job.JobAdd;
import com.lightcs.model.dto.job.JobUpdate;
import com.lightcs.model.pojo.Job;
import com.lightcs.model.vo.JobCardVO;
import com.lightcs.model.vo.JobDetailVO;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */

public interface JobService extends IService<Job> {
    /**
     * 保存职位
     *
     * @param jobAdd
     */
    void save(JobAdd jobAdd);

    /**
     * 更新职位
     *
     * @param jobUpdate
     */
    void update(JobUpdate jobUpdate);


    /**
     * 查询职位卡片
     *
     * @param cardRequest
     * @return
     */
    Page<JobCardVO> selectCards(JobCardRequest cardRequest);

    /**
     * 查询职位详情
     *
     * @param id 职位id
     * @return 职位详情
     */
    JobDetailVO detail(Integer id);

    /**
     * 发布职位
     */
    void publish(Integer jobId);

    /**
     * 下线职位
     *
     * @param jobId 职位Id
     */
    void offline(Integer jobId);

    /**
     * 关闭职位
     *
     * @param jobId 职位Id
     */
    void close(Integer jobId);
}

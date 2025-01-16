package com.lightcs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lightcs.model.dto.JobCardRequest;
import com.lightcs.model.dto.job.JobAdd;
import com.lightcs.model.pojo.Job;
import com.lightcs.model.vo.JobCardVO;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */

public interface JobService extends IService<Job> {
    void save(JobAdd jobAdd);


    /**
     * 查询职位卡片
     *
     * @param cardRequest
     * @return
     */
    Page<JobCardVO> selectCards(JobCardRequest cardRequest);
}

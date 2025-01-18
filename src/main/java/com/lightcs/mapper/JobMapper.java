package com.lightcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lightcs.model.pojo.Job;
import com.lightcs.model.vo.JobCardVO;
import com.lightcs.model.vo.JobVO;

import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */

public interface JobMapper extends BaseMapper<Job> {
    /**
     * 查询职位卡片
     *
     * @param page      分页
     * @param title     职位名称
     * @param jobType   职位类型
     * @param location  工作地点
     * @param minSalary 最低薪资
     * @param maxSalary 最高薪资
     * @param status    状态
     * @return
     */
    List<JobCardVO> selectCards(IPage<JobCardVO> page, String title, Integer jobType, String location, Integer minSalary, Integer maxSalary, Integer status);

    /**
     * 查询职位详情
     *
     * @param id     职位id
     * @param status 职位状态
     * @return 职位详情
     */
    JobVO detail(Integer id, Integer status);
}

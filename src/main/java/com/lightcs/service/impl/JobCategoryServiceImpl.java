package com.lightcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.mapper.JobCategoryMapper;
import com.lightcs.model.pojo.JobCategory;
import com.lightcs.service.JobCategoryService;
import org.springframework.stereotype.Service;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */
@Service
public class JobCategoryServiceImpl extends ServiceImpl<JobCategoryMapper, JobCategory> implements JobCategoryService {
}

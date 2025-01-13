package com.lightcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.mapper.JobCategoryMapper;
import com.lightcs.model.pojo.JobCategory;
import com.lightcs.service.JobCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-13
 * @Description: 职位分类Service实现类
 * @Version: 1.0
 */
@Service
@Transactional
public class JobCategoryServiceImpl extends ServiceImpl<JobCategoryMapper, JobCategory> implements JobCategoryService {


}

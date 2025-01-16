package com.lightcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.mapper.CategoryMapper;
import com.lightcs.model.pojo.Category;
import com.lightcs.service.CategoryService;
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
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}

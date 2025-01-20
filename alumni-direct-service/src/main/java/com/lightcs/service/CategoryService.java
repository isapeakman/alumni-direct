package com.lightcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightcs.model.pojo.Category;

import java.util.List;
import java.util.Map;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-13
 * @Description: 职位分类Service
 * @Version: 1.0
 */

public interface CategoryService extends IService<Category> {
    List<Map<String, Object>> getCategoryTree();
}

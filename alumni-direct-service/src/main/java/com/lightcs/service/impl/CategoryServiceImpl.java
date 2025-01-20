package com.lightcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.mapper.CategoryMapper;
import com.lightcs.model.pojo.Category;
import com.lightcs.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.lightcs.constants.Common.ROOT_PARENT_ID;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-13
 * @Description: 职位分类Service实现类
 * @Version: 1.0
 */
@Service
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Map<String, Object>> getCategoryTree() {
        List<Category> categories = categoryMapper.selectList(null);
        List<Map<String, Object>> categoryList = categories.stream()
                .map(category -> {
                    Map<String, Object> categoryMap = new HashMap<>();
                    categoryMap.put("id", category.getId());
                    categoryMap.put("categoryName", category.getCategoryName());
                    categoryMap.put("description", category.getDescription());
                    categoryMap.put("parentId", category.getParentId());
                    return categoryMap;
                })
                .collect(Collectors.toList());

        return buildCategoryTree(categoryList);
    }

    private List<Map<String, Object>> buildCategoryTree(List<Map<String, Object>> categoryList) {

        if (categoryList == null || categoryList.isEmpty()) {
            return Collections.emptyList();
        }

        // 构建父节点到子节点的映射
        Map<String, List<Map<String, Object>>> childrenMap = new HashMap<>();
        for (Map<String, Object> category : categoryList) {
            String parentId = String.valueOf(category.get("parentId"));
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(category);
        }

        List<Map<String, Object>> res = new ArrayList<>();
        for (Map<String, Object> category : categoryList) {
            if (Objects.equals(category.get("parentId"), ROOT_PARENT_ID)) { // 当前节点是根节点
                LinkedHashMap<String, Object> map = new LinkedHashMap<>(category);
                List<Map<String, Object>> children = buildSubCategoryTree(String.valueOf(category.get("id")), childrenMap);
                if (!children.isEmpty()) {
                    map.put("children", children);
                }
                res.add(map);
            }
        }
        return res;
    }

    private List<Map<String, Object>> buildSubCategoryTree(String parentId, Map<String, List<Map<String, Object>>> childrenMap) {
        List<Map<String, Object>> res = new ArrayList<>();
        List<Map<String, Object>> children = childrenMap.getOrDefault(parentId, Collections.emptyList());
        for (Map<String, Object> child : children) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>(child);
            List<Map<String, Object>> subChildren = buildSubCategoryTree(String.valueOf(child.get("id")), childrenMap);
            if (!subChildren.isEmpty()) {
                map.put("children", subChildren);
            }
            res.add(map);
        }
        return res;
    }

}

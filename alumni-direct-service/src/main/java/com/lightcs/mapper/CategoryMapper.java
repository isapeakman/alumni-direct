package com.lightcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lightcs.model.pojo.Category;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-13
 * @Description: 职业分类Mapper
 * @Version: 1.0
 */

public interface CategoryMapper extends BaseMapper<Category> {
    @Select("SELECT id FROM category WHERE parent_id = #{categoryId}")
    List<Integer> selectCategoryIdByParentId(Integer categoryId);
}

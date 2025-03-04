package com.lightcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lightcs.model.pojo.JobCategory;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-16
 * @Description:
 * @Version: 1.0
 */

public interface JobCategoryMapper extends BaseMapper<JobCategory> {
    @Select("select job_id from job_category where category_id = #{categoryId}")
    List<Integer> selectJobIdByCategoryId(Integer categoryId);
}

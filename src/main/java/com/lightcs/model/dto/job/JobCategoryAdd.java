package com.lightcs.model.dto.job;

import lombok.Data;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-13
 * @Description:
 * @Version: 1.0
 */
@Data
public class JobCategoryAdd {
    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 上级id
     */
    private Integer parentId;
}

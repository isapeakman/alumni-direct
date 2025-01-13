package com.lightcs.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-13
 * @Description:
 * @Version: 1.0
 */
@Data
@Builder
public class JobCategoryVO {
    private Integer id;

    /**
     * 分类名
     */
    private String categoryName;

    /**
     * 上级id
     */
    private Integer parentId;
}

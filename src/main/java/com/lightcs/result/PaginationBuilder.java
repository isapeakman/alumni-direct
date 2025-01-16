package com.lightcs.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.LinkedHashMap;
import java.util.Map;


public class PaginationBuilder {
    private PaginationBuilder() {
    }

    /**
     * 分页结果构造器: 从服务层返回的分页数据 封装成前端需要的格式
     *
     * @param page
     * @return
     */
    public static synchronized BaseResponse<Map<String, Object>> build(Page page) {
        LinkedHashMap<String, Object> resultMap = new LinkedHashMap<>();
        LinkedHashMap<String, Object> pageMap = new LinkedHashMap<>();
        resultMap.put("records", page.getRecords());
        pageMap.put("total", page.getTotal());
        pageMap.put("pageNum", page.getCurrent());
        pageMap.put("pageSize", page.getSize());
        resultMap.put("page", pageMap);
        return ResultBuilder.success(resultMap);
    }
}

package com.lightcs.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.LinkedHashMap;
import java.util.Map;


public class PaginationBuilder {
    private PaginationBuilder() {
    }

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

package com.lightcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lightcs.model.pojo.ApiCallLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * API调用日志Mapper
 */
@Mapper
public interface ApiCallLogMapper extends BaseMapper<ApiCallLog> {
}
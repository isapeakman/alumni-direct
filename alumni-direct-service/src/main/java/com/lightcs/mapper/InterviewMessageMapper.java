package com.lightcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lightcs.model.pojo.InterviewMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InterviewMessageMapper extends BaseMapper<InterviewMessage> {

    @Select("SELECT * FROM interview_message WHERE session_id = #{sessionId} ORDER BY sequence ASC")
    List<InterviewMessage> selectBySessionId(String sessionId);

    @Select("SELECT COALESCE(MAX(sequence), 0) FROM interview_message WHERE session_id = #{sessionId}")
    Integer selectMaxSequence(String sessionId);
}

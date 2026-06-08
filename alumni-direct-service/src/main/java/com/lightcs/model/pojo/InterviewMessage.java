package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 面试消息记录实体
 */
@Data
@TableName("interview_message")
public class InterviewMessage {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String sessionId;

    private String role;

    private String content;

    private Integer sequence;

    private LocalDateTime createdAt;
}

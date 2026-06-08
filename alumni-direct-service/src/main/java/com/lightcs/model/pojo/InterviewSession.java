package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 面试会话实体
 */
@Data
@TableName("interview_session")
public class InterviewSession {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    private String resumeId;

    private String resumeContent;

    private String status;

    private String currentQuestion;

    private Integer duration;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime endedAt;
}

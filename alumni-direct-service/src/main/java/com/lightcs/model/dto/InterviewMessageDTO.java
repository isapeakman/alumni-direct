package com.lightcs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 面试消息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewMessageDTO {
    // TODO 注释
    private String id;

    private String role;

    private String content;

    private Integer sequence;

    private LocalDateTime createdAt;
}

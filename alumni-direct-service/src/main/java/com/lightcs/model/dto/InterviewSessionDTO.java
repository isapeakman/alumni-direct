package com.lightcs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试会话DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSessionDTO {

    private String id;

    private String resumeId;

    private String status;

    private String currentQuestion;

    private List<InterviewMessageDTO> messages;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

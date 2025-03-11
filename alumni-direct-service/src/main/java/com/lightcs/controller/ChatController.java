package com.lightcs.controller;

import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.pojo.ChatMessage;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-12
 * @Description:
 * @Version: 1.0
 */
@RestController
@RequestMapping("/chat/history")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @GetMapping
    public BaseResponse<List<ChatMessage>> getChatHistory(Integer userId, Integer contactId, Date historyTime) {
        ThrowUtils.throwIf(userId == null || contactId == null || historyTime == null, ErrorCode.PARAMS_ERROR, "参数不能为空");
        List<ChatMessage> messageHistory = chatService.getMessageHistory(userId, contactId, historyTime);
        return ResultBuilder.success(messageHistory);
    }
}

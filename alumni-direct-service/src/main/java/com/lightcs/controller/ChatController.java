package com.lightcs.controller;

import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.pojo.ChatMessage;
import com.lightcs.model.vo.ChatSessionVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-12
 * @Description:
 * @Version: 1.0
 */
@Tag(name = "聊天相关接口")
@RestController
@RequestMapping("/chat/session")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Operation(summary = "获取聊天历史记录")
    @GetMapping("/history")
    public BaseResponse<List<ChatMessage>> getChatHistory(String sessionId, String historyTime) {
        ThrowUtils.throwIf(sessionId == null, ErrorCode.PARAMS_ERROR, "参数不能为空");
        Date currentHistoryTime;
        if (historyTime == null) {
            currentHistoryTime = new Date();
        } else {
            //将 historyTime 转为 Date
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                currentHistoryTime = simpleDateFormat.parse(historyTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        List<ChatMessage> messageHistory = chatService.getMessageHistory(sessionId, currentHistoryTime);
        return ResultBuilder.success(messageHistory);
    }

    @Operation(summary = "获取会话列表")
    @GetMapping("/list")
    public BaseResponse<List<ChatSessionVO>> getSessionList() {
        return ResultBuilder.success(chatService.getSessionList());
    }

}

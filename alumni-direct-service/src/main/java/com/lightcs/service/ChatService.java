package com.lightcs.service;

import com.lightcs.model.pojo.ChatMessage;
import com.lightcs.model.vo.ChatSessionVO;
import com.lightcs.ws.MessageDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-12
 * @Description:
 * @Version: 1.0
 */

public interface ChatService {
    @Transactional(rollbackFor = RuntimeException.class)
    void saveMessage(MessageDTO messageDTO);

    List<ChatMessage> getUnSendMessage(Integer concatId);

    List<ChatMessage> getMessageHistory(String sessionId, Date historyTime);

    /**
     * 获取会话列表
     *
     * @return
     */
    List<ChatSessionVO> getSessionList();
}

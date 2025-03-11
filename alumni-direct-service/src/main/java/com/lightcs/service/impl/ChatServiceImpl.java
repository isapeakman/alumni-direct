package com.lightcs.service.impl;

import cn.hutool.core.lang.UUID;
import com.lightcs.mapper.ChatMapper;
import com.lightcs.model.pojo.ChatMessage;
import com.lightcs.model.pojo.ChatSession;
import com.lightcs.model.pojo.ChatUserSession;
import com.lightcs.service.ChatService;
import com.lightcs.ws.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.lightcs.constants.MessageConstant.MESSAGE_TYPE_TEXT;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description: 聊天服务实现类
 * @Version: 1.0
 */
@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatMapper chatMapper;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveMessage(MessageDTO messageDTO) {
        String sessionId = chatMapper.getSessionIdByUserIdAndContactId(messageDTO.getFromId(), messageDTO.getToId());
        if (sessionId == null) {//首次会话
            // 保存会话
            sessionId = UUID.randomUUID().toString();
            ChatUserSession chatSession = new ChatUserSession();
            chatSession.setSessionId(sessionId);
            chatSession.setUserId(messageDTO.getFromId());
            chatSession.setContactId(messageDTO.getToId());
            chatMapper.insertSession(chatSession);
        }
        //保存消息
        chatMapper.saveTextMessage(ChatMessage.builder()
                .messageContent(messageDTO.getMsg())
                .messageType(MESSAGE_TYPE_TEXT)      //文件类型
                .contactId(messageDTO.getToId())
                .sendUserId(messageDTO.getFromId())
                .sendTime(messageDTO.getTime())
                .sessionId(sessionId)
                .status(messageDTO.getStatus())//消息状态:0未发送，1已发送
                .build());
        //更新会话的最后一条消息
        chatMapper.updateLastMessage(ChatSession.builder()
                .sessionId(sessionId)
                .lastMessage(messageDTO.getMsg())
                .lastReceiveTime(messageDTO.getTime())
                .build());
    }

    /**
     * 获取未发送的消息
     *
     * @param concatId 联系人ID
     * @return
     */
    @Override
    public List<ChatMessage> getUnSendMessage(Integer concatId) {
        return chatMapper.getUnSendMessage(concatId);
    }

    @Override
    public List<ChatMessage> getMessageHistory(Integer userId, Integer contactId, Date historyTime) {
        return chatMapper.getMessageHistory(userId, contactId, historyTime);
    }

}

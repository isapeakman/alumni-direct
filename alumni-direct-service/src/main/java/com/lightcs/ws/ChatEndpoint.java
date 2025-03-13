package com.lightcs.ws;

import com.alibaba.fastjson2.JSON;
import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.vo.UserVO;
import com.lightcs.service.ChatService;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.lightcs.constants.MessageConstant.MESSAGE_TYPE_STATUS;
import static com.lightcs.constants.MessageConstant.MESSAGE_TYPE_STATUS_SEND;

/**
 * 聊天端点
 */
@ServerEndpoint(value = "/chat", configurator = GetUserVOConfigurator.class)    //针对每个客户端的连接请求，都会创建一个新的 ChatEndpoint 对象
@Component
@Slf4j
public class ChatEndpoint {

    // 保存在线的用户，key为用户id，value为 websocket-Session 对象
    private static final Map<Integer, Session> onlineUsers = new ConcurrentHashMap<>();
    // 保存当前用户的 UserVO 对象
    private UserVO currentUserVO;
    //    @Autowired // 不能注入，因为 每个ChatEndpoint都是一个新的对象，不是单例，这些对象不受spring管理，无法注入
    private static ChatService chatService;// 静态变量，当Spring创建单例的 ChatEndpoint 对象时进行注入，所有ChatEndpoint对象共享一个chatService对象

    @Autowired
    public void setChatService(ChatService chatService) {
        ChatEndpoint.chatService = chatService;
    }

    /**
     * 建立websocket连接后，被调用
     *
     * @param session Session
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        try {
            // 1.将当前用户的 session 对象保存到 onlineUsers 中
            UserVO userVO = (UserVO) config.getUserProperties().get("user");
            ThrowUtils.throwIf(userVO == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
            this.currentUserVO = userVO;
            if (onlineUsers.containsKey(userVO.getUserId())) {
                // 如果当前用户已经在线，则关闭之前的连接
                Session oldSession = onlineUsers.get(userVO.getUserId());
                try {
                    oldSession.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            onlineUsers.put(userVO.getUserId(), session);
            log.warn("用户 {} 上线了", userVO.getNickname());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // todo 将该用户未接收的消息发送给该用户
//        List<ChatMessage> unSendMessages = chatService.getUnSendMessage(userVO.getUserId());
//
//        String msgToSend = JSON.toJSONString(unSendMessages);
//        session.getAsyncRemote().sendText(msgToSend);

    }

    /**
     * 浏览器发送消息到服务端时该方法会被调用，也就是私聊
     *
     * @param message String
     */
    @OnMessage
    public void onMessage(String message) {
        try {
            // 消息格式为 JSON 字符串，将其转为 MessageDTO 对象
            MessageDTO msg = JSON.parseObject(message, MessageDTO.class);

            // 获取消息接收方id
            Integer toUserId = msg.getToId();
            String tempMessage = msg.getMessageContent();
            Date currentTime = new Date();

            // 获取消息接收方用户对象的 session 对象
            Session receiveSession = onlineUsers.get(toUserId);
            Integer status = MESSAGE_TYPE_STATUS;// 未发送
            // 接收方在线，则将消息发送给接收方
            if (receiveSession != null) {
                Integer currentUserId = this.currentUserVO.getUserId();
                String messageToSend = MessageUtils.buildMessage(currentUserId, tempMessage, currentTime);
                receiveSession.getBasicRemote().sendText(messageToSend);
                //todo 判断消息是否发送成功
                status = MESSAGE_TYPE_STATUS_SEND;// 已发送
            }
            // 消息保存到数据库中，等接收方上线后再发送
            msg.setFromId(this.currentUserVO.getUserId());
            msg.setStatus(status);
            msg.setTime(currentTime);
            chatService.saveMessage(msg);//todo 立即同步增加网络时延，后续考虑异步处理，如定时任务、消息队列；或者是在连接断开时同步到数据库中

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 断开 websocket 连接时被调用
     *
     * @param session Session
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        // 从 onlineUsers 中删除当前用户的 session 对象，表示当前用户已下线

        String userId = String.valueOf(this.currentUserVO.getUserId());
        if (userId != null) {
            Session remove = onlineUsers.remove(userId);
            if (remove != null) {
                remove.close();
            }
            session.close();
            log.warn("用户 {} 下线了", this.currentUserVO.getNickname());
        }
    }

}


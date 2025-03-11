package com.lightcs.ws;

import com.alibaba.fastjson2.JSON;
import com.lightcs.model.vo.UserVO;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天端点
 */
@ServerEndpoint(value = "/chat", configurator = GetUserVOConfigurator.class)    //针对每个客户端的连接请求，都会创建一个新的 ChatEndpoint 对象
@Component
@Slf4j
public class ChatEndpoint {

    // 保存在线的用户，key为用户id，value为 websocket-Session 对象
    private static final Map<String, Session> onlineUsers = new ConcurrentHashMap<>();
    // 保存当前用户的 UserVO 对象
    private UserVO currentUserVO;

    /**
     * 建立websocket连接后，被调用
     *
     * @param session Session
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        // 1.将当前用户的 session 对象保存到 onlineUsers 中
        UserVO userVO = (UserVO) config.getUserProperties().get("user");
        log.error("已连接===========");
        if (userVO != null) {
            this.currentUserVO = userVO;
            onlineUsers.put(String.valueOf(userVO.getUserId()), session);
            log.warn("用户 {} 上线了", userVO.getNickname());
            // todo 将该用户未读的消息发送给该用户   是HTTP请求直接查询还是websocket发送消息

        }
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
            String toUserId = msg.getToId();
            String tempMessage = msg.getMsg();

            // 获取消息接收方用户对象的 session 对象
            Session receiveSession = onlineUsers.get(toUserId);
            // 接收方在线，则将消息发送给接收方
            if (receiveSession != null) {
                String currentUserId = String.valueOf(this.currentUserVO.getUserId());
                String messageToSend = MessageUtils.buildMessage(currentUserId, tempMessage);

                receiveSession.getBasicRemote().sendText(messageToSend);
            }
            // 接收方不在线
            // todo 将消息保存到数据库中，等接收方上线后再发送
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


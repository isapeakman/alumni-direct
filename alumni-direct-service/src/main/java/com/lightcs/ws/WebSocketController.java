package com.lightcs.ws;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-10
 * @Description:
 * @Version: 1.0
 */

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    // 处理客户端发送到 /app/chat 的消息
    @MessageMapping("/chat")
    @SendTo("/topic/messages") // 将消息广播到 /topic/messages
    public String sendMessage(String message) {
        return message;
    }
}

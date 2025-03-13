package com.lightcs.ws;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description:
 * @Version: 1.0
 */

/**
 * 进行 websocket的拦截:在消息发送前进行权限验证和处理
 */
public class Interceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        return ChannelInterceptor.super.preSend(message, channel);
    }

}

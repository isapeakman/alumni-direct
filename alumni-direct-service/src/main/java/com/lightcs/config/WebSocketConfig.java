package com.lightcs.config;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-10
 * @Description:
 * @Version: 1.0
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的消息代理，前缀为 /topic
        config.enableSimpleBroker("/topic");
        // 设置应用程序目的地前缀为 /app
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册一个 WebSocket 端点，客户端将使用它连接到 WebSocket 服务器
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")      // 允许跨域,需要单独设置跨域
                .withSockJS();
    }
}

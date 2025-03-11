package com.lightcs.config;

import com.lightcs.ws.ChatEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description: websocket配置类
 * @Version: 1.0
 */
@Configuration
public class WebSocketConfig {
    /**
     * 注入ServerEndpointExporter，该Bean会自动注册使用@ServerEndpoint注解声明的websocket endpoint
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        ServerEndpointExporter exporter = new ServerEndpointExporter();
        exporter.setAnnotatedEndpointClasses(ChatEndpoint.class);
        return exporter;
    }
}

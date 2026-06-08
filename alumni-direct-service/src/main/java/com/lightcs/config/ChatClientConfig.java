package com.lightcs.config;

import com.lightcs.provider.PromptTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatClient 配置类
 * 根据不同场景配置预设系统提示词的 ChatClient
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

    private final PromptTemplateService promptTemplateService;

    /**
     * 通用场景 ChatClient（不预设系统提示词）
     */
    @Bean("generalChatClient")
    public ChatClient generalChatClient(ChatClient.Builder chatClientBuilder) {
        log.info("初始化通用场景 ChatClient");
        return chatClientBuilder.build();
    }

    /**
     * 简历解析场景 ChatClient
     * 预加载 resume-parse.txt 系统提示词
     */
    @Bean("resumeParseChatClient")
    public ChatClient resumeParseChatClient(ChatClient.Builder chatClientBuilder) {
        log.info("初始化简历解析场景 ChatClient");
        String systemPrompt = promptTemplateService.loadPrompt("resume-parse.txt");
        // TODO 配置温度、最大Token数等参数
        return chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }

    /**
     * AI模拟面试场景 ChatClient
     * 预加载 resume-prompt.st 系统提示词
     */
    @Bean("aiInterviewChatClient")
    public ChatClient aiInterviewChatClient(ChatClient.Builder chatClientBuilder) {
        log.info("初始化AI模拟面试场景 ChatClient");
        String systemPrompt = promptTemplateService.loadPrompt("resume-prompt.st");
        // TODO 配置温度、最大Token数等参数
        return chatClientBuilder
                .defaultSystem(systemPrompt)
                .build();
    }
}

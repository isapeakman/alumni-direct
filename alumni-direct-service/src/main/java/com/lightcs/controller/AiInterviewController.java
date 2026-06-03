package com.lightcs.controller;


import com.lightcs.provider.PromptTemplateService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class AiInterviewController {

    private final ChatClient chatClient;
    private ZhiPuAiChatModel chatModel;
    private PromptTemplateService promptTemplateService;

    public AiInterviewController(ChatClient.Builder chatClientBuilder,
                                 ZhiPuAiChatModel chatModel,
                                 PromptTemplateService promptTemplateService) {
        this.chatClient = chatClientBuilder.build();
        this.chatModel = chatModel;
        this.promptTemplateService = promptTemplateService;
    }

    @GetMapping("/ai")
    String generation(String userInput, String jobTitle) throws Exception {
        // 根据职位获取特定的系统提示
        String jobSpecificPrompt = promptTemplateService.getJobSpecificPrompt(jobTitle);
        return this.chatClient.prompt()
                .system(jobSpecificPrompt)
                .user(userInput)
                .call()
                .content();
    }

    @GetMapping("/ai/stream")
    Flux<String> generationByStream(String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                .stream()
                .content();
    }


    /**
     * glm 模型直接调用
     *
     * @return
     */
    @GetMapping("/call/chat")
    public String callChat(@RequestParam String message, @RequestParam(defaultValue = "glm-4.6") String model) {
        ChatResponse response = chatModel.call(
                new Prompt(
                        message,
                        ZhiPuAiChatOptions.builder()
                                .model(model)
                                .temperature(0.7)
                                .build()
                ));
        return response.getResult().getOutput().getText();
    }

    /**
     * glm 模型流式调用
     *
     * @return
     */
    @GetMapping(value = "/stream/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> streamChat(@RequestParam String message, @RequestParam(defaultValue = "glm-4.6") String model) {
        return chatModel.stream(
                        new Prompt(message, ZhiPuAiChatOptions.builder().model(model).build())
                )
                .map(chatResponse -> {
                    String text = chatResponse.getResults()
                            .stream()
                            .filter(r -> r.getOutput().getText() != null)
                            .map(r -> r.getOutput().getText())
                            .findFirst()
                            .orElse("");
                    return ServerSentEvent.builder()
                            .data(text)
                            .build();
                })
                .concatWith(Flux.just(
                        ServerSentEvent.builder()
                                .data("[DONE]")
                                .build()
                ))
                .doOnError(Throwable::printStackTrace);
    }
}

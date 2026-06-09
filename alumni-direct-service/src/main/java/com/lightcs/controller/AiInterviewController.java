package com.lightcs.controller;


import com.lightcs.exception.BusinessException;
import com.lightcs.model.dto.InterviewMessageDTO;
import com.lightcs.model.dto.InterviewSessionDTO;
import com.lightcs.model.vo.AsyncTaskStatusVO;
import com.lightcs.provider.PromptTemplateService;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.AsyncResumeParseService;
import com.lightcs.service.InterviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
public class AiInterviewController {

    private final ChatClient generalChatClient;
    private final ChatClient aiInterviewChatClient;
    private final ZhiPuAiChatModel chatModel;
    private final PromptTemplateService promptTemplateService;
    private final AsyncResumeParseService asyncResumeParseService;
    private final InterviewService interviewService;

    public AiInterviewController(ChatClient generalChatClient,
                                 ChatClient aiInterviewChatClient,
                                 ZhiPuAiChatModel chatModel,
                                 PromptTemplateService promptTemplateService,
                                 AsyncResumeParseService asyncResumeParseService,
                                 InterviewService interviewService) {
        this.generalChatClient = generalChatClient;
        this.aiInterviewChatClient = aiInterviewChatClient;
        this.chatModel = chatModel;
        this.promptTemplateService = promptTemplateService;
        this.asyncResumeParseService = asyncResumeParseService;
        this.interviewService = interviewService;
    }


    /**
     * 异步简历解析接口 - 提交任务
     *
     * @param file 简历文件（支持图片、PDF等格式）
     * @return 任务ID
     */
    @PostMapping("/ai/resume/parse/async")
    public BaseResponse<Object> submitParseTask(@RequestPart MultipartFile file) {
        try {
            // 验证文件是否为空
            if (file == null || file.isEmpty()) {
                return ResultBuilder.fail("上传的文件不能为空");
            }

            // 验证文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResultBuilder.fail("文件名无效");
            }

            String lowerCaseFilename = originalFilename.toLowerCase();
            boolean isSupportedFormat = lowerCaseFilename.endsWith(".pdf") ||
                    lowerCaseFilename.endsWith(".jpg") ||
                    lowerCaseFilename.endsWith(".jpeg") ||
                    lowerCaseFilename.endsWith(".png") ||
                    lowerCaseFilename.endsWith(".gif") ||
                    lowerCaseFilename.endsWith(".bmp");

            if (!isSupportedFormat) {
                return ResultBuilder.fail("不支持的文件格式，请上传PDF或图片文件（JPG、PNG等）");
            }

            // 提交异步任务
            String taskId = asyncResumeParseService.submitParseTask(file);
            // 返回任务ID
            return ResultBuilder.success(java.util.Map.of("taskId", taskId));

        } catch (BusinessException e) {
            log.error("提交简历解析任务业务异常", e);
            return ResultBuilder.fail(e.getMessage());
        } catch (Exception e) {
            log.error("提交简历解析任务失败", e);
            return ResultBuilder.fail("提交任务失败: " + e.getMessage());
        }
    }

    /**
     * 查询简历解析任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态
     */
    @GetMapping("/ai/resume/parse/task/{taskId}")
    public BaseResponse<Object> getParseTaskStatus(@PathVariable String taskId) {
        try {
            AsyncTaskStatusVO taskStatus = asyncResumeParseService.getTaskStatus(taskId);
            return ResultBuilder.success(taskStatus);
        } catch (Exception e) {
            log.error("查询任务状态失败: {}", taskId, e);
            return ResultBuilder.fail("查询任务状态失败: " + e.getMessage());
        }
    }

    /**
     * 开始新面试
     *
     * @param resumeId      简历ID（可选）
     * @param resumeContent 简历内容（JSON格式）
     * @return 面试会话信息
     */
    @PostMapping("/interview/start")
    public BaseResponse<InterviewSessionDTO> startInterview(
            @RequestParam(required = false) String resumeId,
            @RequestBody String resumeContent) {
        log.info("开始新面试，简历ID: {}", resumeId != null ? resumeId : "未提供");
        InterviewSessionDTO session = interviewService.startInterview(resumeId, resumeContent);
        return ResultBuilder.success(session);
    }

    /**
     * 发送消息（用户回答）
     *
     * @param sessionId  会话ID
     * @param userAnswer 用户回答
     * @return AI回复
     */
    @PostMapping("/interview/{sessionId}/message")
    public BaseResponse<InterviewMessageDTO> sendMessage(
            @PathVariable String sessionId,
            @RequestBody String userAnswer) {
        log.info("发送消息，会话ID: {}", sessionId);
        InterviewMessageDTO message = interviewService.sendMessage(sessionId, userAnswer);
        return ResultBuilder.success(message);
    }

    /**
     * 获取会话详情
     *
     * @param sessionId 会话ID
     * @return 会话详情
     */
    @GetMapping("/interview/{sessionId}")
    public BaseResponse<InterviewSessionDTO> getSession(@PathVariable String sessionId) {
        InterviewSessionDTO session = interviewService.getSession(sessionId);
        return ResultBuilder.success(session);
    }

    /**
     * 获取面试会话列表
     *
     * @return 会话列表
     */
    @GetMapping("/interview/list")
    public BaseResponse<List<InterviewSessionDTO>> listSessions() {
        List<InterviewSessionDTO> sessions = interviewService.listSessions();
        return ResultBuilder.success(sessions);
    }

    /**
     * 结束面试
     *
     * @param sessionId 会话ID
     * @return 面试总结
     */
    @PostMapping("/interview/{sessionId}/end")
    public BaseResponse<String> endInterview(@PathVariable String sessionId) {
        log.info("结束面试，会话ID: {}", sessionId);
        String summary = interviewService.endInterview(sessionId);
        return ResultBuilder.success(summary);
    }

    @GetMapping("/ai")
    String generation(String userInput, String jobTitle, String resumeText) throws Exception {
        // 使用 AI面试场景的 ChatClient，但传入自定义提示词会覆盖预设
        String jobSpecificPrompt = promptTemplateService.getJobSpecificPrompt(jobTitle, resumeText);
        return aiInterviewChatClient.prompt()
                .system(jobSpecificPrompt)
                .user(userInput)
                .call()
                .content();
    }


    @GetMapping("/ai/stream")
    Flux<String> generationByStream(String userInput) {
        // 使用通用场景的 ChatClient
        return this.generalChatClient.prompt()
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

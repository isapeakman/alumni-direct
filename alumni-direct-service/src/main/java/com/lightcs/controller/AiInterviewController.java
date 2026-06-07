package com.lightcs.controller;


import com.alibaba.fastjson2.JSON;
import com.lightcs.exception.BusinessException;
import com.lightcs.model.dto.ResumeGenerationRequest;
import com.lightcs.model.vo.AsyncTaskStatusVO;
import com.lightcs.provider.PromptTemplateService;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.AsyncResumeParseService;
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

@Slf4j
@RestController
public class AiInterviewController {

    private final ChatClient generalChatClient;
    private final ChatClient aiInterviewChatClient;
    private final ZhiPuAiChatModel chatModel;
    private final PromptTemplateService promptTemplateService;
    private final AsyncResumeParseService asyncResumeParseService;

    public AiInterviewController(ChatClient generalChatClient,
                                 ChatClient aiInterviewChatClient,
                                 ZhiPuAiChatModel chatModel,
                                 PromptTemplateService promptTemplateService,
                                 AsyncResumeParseService asyncResumeParseService) {
        this.generalChatClient = generalChatClient;
        this.aiInterviewChatClient = aiInterviewChatClient;
        this.chatModel = chatModel;
        this.promptTemplateService = promptTemplateService;
        this.asyncResumeParseService = asyncResumeParseService;
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

    /**
     * 简历生成接口
     * 根据用户修改后的简历信息和求职岗位，生成求职材料
     *
     * @param request 简历信息和求职岗位
     * @return 生成的求职材料（求职信、面试建议、技能匹配分析）
     */
    @PostMapping("/ai/resume/generate")
    public BaseResponse<String> generateResumeMaterial(@RequestBody ResumeGenerationRequest request) {
        try {
            // 验证必填参数
            if (request.getDesiredPosition() == null || request.getDesiredPosition().isBlank()) {
                return ResultBuilder.fail("求职岗位不能为空");
            }

            // 将简历信息转换为JSON字符串
            String resumeInfo = JSON.toJSONString(request);

            // 获取提示词
            String prompt = promptTemplateService.getResumeGeneratePrompt(
                    request.getDesiredPosition(),
                    resumeInfo
            );

            // 使用通用场景的 ChatClient
            String result = generalChatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("简历生成完成，求职岗位: {}", request.getDesiredPosition());
            return ResultBuilder.success(result);

        } catch (BusinessException e) {
            log.error("简历生成业务异常", e);
            return ResultBuilder.fail(e.getMessage());
        } catch (Exception e) {
            log.error("简历生成失败", e);
            return ResultBuilder.fail("生成失败: " + e.getMessage());
        }
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
}

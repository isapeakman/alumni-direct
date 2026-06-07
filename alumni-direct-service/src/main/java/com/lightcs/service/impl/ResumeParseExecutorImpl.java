package com.lightcs.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lightcs.component.LLMApiStrategy;
import com.lightcs.component.OcrService;
import com.lightcs.enums.AsyncTaskStageEnum;
import com.lightcs.enums.AsyncTaskStatusEnum;
import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.BusinessException;
import com.lightcs.model.vo.AsyncTaskStatusVO;
import com.lightcs.model.vo.ResumeDTO;
import com.lightcs.service.ResumeParseExecutor;
import com.lightcs.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.lightcs.constants.RedisConstant.TASK_KEY_PREFIX;

/**
 * 简历解析异步执行器实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeParseExecutorImpl implements ResumeParseExecutor {

    private final OcrService ocrService;
    private final LLMApiStrategy llmApiStrategy;
    private final RedisUtil redisUtil;

    /**
     * 异步执行简历解析任务
     * 此方法会被 Spring AOP 代理，确保真正的异步执行
     */
    @Override
    @Async("taskExecutor")
    public void executeAsync(String taskId, MultipartFile file) {
        try {
            // 更新状态为处理中 - OCR阶段
            updateTaskStatus(taskId, AsyncTaskStatusEnum.PROCESSING.getValue(), 10,
                    AsyncTaskStageEnum.OCR_RECOGNITION.getValue(), null, null);

            log.info("🚀[任务: {}] 开始解析简历文件...", taskId);

            // 执行简历解析（包含OCR和GLM调用）
            ResumeDTO result = parseResume(taskId, file);

            log.info("✅ [任务: {}] 解析成功", taskId);

            // 更新状态为完成
            updateTaskStatus(taskId, AsyncTaskStatusEnum.COMPLETED.getValue(), 100,
                    AsyncTaskStageEnum.COMPLETED.getValue(), result, null);
        } catch (Exception e) {
            log.error("❌ [任务: {}] 简历解析任务失败", taskId, e);
            // 更新状态为失败
            updateTaskStatus(taskId, AsyncTaskStatusEnum.FAILED.getValue(), 0,
                    AsyncTaskStageEnum.FAILED.getValue(), null, e.getMessage());
        }
    }

    /**
     * 解析简历文件
     *
     * @param taskId 任务ID
     * @param file   简历文件（支持PDF、图片等格式）
     * @return 结构化的简历DTO
     */
    private ResumeDTO parseResume(String taskId, MultipartFile file) {
        String filename = file.getOriginalFilename();
        // 立即读取文件内容到字节数组，避免临时文件被清理
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
            log.info("[任务: {}] ✅ 文件读取成功，文件大小: {} bytes", taskId, fileBytes.length);
            // 验证文件内容是否为空
            if (fileBytes.length == 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传的文件为空，请重新选择文件");
            }
        } catch (IOException e) {
            log.error("[任务: {}] ❌ 读取上传文件失败: {}", taskId, filename, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "读取上传文件失败，请检查文件是否完整或重新上传");
        }

        try {
            // 1. OCR识别：将图片/PDF转换为文本
            String rawText = ocrImageOrPDF(taskId, filename, fileBytes);

            // 2. 文本预处理（轻微清理，保留原始特征供GLM纠错）
            String cleanedText = cleanText(rawText, taskId);

            // 3. GLM结构化：纠正OCR错误并提取结构化信息（系统提示词已在ChatClient中预设）
            log.info("[任务: {}] 🤖 调用GLM进行OCR纠错和结构化解析...", taskId);
            String jsonResult = llmApiStrategy.parseResume(cleanedText);
            log.info("[任务: {}] ✅ GLM解析完成，返回JSON长度: {} 字符", taskId, jsonResult.length());
            updateTaskStatus(taskId, AsyncTaskStatusEnum.PROCESSING.getValue(), 90,
                    AsyncTaskStageEnum.GLM_PARSING.getValue(), null, null);
            // 4. 解析JSON为DTO对象
            ResumeDTO resumeDTO = parseJsonToDto(jsonResult);
            resumeDTO.setRawText(cleanedText);
            return resumeDTO;
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("[任务: {}] ❌ 简历解析失败: {}", taskId, filename, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "简历解析失败: " + e.getMessage());
        }
    }

    /**
     * 使用OCR识别图片或PDF文件，并返回识别结果
     *
     * @param taskId    任务ID
     * @param filename  文件名
     * @param fileBytes 文件字节数组
     * @return 识别结果文本
     */
    @NotNull
    private String ocrImageOrPDF(String taskId, String filename, byte[] fileBytes) {
        String rawText;
        if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
            log.info("[任务: {}] 检测到PDF文件，使用OCR识别PDF内容", taskId);
            rawText = ocrService.recognizePdfText(fileBytes);
        } else {
            log.info("[任务: {}] 检测到图片文件，使用OCR识别图片内容", taskId);
            rawText = ocrService.recognizeText(fileBytes);
        }

        log.info("[任务: {}] ✅ OCR识别完成，结果长度: {} 字符", taskId, rawText.length());
        log.debug("[任务: {}] OCR识别原始结果:\n{}", taskId, rawText);

        if (rawText.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法从文件中提取文本内容，请检查文件是否清晰可读");
        }
        // 更新状态为处理中 - OCR阶段
        updateTaskStatus(taskId, AsyncTaskStatusEnum.PROCESSING.getValue(), 30,
                AsyncTaskStageEnum.OCR_RECOGNITION.getValue(), null, null);
        return rawText;
    }

    /**
     * 使用Tika提取PDF文本
     */
    private String extractPdfText(byte[] pdfBytes) {
        try {
            org.apache.tika.Tika tika = new org.apache.tika.Tika();
            return tika.parseToString(new java.io.ByteArrayInputStream(pdfBytes));
        } catch (Exception e) {
            log.warn("Tika提取PDF文本失败，将使用OCR: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 文本预处理（OCR后基础清洗）
     * 移除明显的噪声字符和特殊符号
     */
    private String cleanText(String text, String taskId) {
        if (text == null) {
            return "";
        }

        String cleaned = text;

        // 1. 移除所有黑色的方块、圆点等OCR噪声字符（扩展Unicode范围）
        // 包括：黑方框、黑圆点、各种几何图形、制表符等
        cleaned = cleaned.replaceAll("[■●◆▲▼◀▶◇○□▪▫►◄•·‣⁃⁌⁍※†‡§¶]", " ");

        // 2. 移除其他常见的OCR噪声字符
        // ▢▣▤▥▦▧▨▩▪▫▬▭▮▯ 等几何图形
        cleaned = cleaned.replaceAll("[▢▣▤▥▦▧▨▩▪▫▬▭▮▯]", " ");
        // █▌▐▍▎▏ 等块元素
        cleaned = cleaned.replaceAll("[█▌▐▍▎▏▁▂▃▄▅▆▇]", " ");
        // ◼◻◽◾⬛⬜ 等黑白方块
        cleaned = cleaned.replaceAll("[◼◻◽◾⬛⬜⬟⬠]", " ");
        // ⚫⚪⏺ 等圆形符号
        cleaned = cleaned.replaceAll("[⚫⚪⏺⏏]", " ");

        // 3. 移除连续的空白字符（包括空格、Tab等）
        cleaned = cleaned.replaceAll("[\\s&&[^\\n]]+", " ");

        // 4. 移除连续的特殊标点（如多个句号、逗号等）
        cleaned = cleaned.replaceAll("[.。]{3,}", "...");
        cleaned = cleaned.replaceAll("[,，]{2,}", ",");

        // 5. 去除连续的换行（保留最多2个换行）
        cleaned = cleaned.replaceAll("\n{3,}", "\n\n");

        // 6. 去除每行首尾的空白
        String[] lines = cleaned.split("\n");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }
        cleaned = String.join("\n", lines);

        // 7. 去除整体首尾空白
        cleaned = cleaned.trim();

        log.debug("文本清洗完成，原始长度: {}, 清洗后长度: {}", text.length(), cleaned.length());
        updateTaskStatus(taskId, AsyncTaskStatusEnum.PROCESSING.getValue(), 40,
                AsyncTaskStageEnum.GLM_PARSING.getValue(), null, null);
        return cleaned;
    }

    /**
     * 解析JSON为DTO
     */
    private ResumeDTO parseJsonToDto(String jsonResult) {
        try {
            return JSON.parseObject(jsonResult, ResumeDTO.class);
        } catch (Exception e) {
            log.error("解析GLM返回的JSON失败: {}", jsonResult, e);
            // 如果解析失败，返回包含原始文本的DTO
            return ResumeDTO.builder()
                    .rawText(jsonResult)
                    .build();
        }
    }

    /**
     * 仅使用OCR识别文本（不进行结构化）
     */
    public String recognizeTextOnly(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String filename = file.getOriginalFilename();

            if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
                String text = extractPdfText(fileBytes);
                if (!text.isEmpty() && text.length() >= 50) {
                    return text;
                }
                return ocrService.recognizePdfText(fileBytes);
            } else {
                return ocrService.recognizeText(fileBytes);
            }
        } catch (IOException e) {
            log.error("OCR识别失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "OCR识别失败: " + e.getMessage());
        }
    }

    /**
     * 更新任务状态到Redis
     */
    private void updateTaskStatus(String taskId, String status, Integer progress,
                                  String stage, Object result, String errorMessage) {
        AsyncTaskStatusVO taskStatus = AsyncTaskStatusVO.builder()
                .taskId(taskId)
                .status(status)
                .progress(progress)
                .stage(stage)
                .result(result)
                .errorMessage(errorMessage)
                .completeTime(AsyncTaskStatusEnum.isFinalState(status)
                        ? System.currentTimeMillis() : null)
                .build();

        String key = TASK_KEY_PREFIX + taskId;
        String taskJson = JSON.toJSONString(taskStatus);
        redisUtil.set(key, taskJson, 30, TimeUnit.MINUTES);

        log.debug("📊 [任务: {}] 状态更新: {} - {}% - {}", taskId, status, progress, stage);
    }
}

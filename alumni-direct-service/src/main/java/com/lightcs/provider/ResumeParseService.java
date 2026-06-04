package com.lightcs.provider;


import com.alibaba.fastjson2.JSON;
import com.lightcs.component.GlmApiService;
import com.lightcs.component.OcrService;
import com.lightcs.model.vo.ResumeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 简历解析服务类
 * 整合OCR识别和GLM结构化
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeParseService {
    private final OcrService ocrService;
    private final GlmApiService glmApiService;

    /**
     * 解析简历文件
     *
     * @param file 简历文件（支持PDF、图片等格式）
     * @return 结构化的简历DTO
     */
    public ResumeDTO parseResume(MultipartFile file) {
        String filename = file.getOriginalFilename();
        log.info("开始解析简历文件: {}", filename);

        try {
            byte[] fileBytes = file.getBytes();

            // 1. OCR识别：将图片/PDF转换为文本
            String rawText;
            if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
                log.info("检测到PDF文件，使用OCR识别PDF内容");
                rawText = ocrService.recognizePdfText(fileBytes);
            } else {
                log.info("检测到图片文件，使用OCR识别图片内容");
                rawText = ocrService.recognizeText(fileBytes);
            }

            log.info("✅ OCR识别完成，结果长度: {} 字符", rawText.length());
            log.debug("OCR识别原始结果:\n{}", rawText);

            if (rawText.isEmpty()) {
                throw new RuntimeException("无法从文件中提取文本内容，请检查文件是否清晰可读");
            }

            // 2. 文本预处理（轻微清理，保留原始特征供GLM纠错）
            String cleanedText = cleanText(rawText);
            log.info("文本预处理完成，清理后长度: {} 字符", cleanedText.length());

            // 3. GLM结构化：纠正OCR错误并提取结构化信息
            log.info("🤖 调用GLM进行OCR纠错和结构化解析...");
            String jsonResult = glmApiService.parseResumeToJson(cleanedText);
            log.info("✅ GLM解析完成，返回JSON长度: {} 字符", jsonResult.length());
            log.debug("GLM返回的JSON:\n{}", jsonResult);

            // 4. 解析JSON为DTO对象
            ResumeDTO resumeDTO = parseJsonToDto(jsonResult);
            resumeDTO.setRawText(rawText); // 保留原始OCR文本用于调试

            log.info("✅ 简历解析成功！姓名: {}, 邮箱: {}, 电话: {}",
                    resumeDTO.getName(),
                    resumeDTO.getEmail(),
                    resumeDTO.getPhone());

            return resumeDTO;

        } catch (IOException e) {
            log.error("❌ 读取简历文件失败: {}", filename, e);
            throw new RuntimeException("读取简历文件失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ 简历解析失败: {}", filename, e);
            throw new RuntimeException("简历解析失败: " + e.getMessage(), e);
        }
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
     * 文本预处理
     */
    private String cleanText(String text) {
        if (text == null) {
            return "";
        }

        return text
                // 去除多余的空白字符
                .replaceAll("\\s+", " ")
                // 去除连续的换行
                .replaceAll("\\n+", "\n")
                // 去除首尾空白
                .trim();
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
            throw new RuntimeException("OCR识别失败: " + e.getMessage(), e);
        }
    }
}

package com.lightcs.component.impl;


import com.baidu.aip.ocr.AipOcr;
import com.lightcs.component.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * 百度云OCR服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor//@RequiredArgsConstructor 的工作原理：这个 Lombok 注解只为 final 字段或 @NonNull 字段生成构造函数
public class BaiduOcrServiceImpl implements OcrService {

    private final AipOcr aipOcrClient;

    @Override
    public String recognizeText(byte[] imageBytes) {
        try {
            // 传入可选参数调用接口
            HashMap<String, String> options = new HashMap<>();
            options.put("language_type", "CHN_ENG");
            options.put("detect_direction", "true");
            options.put("detect_language", "true");
            options.put("probability", "true");

            // 调用百度OCR API识别图片
            JSONObject res = aipOcrClient.basicGeneral(imageBytes, options);
            log.info("OCR识别成功，完整响应: {}", res.toString(2));

            // 提取识别结果中的文字内容
            StringBuilder textBuilder = new StringBuilder();
            if (res.has("words_result")) {
                org.json.JSONArray wordsResult = res.getJSONArray("words_result");
                for (int i = 0; i < wordsResult.length(); i++) {
                    JSONObject wordObj = wordsResult.getJSONObject(i);
                    if (wordObj.has("words")) {
                        textBuilder.append(wordObj.getString("words")).append("\n");
                    }
                }
            }

            String recognizedText = textBuilder.toString().trim();
            log.info("OCR识别完成，提取文字长度: {} 字符", recognizedText.length());
            log.debug("OCR识别结果:\n{}", recognizedText);

            return recognizedText;
        } catch (Exception e) {
            log.error("OCR识别失败", e);
            throw new RuntimeException("OCR识别失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String recognizePdfText(byte[] pdfBytes) {
        // PDF需要先转换为图片，这里使用高精度OCR接口
        // 实际应用中可能需要使用PDF转图片工具（如Apache PDFBox + Java2D）
        // 这里简化处理，直接返回空字符串，实际使用时需要完善
        log.warn("PDF文件需要先转换为图片才能识别，当前方法未实现完整功能");
        return "";
    }


    public static void main(String[] args) {
        System.out.println("========== 使用百度云OCR服务 ==========");
        String image = "E:\\简历\\简历图片.jpg";
        AipOcr client = new AipOcr("appId", "apiKey", "secretKey");
        // 调用百度OCR API识别图片
        JSONObject res = client.basicGeneral(image, new HashMap<String, String>());
        System.out.println(res.toString(2));
    }

}

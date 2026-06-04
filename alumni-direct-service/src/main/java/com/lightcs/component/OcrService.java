package com.lightcs.component;

/**
 * OCR识别服务接口
 */
public interface OcrService {

    /**
     * 识别图片中的文字
     *
     * @param imageBytes 图片字节数组
     * @return 识别出的文本
     */
    String recognizeText(byte[] imageBytes);

    /**
     * 识别PDF文件中的文字（需要先转换为图片）
     *
     * @param pdfBytes PDF字节数组
     * @return 识别出的文本
     */
    String recognizePdfText(byte[] pdfBytes);
}

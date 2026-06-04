package com.lightcs.utils;

import nl.basjes.shaded.org.springframework.util.ResourceUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文档解析工具类（基于 Apache Tika）
 * 支持 PDF、DOC、DOCX、RTF、HTML 等多种格式
 */
public class PdfDocumentUtil {
    private static final String testPdfDocumentPath = "E:\\简历\\2025-2-21.pdf";

    private static final Tika tika = new Tika();

    public static void main(String[] args) {
        System.out.println("========== 使用 Apache Tika 解析文档 ==========");
        String tikaResult = parseWithTika(testPdfDocumentPath);
        System.out.println(tikaResult);

        System.out.println("\n\n========== 使用 Apache Tika 解析（含元数据）==========");
//        String tikaWithMetadata = parseWithTikaWithMetadata(testPdfDocumentPath);
//        System.out.println(tikaWithMetadata);
    }

    /**
     * 使用 Apache Tika 解析文档（支持PDF、DOC、DOCX等多种格式）
     *
     * @param filePath 文件路径
     * @return 解析出的文本内容
     */
    public static String parseWithTika(String filePath) {
        try {
            File file = ResourceUtils.getFile(filePath);
            return tika.parseToString(file);
        } catch (IOException | TikaException e) {
            throw new RuntimeException("Tika解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 Apache Tika 从输入流解析文档
     *
     * @param inputStream 输入流
     * @return 解析出的文本内容
     */
    public static String parseWithTika(InputStream inputStream) {
        try {
            return tika.parseToString(inputStream);
        } catch (IOException | TikaException e) {
            throw new RuntimeException("Tika解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 Apache Tika 解析并获取文档元数据
     *
     * @param filePath 文件路径
     * @return 包含元数据和内容的字符串
     */
//    public static String parseWithTikaWithMetadata(String filePath) {
//        try {
//            File file = ResourceUtils.getFile(filePath);
//            org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
//            String content = tika.parseToString(file, metadata);
//
//            StringBuilder result = new StringBuilder();
//            result.append("=== 文档元数据 ===\n");
//            for (String name : metadata.names()) {
//                result.append(name).append(": ").append(metadata.get(name)).append("\n");
//            }
//            result.append("\n=== 文档内容 ===\n");
//            result.append(content);
//
//            return result.toString();
//        } catch (IOException | TikaException e) {
//            throw new RuntimeException("Tika解析失败: " + e.getMessage(), e);
//        }
//    }

    /**
     * 使用 Apache Tika 从输入流解析并获取文档元数据
     *
     * @param inputStream 输入流
     * @param metadata    元数据对象（输出参数）
     * @return 解析出的文本内容
     */
    public static String parseWithTikaWithMetadata(InputStream inputStream, org.apache.tika.metadata.Metadata metadata) {
        try {
            return tika.parseToString(inputStream, metadata);
        } catch (IOException | TikaException e) {
            throw new RuntimeException("Tika解析失败: " + e.getMessage(), e);
        }
    }
}

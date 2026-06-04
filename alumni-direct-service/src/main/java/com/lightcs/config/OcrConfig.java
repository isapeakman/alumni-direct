package com.lightcs.config;

import com.baidu.aip.ocr.AipOcr;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OCR配置属性类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "baidu.ocr")
public class OcrConfig {

    /**
     * App ID
     */
    private String appId;

    /**
     * API Key
     */
    private String apiKey;

    /**
     * Secret Key
     */
    private String secretKey;

    @Bean
    public AipOcr aipOcrClient() {
        return new AipOcr(appId, apiKey, secretKey);
    }
}

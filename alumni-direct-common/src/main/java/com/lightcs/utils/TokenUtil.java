package com.lightcs.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-07
 * @Description: 令牌工具类
 * @Version: 1.0
 */

public class TokenUtil {
    /**
     * 生成令牌
     *
     * @param byteLength 令牌长度
     * @return
     */
    public static String generateToken(int byteLength) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[byteLength];
        secureRandom.nextBytes(token);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(token);
    }
}

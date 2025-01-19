package com.lightcs.utils;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-08
 * @Description: 用户代理工具类
 * @Version: 1.0
 */

public class UserAgentUtil {
    private static final UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder()
            .hideMatcherLoadStats()
            .withCache(10000)
            .build();

    /**
     * 获取用户代理
     *
     * @return 设备类型:
     * Desktop（桌面设备）
     * Mobile / Phone（移动设备）
     * Tablet（平板设备）
     * Robot（爬虫或机器人）
     */
    public static String getUserAgent() {
        HttpServletRequest request = ServletUtil.getRequest();
        String userAgent = request.getHeader("User-Agent");
        UserAgent agent = uaa.parse(userAgent);
        return agent.getValue("DeviceClass"); // 返回设备类型
    }
}

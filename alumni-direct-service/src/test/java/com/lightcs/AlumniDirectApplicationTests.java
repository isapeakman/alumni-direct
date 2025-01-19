package com.lightcs;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AlumniDirectApplicationTests {
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final UserAgentAnalyzer uaa = UserAgentAnalyzer.newBuilder()
            .hideMatcherLoadStats()
            .withCache(10000)
            .build();

    @Test
    void contextLoads() {
    }

    @Test
    void test() {
        System.out.println(passwordEncoder.encode("123"));
    }

    public static String getDeviceType(String userAgent) {
        UserAgent agent = uaa.parse(userAgent);
        return agent.getValue("DeviceClass"); // 返回设备类型
    }

    @Test
    void test2() {
        String userAgent1 = "Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1";
        String userAgent2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
        String userAgent3 = "Mozilla/5.0 (iPad; CPU OS 14_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1";
        String userAgent4 = "Mozilla/5.0 (Linux; Android 12; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36";

        System.out.println("设备类型 1: " + getDeviceType(userAgent1)); // Mobile
        System.out.println("设备类型 2: " + getDeviceType(userAgent2)); // Desktop
        System.out.println("设备类型 3: " + getDeviceType(userAgent3)); // Tablet
        System.out.println("设备类型 4: " + getDeviceType(userAgent4)); // Tablet
    }

}




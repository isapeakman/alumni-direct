package com.lightcs.controller;

import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-08
 * @Description: 验证码控制层
 * @Version: 1.0
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    /**
     * todo 注册和修改邮箱时生成并发送验证码到新邮箱
     *
     * @return
     */
    @GetMapping("/email")
    public BaseResponse<String> generateCaptcha() {
        // todo 生成验证码
        return ResultBuilder.success("1234");
    }
}

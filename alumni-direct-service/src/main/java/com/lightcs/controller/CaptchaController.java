package com.lightcs.controller;

import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-08
 * @Description: 验证码控制层
 * @Version: 1.0
 */
@Tag(name = "验证码")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    @Autowired
    private VerifyService verifyService;

    @Operation(summary = "发送邮箱验证码")
    @GetMapping("/email")
    public BaseResponse<String> generateCaptcha(String email) {
        verifyService.sendVerifyCode(email);
        return ResultBuilder.success("验证码已发送，请注意查收");
    }
}

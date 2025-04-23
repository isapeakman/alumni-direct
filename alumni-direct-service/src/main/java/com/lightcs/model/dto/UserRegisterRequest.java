package com.lightcs.model.dto;

import lombok.Data;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-23
 * @Description:
 * @Version: 1.0
 */
@Data
public class UserRegisterRequest {
    private String email;
    private String password;
    private String captcha;
}

package com.lightcs.service;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-23
 * @Description:
 * @Version: 1.0
 */

public interface VerifyService {
    /**
     * 发送验证码
     *
     * @param mail 邮箱
     */
    void sendVerifyCode(String mail);

    /**
     * 验证验证码
     *
     * @param mail 邮箱
     * @param code 验证码
     * @return 是否验证成功
     */
    boolean doVerify(String mail, String code);
}

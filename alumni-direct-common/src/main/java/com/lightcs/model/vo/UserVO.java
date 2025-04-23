package com.lightcs.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO {
    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 用户账号，通常为邮箱地址
     */
    private String userAccount;

    /**
     *
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String userAvatar;

    /**
     * 0:admin; 1:user;
     */
    private Integer role;
    /**
     * token
     */
    private String token;
    /**
     * 用户状态 0:正常 1:禁用
     */
    private Integer status;
}

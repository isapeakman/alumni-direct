package com.lightcs.model.vo;

import lombok.Data;

@Data
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
}

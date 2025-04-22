package com.lightcs.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-08
 * @Description:
 * @Version: 1.0
 */
@Data
//@Accessors(chain = true)  // 链式调用
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVO implements Serializable {
        /**
         * 用户Id
         */
        private Integer userId;

        /**
         * 用户账号，通常为邮箱地址
         */
        private String userAccount;


        /**
         * 昵称
         */
        private String nickname;

        /**
         * 头像地址
         */
        private String userAvatar;

        /**
         * 创建时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date createTime;

    /**
     * 0:admin; 1:user;
     */
    private Integer role;
    /**
     * token
     */
    private String token;

    /**
     * 0:男; 1:女; 2未知
     */
    private Integer gender;
    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birth;
    /**
     * 自我介绍
     */
    private String introduction;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

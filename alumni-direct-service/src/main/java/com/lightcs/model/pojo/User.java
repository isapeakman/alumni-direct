package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
//@Accessors(chain = true)  // 链式调用
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
    /**
     * 用户Id
     */
    @TableId(type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户账号，通常为邮箱地址
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     *
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String userAvatar;

    /**
     * 0:admin; 1:user;2:enterprise
     */
    private Integer role;

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

    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 0 未删除 1已删除
     */
    @TableLogic(value = "0", delval = "1")//逻辑删除, 0未删除 1已删除
    private Integer isDelete;
    /**
     * 上次登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;
    /**
     * 0:普通用户 1:校友用户
     */
    private Integer isAlumni;
    /**
     * 0 正常 1 禁用
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
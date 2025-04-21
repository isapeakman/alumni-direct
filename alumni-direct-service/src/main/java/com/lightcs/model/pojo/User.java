package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

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
     *
     */
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
     * 0:普通用户 1:校友用户
     */
    private Integer isAlumni;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
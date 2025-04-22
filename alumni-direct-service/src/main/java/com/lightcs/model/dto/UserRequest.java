package com.lightcs.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-08
 * @Description: 用户请求对象
 * @Version: 1.0
 */
@Data
public class UserRequest {
    private String nickname;
    private String userAvatar;

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
}

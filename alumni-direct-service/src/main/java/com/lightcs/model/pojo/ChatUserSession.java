package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户会话表
 *
 * @TableName chat_user_session
 */
@TableName(value = "chat_user_session")
@Data
public class ChatUserSession implements Serializable {
    /**
     * 发起人ID
     */
    @TableId
    private Integer userId;

    /**
     * 联系人ID
     */
    @TableId
    private Integer contactId;

    /**
     * 会话ID
     */
    private String sessionId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
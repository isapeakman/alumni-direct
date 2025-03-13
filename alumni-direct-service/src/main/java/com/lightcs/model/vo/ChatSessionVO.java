package com.lightcs.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-12
 * @Description:
 * @Version: 1.0
 */
@Data
public class ChatSessionVO {

    /**
     * 联系人ID
     */
    private Integer contactId;
    /**
     * 联系人昵称
     */
    private String contactName;
    /**
     * 联系人头像
     */
    private String contactAvatar;
    /**
     * 会话ID
     */
    private String sessionId;
    /**
     * 最后一条消息
     */
    private String lastMessage;
    /**
     * 最后一次接收消息时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastReceiveTime;
}

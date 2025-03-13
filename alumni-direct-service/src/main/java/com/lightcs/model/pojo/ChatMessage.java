package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName chat_message
 */
@TableName(value = "chat_message")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 消息类型0文本 1文件
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 发送人ID
     */
    private Integer sendUserId;

    /**
     * 发送人昵称
     */
    private String sendUserNickname;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;

    /**
     * 接收人ID
     */
    private Integer contactId;

    /**
     * 文件大小
     */
    private Integer fileSize;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private Integer fileType;

    /**
     * 状态 0正在发送 1 已发送
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
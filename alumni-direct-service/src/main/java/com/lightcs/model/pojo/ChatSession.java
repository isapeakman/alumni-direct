package com.lightcs.model.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 会话
 *
 * @TableName chat_session
 */
@TableName(value = "chat_session")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSession implements Serializable {
    /**
     * 会话ID
     */
    @TableId
    private String sessionId;

    /**
     * 最后的信息
     */
    private String lastMessage;

    /**
     * 上次接收时间
     */
    private Date lastReceiveTime;
    /**
     * 当前会话求职的职位ID
     */
    private Integer jobId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
package com.lightcs.ws;

import lombok.Data;

import java.util.Date;

import static com.lightcs.constants.MessageConstant.MESSAGE_TYPE_TEXT;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description:
 * @Version: 1.0
 */
@Data
public class MessageDTO {
    private Integer fromId;
    private Integer toId;
    private Integer messageType = MESSAGE_TYPE_TEXT;// 0文本 1文件， 默认文本，减少网络传输量
    private String messageContent; // 当messageType为0时，存放文本内容；当messageType为1时，存放文件的url
    private String fileName;
    private Integer fileSize;
    private Date time;
    private Integer status;
}

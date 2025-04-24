package com.lightcs.ws;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-04-24
 * @Description:
 * @Version: 1.0
 */
@Data
public class FileMessage {
    private Integer fromId;
    private Integer messageType;
    private Object messageContent;
    private String fileName;
    private Integer fileSize;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date sendTime;
}

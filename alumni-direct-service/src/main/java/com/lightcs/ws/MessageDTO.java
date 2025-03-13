package com.lightcs.ws;

import lombok.Data;

import java.util.Date;

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
    private String messageContent;
    private Date time;
    private Integer status;
}

package com.lightcs.ws;

import lombok.Data;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description:
 * @Version: 1.0
 */
@Data
public class MessageDTO {
    private String fromId;
    private String toId;
    private String msg;
    private String time;
}

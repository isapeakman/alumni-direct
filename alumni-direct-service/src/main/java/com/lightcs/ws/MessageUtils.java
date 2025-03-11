package com.lightcs.ws;

import com.alibaba.fastjson2.JSON;

import java.util.Date;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description:
 * @Version: 1.0
 */

public class MessageUtils {
    public static String buildMessage(Integer fromId, String message, Date date) {
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setFromId(fromId);
        resultMessage.setMessage(message);
        resultMessage.setTime(date);
        return JSON.toJSONString(resultMessage);
    }
}

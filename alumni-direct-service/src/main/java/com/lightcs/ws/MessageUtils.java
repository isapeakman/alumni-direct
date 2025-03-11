package com.lightcs.ws;

import com.alibaba.fastjson2.JSON;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description:
 * @Version: 1.0
 */

public class MessageUtils {
    public static String buildMessage(String fromId, String message) {
        ResultMessage resultMessage = new ResultMessage();
        resultMessage.setFromId(fromId);
        resultMessage.setMessage(message);
        return JSON.toJSONString(resultMessage);
    }
}

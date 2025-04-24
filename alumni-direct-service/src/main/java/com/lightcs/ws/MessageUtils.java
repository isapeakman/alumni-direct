package com.lightcs.ws;

import com.alibaba.fastjson2.JSON;

import java.util.Date;
import java.util.Objects;

import static com.lightcs.constants.MessageConstant.MESSAGE_TYPE_FILE;
import static com.lightcs.constants.MessageConstant.MESSAGE_TYPE_TEXT;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description:
 * @Version: 1.0
 */

public class MessageUtils {
    public static String buildMessage(Integer fromId, Integer messageType, String message, Date date, String fileName, Integer fileSize) {
        if (Objects.equals(messageType, MESSAGE_TYPE_TEXT)) {// 文本消息
            ResultMessage resultMessage = new ResultMessage();
            resultMessage.setFromId(fromId);
            resultMessage.setMessageType(messageType);
            resultMessage.setMessageContent(message);
            resultMessage.setSendTime(date);
            return JSON.toJSONString(resultMessage);
        } else if (Objects.equals(messageType, MESSAGE_TYPE_FILE)) {// 文件消息
            FileMessage fileMessage = new FileMessage();
            fileMessage.setFromId(fromId);
            fileMessage.setMessageType(messageType);
            fileMessage.setMessageContent(message);
            fileMessage.setFileName(fileName);
            fileMessage.setFileSize(fileSize);
            fileMessage.setSendTime(date);
            return JSON.toJSONString(fileMessage);
        }
        return null;
    }
}

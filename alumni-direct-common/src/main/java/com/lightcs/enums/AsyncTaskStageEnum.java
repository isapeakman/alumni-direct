package com.lightcs.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 异步任务阶段枚举
 */
public enum AsyncTaskStageEnum {

    WAITING("等待处理", "WAITING"),
    OCR_RECOGNITION("正在识别文件内容（OCR）", "OCR_RECOGNITION"),
    GLM_PARSING("正在进行智能解析（GLM）", "GLM_PARSING"),
    COMPLETED("解析完成", "COMPLETED"),
    FAILED("解析失败", "FAILED");

    private final String text;

    private final String value;

    AsyncTaskStageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 阶段值
     * @return 对应的枚举，未找到返回 null
     */
    public static AsyncTaskStageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (AsyncTaskStageEnum anEnum : AsyncTaskStageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}

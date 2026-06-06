package com.lightcs.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 异步任务状态枚举
 */
public enum AsyncTaskStatusEnum {

    PENDING("等待中", "PENDING"),
    PROCESSING("处理中", "PROCESSING"),
    COMPLETED("已完成", "COMPLETED"),
    FAILED("失败", "FAILED");

    private final String text;

    private final String value;

    AsyncTaskStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return 状态值列表
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 状态值
     * @return 对应的枚举，未找到返回 null
     */
    public static AsyncTaskStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (AsyncTaskStatusEnum anEnum : AsyncTaskStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否为终态（完成或失败）
     *
     * @param value 状态值
     * @return true-终态，false-非终态
     */
    public static boolean isFinalState(String value) {
        return COMPLETED.value.equals(value) || FAILED.value.equals(value);
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}

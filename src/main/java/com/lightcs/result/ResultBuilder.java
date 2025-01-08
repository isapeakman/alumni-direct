package com.lightcs.result;

import cn.hutool.http.HttpStatus;

import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;


public class ResultBuilder {
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";

    private ResultBuilder() {
    }

    public static synchronized <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(HttpStatus.HTTP_OK, data, SUCCESS);
    }

    public static synchronized <T> BaseResponse<T> fail(T data) {
        return new BaseResponse<T>(PARAMS_ERROR.getCode(), data, FAIL);
    }
}

package com.lightcs.result;

import cn.hutool.http.HttpStatus;


public class ResultBuilder {
    private static final String SUCCESS = "success";

    private ResultBuilder() {
    }

    public static synchronized <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(HttpStatus.HTTP_OK, data, SUCCESS);
    }
}

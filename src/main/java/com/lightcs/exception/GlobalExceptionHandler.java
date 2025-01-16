package com.lightcs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: peak-like
 * @Description: 全局异常拦截器：Spring会选择最合适的异常拦截器处理对应的异常，最后的Exception作为兜底
 * @DateTime: 2024/11/26 11:14
 **/
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验异常拦截器
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleNotValidException(MethodArgumentNotValidException e) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        map.put("data", Collections.emptyMap());
        map.put("message", e.getBindingResult().getFieldError().getDefaultMessage());
        return map;
    }

    /**
     * 业务异常拦截器
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleBusinessException(Exception e) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (e instanceof BusinessException) {
            map.put("code", ((BusinessException) e).getCode());
        } else {
            map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        map.put("data", Collections.emptyMap());
        map.put("message", e.getMessage()); //todo 后续如果不是自定义异常，可以将异常信息隐藏，返回固定的提示信息
        return map;
    }

}

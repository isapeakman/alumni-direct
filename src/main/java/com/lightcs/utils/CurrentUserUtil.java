package com.lightcs.utils;

import com.lightcs.exception.BusinessException;
import com.lightcs.model.vo.UserVO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.lightcs.enums.ErrorCode.NOT_LOGIN_ERROR;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-07
 * @Description: 当前用户工具类
 * @Version: 1.0
 */

public class CurrentUserUtil {
    /**
     * 获取用户VO信息
     *
     * @return
     */
    public static UserVO getCurrentUserVO() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (UserVO) authentication.getPrincipal();
        }
        throw new BusinessException(NOT_LOGIN_ERROR);
    }

}

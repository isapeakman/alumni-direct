package com.lightcs.constants;

/**
 * @Author: peak-like
 * @CreateTime: 2025-01-08
 * @Description: 用户常量
 * @Version: 1.0
 */
public interface UserConstant {
    /**
     * 默认昵称前缀
     */
    String DEFAULT_NICKNAME_PREFIX = "user_";
    /**
     * 默认头像
     */
    String DEFAULT_USER_AVATAR = "https://k.sinaimg.cn/n/sinakd20118/638/w690h748/20250206/8158-5470af39142257bc3ec3984e498a495f.jpg/w700d1q75cms.jpg";
    String DEFAULT_FILE_DIR = "classpath:static/image/";
    String ADMIN = "ROLE_ADMIN";
    String USER = "ROLE_USER";
    Integer NORMAL = 0;
    Integer BANNED = 1;
}

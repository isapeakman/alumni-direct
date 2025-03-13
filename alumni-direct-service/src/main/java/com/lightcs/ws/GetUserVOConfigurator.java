package com.lightcs.ws;

import com.lightcs.enums.ErrorCode;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.vo.UserVO;
import com.lightcs.utils.ApplicationContextGetBeanUtil;
import com.lightcs.utils.RedisUtil;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import lombok.extern.slf4j.Slf4j;

import static com.lightcs.constants.RedisConstant.TOKEN_PREFIX;

/**
 * @Author: peak-like
 * @CreateTime: 2025-03-11
 * @Description:
 * @Version: 1.0
 */

/**
 * 获取用户信息配置类
 */
@Slf4j
public class GetUserVOConfigurator extends ServerEndpointConfig.Configurator {
    /**
     * 握手时获取用户信息
     *
     * @param sec
     * @param request
     * @param response
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        //由于使用的对象不受spring管理，所以不能使用@Autowired注解注入
        RedisUtil redisUtil = ApplicationContextGetBeanUtil.getBean(RedisUtil.class);
        UserVO userVO = new UserVO();
        userVO.setUserId(6);
        userVO.setNickname("test");
        sec.getUserProperties().put("user", userVO);
        //以下注释代码 前端放入到标准头部后,连接成功后,因未知原因又自动关闭连接
//        if (request.getHeaders().containsKey("sec-websocket-protocol")) {//将令牌从ws定义的标准头部取出
//            // 从请求头中获取token
//            String token = request.getHeaders().get("sec-websocket-protocol").get(0);
//            ThrowUtils.throwIf(token == null, ErrorCode.NOT_LOGIN_ERROR);
//            // 从redis中获取用户信息
//            Object o = redisUtil.get(TOKEN_PREFIX + token);
//            ThrowUtils.throwIf(o == null, ErrorCode.NOT_LOGIN_ERROR, "Token is invalid");
//            UserVO user = (UserVO) o;
//            // 将用户信息存入ServerEndpointConfig
//            sec.getUserProperties().put("user", user);
//        }
        //采用url传参方式
        String queryString = request.getQueryString();
        log.info("queryString:{}", queryString);//形如token=xxx
        if (queryString != null) {
            String[] split = queryString.split("=");
            if (split.length == 2) {
                String token = split[1];//获取token
                Object o = redisUtil.get(TOKEN_PREFIX + token);
                ThrowUtils.throwIf(o == null, ErrorCode.NOT_LOGIN_ERROR, "Token is invalid");
                UserVO user = (UserVO) o;
                sec.getUserProperties().put("user", user);
            }
        }
    }
}

package com.lightcs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.UserMapper;
import com.lightcs.model.pojo.User;
import com.lightcs.model.vo.UserVO;
import com.lightcs.service.UserService;
import com.lightcs.utils.CurrentUserUtil;
import com.lightcs.utils.RedisUtil;
import com.lightcs.utils.TokenUtil;
import com.lightcs.utils.UserAgentUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lightcs.constants.RedisConstant.TOKEN_PREFIX;
import static com.lightcs.constants.UserConstant.DEFAULT_NICKNAME_PREFIX;
import static com.lightcs.enums.ErrorCode.*;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserVO userRegister(String userAccount, String userPassword) {
        // 在有效的邮箱中是否已经注册
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("is_delete", 0);
        Long count = userMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(count > 0, PARAMS_ERROR, "用户已存在");

        // 密码加密
        String encodePassword = passwordEncoder.encode(userPassword);
        // 注册用户
        String nickname = DEFAULT_NICKNAME_PREFIX + RandomUtil.randomNumbers(6);
        User user = User.builder()
                .userAccount(userAccount)
                .userPassword(encodePassword)
                .nickname(nickname)
                .build();
        int res = userMapper.insert(user);
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "注册失败");

        // 返回用户信息
        return UserVO.builder().userId(user.getUserId()).userAccount(userAccount).nickname(nickname).build();
    }

    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken unauthenticated = UsernamePasswordAuthenticationToken.unauthenticated(userAccount, userPassword);
        // 认证
        Authentication authenticate = authenticationManager.authenticate(unauthenticated);
//      SecurityContextHolder.getContext().setAuthentication(authenticate);// 将认证信息存入上下文 , 实际上将用户信息是set到HttpSession中去

        //上述代码执行成功则认证成功，可以将用户信息存入redis

        //查询用户信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("is_delete", 0);
        User user = userMapper.selectOne(queryWrapper);

        // 生成 token
        String token = TokenUtil.generateToken(24);
        // 用户信息存入 redis 并 保留会话状态
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);

        // 删除旧的 token
        String oldToken = (String) redisUtil.get(TOKEN_PREFIX + userVO.getUserId() + ":" + UserAgentUtil.getUserAgent());
        if (StringUtils.isNotBlank(oldToken)) {
            redisUtil.del(TOKEN_PREFIX + oldToken);
        }
        // 存入新的 token
        redisUtil.set(TOKEN_PREFIX + token, userVO);  // token -> userVO
        redisUtil.set(TOKEN_PREFIX + userVO.getUserId() + ":" + UserAgentUtil.getUserAgent(), token);// userId:设备 -> token

        // 返回用户信息 以及 token
        response.setHeader("Authorization", token);
        return userVO;
    }

    @Override
    public boolean userLogout() {
        // 获取当前用户
        UserVO userVO = CurrentUserUtil.getCurrentUserVO();
        // 移除当前登录设备用户的 token
        String deleteToken = (String) redisUtil.get(TOKEN_PREFIX + userVO.getUserId() + ":" + UserAgentUtil.getUserAgent());
        ThrowUtils.throwIf(StringUtils.isBlank(deleteToken), NOT_LOGIN_ERROR, "用户未登录");
        boolean del = redisUtil.del(TOKEN_PREFIX + deleteToken);
        boolean del1 = redisUtil.del(TOKEN_PREFIX + userVO.getUserId() + ":" + UserAgentUtil.getUserAgent());
        return del && del1;
    }

    /**
     * 获取当前最新的用户信息
     *
     * @return User
     */
    private User getCurrentUser() {
        // 用户已登录
        UserVO currentUserVO = CurrentUserUtil.getCurrentUserVO();
        Integer userId = currentUserVO.getUserId();
        //从数据库获取最新用户信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_delete", 0);
        User user = userMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(user == null, NOT_FOUND_ERROR, "用户不存在");
        return user;
    }

    /**
     * 获取当前最新的用户VO信息
     *
     * @return UserVO
     */
    @Override
    public UserVO getCurrentUserVO() {
        User user = getCurrentUser();
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }


}

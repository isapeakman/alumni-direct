package com.lightcs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.UserMapper;
import com.lightcs.model.dto.UserRequest;
import com.lightcs.model.pojo.User;
import com.lightcs.model.vo.UserVO;
import com.lightcs.service.UserService;
import com.lightcs.utils.CurrentUserUtil;
import com.lightcs.utils.RedisUtil;
import com.lightcs.utils.TokenUtil;
import com.lightcs.utils.UserAgentUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static com.lightcs.constants.RedisConstant.TOKEN_PREFIX;
import static com.lightcs.constants.UserConstant.*;
import static com.lightcs.enums.ErrorCode.*;

@Service
@Transactional
@Slf4j
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
                .userAvatar(DEFAULT_USER_AVATAR)
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
        ThrowUtils.throwIf(user == null, NOT_FOUND_ERROR, "用户不存在");

        // 校验是否被禁用，禁用无法登录
        ThrowUtils.throwIf(checkUserIsBanned(user), NOT_FOUND_ERROR, "用户已被禁用");

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
//        response.setHeader("Authorization", token);
        // 更新用户上次的登录时间
        userMapper.updateById(User.builder()
                .userId(user.getUserId())
                .lastLoginTime(new Date())
                .build());

        userVO.setToken(token);
        return userVO;
    }

    @Override
    public void userLogout() {
        // 获取当前用户
        UserVO userVO = CurrentUserUtil.getCurrentUserVO();
        // 移除当前登录设备用户的 token
        String deleteToken = (String) redisUtil.get(TOKEN_PREFIX + userVO.getUserId() + ":" + UserAgentUtil.getUserAgent());
        ThrowUtils.throwIf(StringUtils.isBlank(deleteToken), NOT_LOGIN_ERROR, "用户未登录");
        boolean del = redisUtil.del(TOKEN_PREFIX + deleteToken);
        redisUtil.del(TOKEN_PREFIX + userVO.getUserId() + ":" + UserAgentUtil.getUserAgent());
        ThrowUtils.throwIf(!del, OPERATION_ERROR, "登出失败");
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

    @Override
    public void update(UserRequest userRequest) {
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();


        int count = userMapper.updateById(User.builder()
                .userId(currentUserId)
                .nickname(userRequest.getNickname())
                .birth(userRequest.getBirth())
                .gender(userRequest.getGender())
                .introduction(userRequest.getIntroduction())
                .build()
        );
        ThrowUtils.throwIf(count == 0, OPERATION_ERROR, "更新失败");
    }

    @Override
    public void resetPassword(String oldPassword, String newPassword) {
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        // 获取当前用户
        User user = getCurrentUser();
        // 校验旧密码
        boolean matches = passwordEncoder.matches(oldPassword, user.getUserPassword());
        ThrowUtils.throwIf(!matches, PARAMS_ERROR, "旧密码错误");
        // 更新密码
        String encodePassword = passwordEncoder.encode(newPassword);
        int count = userMapper.updateById(User.builder().userId(currentUserId).userPassword(encodePassword).build());
        ThrowUtils.throwIf(count == 0, OPERATION_ERROR, "更新失败");
        // 删除所有设备的会话
        removeUserSession(currentUserId);
    }

    @Override
    public void modifyAccount(String oldAccount, String newAccount) {
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        // 获取当前用户
        User user = getCurrentUser();
        // 校验旧账号
        ThrowUtils.throwIf(!oldAccount.equals(user.getUserAccount()), PARAMS_ERROR, "旧账号错误");
        // 校验新账号是否已存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_account", newAccount);
        queryWrapper.eq("is_delete", 0);
        Long count = userMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(count > 0, PARAMS_ERROR, "新账号已存在");
        // 更新账号
        int res = userMapper.updateById(User.builder().userId(currentUserId).userAccount(newAccount).build());
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "更新失败");
    }

    @Override
    public void delete() {
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        // 获取当前用户
        User user = getCurrentUser();
        // 删除用户
        int res = userMapper.updateById(User.builder().userId(currentUserId).isDelete(1).build());
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "删除失败");
        // 删除所有设备的会话
//        String token = (String) redisUtil.get(TOKEN_PREFIX + currentUserId + ":" + UserAgentUtil.getUserAgent());
//        redisUtil.del(TOKEN_PREFIX + token);
//        redisUtil.del(TOKEN_PREFIX + currentUserId + ":" + UserAgentUtil.getUserAgent());
        removeUserSession(currentUserId);
    }

    /**
     * 更新头像
     *
     * @param avatar
     */
    @Override
    @Transactional
    public void updateAvatar(String avatar) {

//        String savePath;
//        // 上传头像
//        try {
//            File path = new File(ResourceUtils.getURL("classpath:").getPath());
//            if (!path.exists()) {
//                path = new File("");
//            }
//            File upload = new File(path.getAbsolutePath(), "static/images/" + avatar.getOriginalFilename());
//            if (!upload.exists()) {
//                upload.mkdirs();
//                System.out.println(upload.getAbsolutePath());
//            }
//
//            avatar.transferTo(upload);
//
//            // 5. 构造存储路径（前端访问路径）
//            savePath = "localhost:8080/ad/static/image/" + avatar.getOriginalFilename();
//
//        } catch (IOException e) {
//            log.error("{}", e.getMessage());
//            throw new BusinessException("上传头像失败");
//        }
        // 获取当前用户
        Integer currentUserId = CurrentUserUtil.getCurrentUserId();
        // 更新头像
        int res = userMapper.updateById(User.builder().userId(currentUserId).userAvatar(avatar).build());
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "更新失败");
    }

    @Override
    public void disableUser(Integer userId, Integer status) {
        // 禁用用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        User user = userMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(user == null, NOT_FOUND_ERROR, "用户不存在");
        // 设置用户禁用
        int res = userMapper.updateById(User.builder().userId(userId).status(status).build());
        ThrowUtils.throwIf(res == 0, OPERATION_ERROR, "操作失败");
        //清空禁用用户 会话缓存
        removeUserSession(userId);
    }

    /**
     * 删除所有设备的会话
     *
     * @param currentUserId 当前用户id
     */
    private void removeUserSession(Integer currentUserId) {
        // 获取所有设备的 token
        Set<String> tokenKeys = redisUtil.getKeys(TOKEN_PREFIX + currentUserId + ":*");
        for (String tokenKey : tokenKeys) {
            String token = (String) redisUtil.get(tokenKey);
            redisUtil.del(TOKEN_PREFIX + token);
            redisUtil.del(tokenKey);
        }
    }

    private boolean checkUserIsBanned(User user) {
        return Objects.equals(user.getStatus(), BANNED);
    }

}

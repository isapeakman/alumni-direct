package com.lightcs.provider;

import com.lightcs.exception.ThrowUtils;
import com.lightcs.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;

@Service
@Transactional
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.lightcs.model.pojo.User user = userMapper.selectByUsername(username);
        ThrowUtils.throwIf(user == null, PARAMS_ERROR, "用户或密码错误");
        String userPassword = "{bcrypt}" + user.getUserPassword();// 加密方式为 bcrypt， 添加 {bcrypt} 前缀
//        String userPassword =  "{noop}"+ user.getUserPassword();// 没有加密方式， 添加 {noop} 前缀
        String userRole = user.getRole()==0? "ADMIN" : "USER";    //不以这个开头
        return User.builder()
                .username(user.getUserAccount())
                .password(userPassword)
                .roles(userRole)
                .build();
    }
}

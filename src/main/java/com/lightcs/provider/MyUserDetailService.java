package com.lightcs.provider;

import com.lightcs.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.lightcs.model.pojo.User user = userMapper.selectByUsername(username);
        String userPassword = "{bcrypt}" + user.getUserPassword();// 加密方式为 bcrypt， 添加 {bcrypt} 前缀
//        String userPassword =  "{noop}"+ user.getUserPassword();// 没有加密方式， 添加 {noop} 前缀

        return User.builder()
                .username(user.getUserAccount())
                .password(userPassword).build();         //todo 注意这里判断下是否要添加 ROLE_
    }
}

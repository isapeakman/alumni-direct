package com.lightcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lightcs.model.pojo.User;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {
    @Select("select user_account,user_password,role from user where user_account = #{username} and is_delete = 0")
    User selectByUsername(String username);

    @Select("select user_avatar from user where user_id = #{id}")
    String selectAvatarById(Integer id);
}

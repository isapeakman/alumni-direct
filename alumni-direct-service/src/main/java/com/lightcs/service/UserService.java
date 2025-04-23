package com.lightcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lightcs.model.dto.UserRequest;
import com.lightcs.model.pojo.User;
import com.lightcs.model.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IService<User> {
    UserVO userRegister(String userAccount, String userPassword);

    UserVO userLogin(String userAccount, String userPassword, HttpServletResponse response);

    void userLogout();

    UserVO getCurrentUserVO();

    void update(UserRequest userRequest);

    void resetPassword(String oldPassword, String newPassword);

    void modifyAccount(String oldAccount, String newAccount);

    void delete();

    void updateAvatar(MultipartFile avatar);

    void disableUser(Integer userId, Integer status);
}

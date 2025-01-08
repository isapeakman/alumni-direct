package com.lightcs.controller;

import com.lightcs.model.vo.UserVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //region 用户操作
    @PostMapping("/login")
    public BaseResponse<UserVO> login(String username, String password, HttpServletResponse response) {
        UserVO userVO = userService.userLogin(username, password, response);
        return ResultBuilder.success(userVO);
    }

    @PostMapping("/register")
    public BaseResponse<UserVO> register(String username, String password) {
        UserVO userVO = userService.userRegister(username, password);
        return ResultBuilder.success(userVO);
    }

    @GetMapping("/logout")
    public BaseResponse<String> logout() {
        boolean res = userService.userLogout();
        return ResultBuilder.success(res ? "登出成功" : "登出失败");
    }

    @GetMapping("/current")
    public BaseResponse<UserVO> current() {
        UserVO userVO = userService.getCurrentUserVO();
        return ResultBuilder.success(userVO);
    }
    //endregion
}

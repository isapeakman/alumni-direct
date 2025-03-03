package com.lightcs.controller;

import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.UserRequest;
import com.lightcs.model.vo.UserVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.lightcs.constants.Common.DELETE_SUCCESS;
import static com.lightcs.constants.Common.UPDATE_SUCCESS;
import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;


@Tag(name = "用户操作")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //region 用户操作
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public BaseResponse<UserVO> login(@RequestParam(value = "email") String username, String password, HttpServletResponse response) {
        ThrowUtils.throwIf(StringUtils.isBlank(username) || StringUtils.isBlank(password), PARAMS_ERROR, "用户名或密码不能为空");
        UserVO userVO = userService.userLogin(username, password, response);
        return ResultBuilder.success(userVO);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public BaseResponse<UserVO> register(@RequestParam(value = "email") String username, String password, String captcha) {
        ThrowUtils.throwIf(StringUtils.isBlank(captcha), PARAMS_ERROR, "验证码不能为空");
        // todo 验证码校验

        UserVO userVO = userService.userRegister(username, password);
        return ResultBuilder.success(userVO);
    }

    @Operation(summary = "用户登出")
    @GetMapping("/logout")
    public BaseResponse<String> logout() {
        userService.userLogout();
        return ResultBuilder.success("登出成功");
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public BaseResponse<UserVO> current() {
        UserVO userVO = userService.getCurrentUserVO();
        return ResultBuilder.success(userVO);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/update")
    public BaseResponse<String> update(@RequestBody UserRequest userRequest) {
        if (userRequest == null) {
            return ResultBuilder.fail("参数不能为空");
        }
        if (StringUtils.isBlank(userRequest.getNickname()) && StringUtils.isBlank(userRequest.getUserAvatar())) {
            return ResultBuilder.fail("昵称和头像不能为空");
        }
        userService.update(userRequest);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    @Operation(summary = "重置密码")
    @PutMapping("/reset")
    public BaseResponse<String> resetPassword(String oldPassword, String newPassword) {
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) {
            return ResultBuilder.fail("密码不能为空");
        }
        if (oldPassword.equals(newPassword)) {
            return ResultBuilder.fail("新旧密码不能相同");
        }
        userService.resetPassword(oldPassword, newPassword);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    @Operation(summary = "修改账号")
    @PutMapping("/modifyAccount")
    public BaseResponse<String> modifyAccount(String oldAccount, String newAccount, String captcha) {
        if (StringUtils.isBlank(oldAccount) || StringUtils.isBlank(newAccount)) {
            return ResultBuilder.fail("账号不能为空");
        }
        if (StringUtils.isBlank(captcha)) {
            return ResultBuilder.fail("验证码不能为空");
        }
        // todo 验证码校验

        if (oldAccount.equals(newAccount)) {
            return ResultBuilder.fail("新旧账号不能相同");
        }

        userService.modifyAccount(oldAccount, newAccount);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/del")
    public BaseResponse<String> delete() {
        userService.delete();
        return ResultBuilder.success(DELETE_SUCCESS);
    }
    //endregion
}

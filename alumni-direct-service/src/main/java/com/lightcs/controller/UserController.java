package com.lightcs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lightcs.exception.ThrowUtils;
import com.lightcs.model.dto.UserRegisterRequest;
import com.lightcs.model.dto.UserRequest;
import com.lightcs.model.pojo.User;
import com.lightcs.model.vo.UserVO;
import com.lightcs.result.BaseResponse;
import com.lightcs.result.PaginationBuilder;
import com.lightcs.result.ResultBuilder;
import com.lightcs.service.UserService;
import com.lightcs.service.VerifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.lightcs.constants.Common.DELETE_SUCCESS;
import static com.lightcs.constants.Common.UPDATE_SUCCESS;
import static com.lightcs.enums.ErrorCode.PARAMS_ERROR;


@Tag(name = "用户操作")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private VerifyService verifyService;

    //region 用户操作
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public BaseResponse<UserVO> login(@RequestBody UserRegisterRequest userRegisterRequest, HttpServletResponse response) {
        ThrowUtils.throwIf(userRegisterRequest == null, PARAMS_ERROR, "参数不能为空");
        String username = userRegisterRequest.getEmail();
        String password = userRegisterRequest.getPassword();
        ThrowUtils.throwIf(StringUtils.isBlank(username) || StringUtils.isBlank(password), PARAMS_ERROR, "用户名或密码不能为空");
        UserVO userVO = userService.userLogin(username, password, response);
        return ResultBuilder.success(userVO);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public BaseResponse<UserVO> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, PARAMS_ERROR, "参数不能为空");
        String username = userRegisterRequest.getEmail();
        String password = userRegisterRequest.getPassword();
        String captcha = userRegisterRequest.getCaptcha();
        ThrowUtils.throwIf(StringUtils.isBlank(username) || StringUtils.isBlank(password), PARAMS_ERROR, "用户名或密码不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(captcha), PARAMS_ERROR, "验证码不能为空");
        //  验证码校验
        ThrowUtils.throwIf(!verifyService.doVerify(username, captcha), PARAMS_ERROR, "验证码错误");
        // 注册
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
    @PostMapping("/update")
    public BaseResponse<String> update(@RequestBody UserRequest userRequest) {
        if (userRequest == null) {
            return ResultBuilder.fail("参数不能为空");
        }
        if (StringUtils.isBlank(userRequest.getNickname()) && StringUtils.isBlank(userRequest.getUserAvatar())) {
            return ResultBuilder.fail("昵称和头像不能为空");
        }
        if (StringUtils.isNotBlank(userRequest.getNickname()) && userRequest.getNickname().length() > 20) {
            return ResultBuilder.fail("昵称长度不能超过20");
        }

        userService.update(userRequest);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    @Operation(summary = "重置密码")
    @PostMapping("/reset")
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
    @PostMapping("/modifyAccount")
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
    @PostMapping("/del")
    public BaseResponse<String> delete() {
        userService.delete();
        return ResultBuilder.success(DELETE_SUCCESS);
    }

    @Operation(summary = "修改头像")
    @PostMapping("/update/avatar")
    public BaseResponse<String> updateAvatar(MultipartFile avatar) {
        if (avatar == null) {
            return ResultBuilder.fail("头像不能为空");
        }
        if (avatar.getSize() > 1024 * 1024) {
            return ResultBuilder.fail("头像大小不能超过1M");
        }
        if (!Objects.requireNonNull(avatar.getContentType()).startsWith("image")) {
            return ResultBuilder.fail("文件格式不支持");
        }
        userService.updateAvatar(avatar);
        return ResultBuilder.success(UPDATE_SUCCESS);
    }

    //endregion

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public BaseResponse<Map<String, Object>> list(Integer current, Integer pageSize, String username) {
        current = current == null ? 1 : current;
        pageSize = pageSize == null ? 10 : pageSize;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(true, false, "create_time");
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("user_Account", username);
        }
        Page<User> page = new Page<>(current, pageSize);
        List<User> users = userService.list(page, queryWrapper);
//        List<UserVO> userVOS = new ArrayList<>();
//        users.forEach(user -> {
//            UserVO userVO = new UserVO();
//            userVO.setUserId(user.getUserId());
//            userVO.setUserAccount(user.getUserAccount());
//            userVO.setNickname(user.getNickname());
//            userVO.setUserAvatar(user.getUserAvatar());
//            userVO.setCreateTime(user.getCreateTime());
//            userVO.setRole(user.getRole());
//            userVOS.add(userVO);
//        });
        // 对于管理员 直接返回 用户全部信息
        page.setRecords(users);
        return PaginationBuilder.build(page);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "禁用用户")
    @PostMapping("/disable")
    public BaseResponse<String> disable(Integer userId, Integer status) {
        if (userId == null) {
            return ResultBuilder.fail("用户id不能为空");
        }
        userService.disableUser(userId, status);
        return ResultBuilder.success("禁用成功");
    }

}

package com.coding.controller.auth;

import com.coding.common.Const;
import com.coding.entity.User;
import com.coding.mapper.UserMapper;
import com.coding.service.IUserService;
import com.coding.starter.jwt.config.IJwtService;
import com.coding.utils.HttpKit;
import com.coding.utils.PasswordUtil;
import com.coding.utils.R;
import com.coding.vo.LoginParam;
import com.coding.vo.RegisterParam;
import com.coding.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 认证接口
 *
 * @author roma@guanweiming.com
 * @since 2025-11-02
 */
@Slf4j
@Api(tags = "认证接口")
@RequestMapping(Const.API + "auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final IJwtService jwtService;
    private final IUserService userService;
    private final UserMapper userMapper;

    @ApiOperation("用户注册")
    @PostMapping("register")
    public R<String> register(@RequestBody @Valid RegisterParam param) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectOne(new User().setUsername(param.getUsername()));
        if (existUser != null) {
            return R.createByErrorMessage("用户名已存在");
        }

        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(param, user);
        user.setPassword(PasswordUtil.encode(param.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setDeleted(0);
        user.setStatus(1);

        R<String> result = userService.add(user);
        // 如果添加成功，返回成功信息
        return R.createBySuccess("注册成功");
    }

    @ApiOperation("用户登录")
    @PostMapping("login")
    public R<LoginResult> login(@RequestBody @Valid LoginParam param) {
        // 查询用户
        User user = userMapper.selectOne(new User().setUsername(param.getUsername()));
        if (user == null) {
            return R.createByErrorMessage("用户名或密码错误");
        }

        // 验证密码
        if (!PasswordUtil.matches(param.getPassword(), user.getPassword())) {
            return R.createByErrorMessage("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            return R.createByErrorMessage("账号已被禁用");
        }

        // 生成Token
        String token = jwtService.createToken(String.valueOf(user.getUserId()), "api", 60000000L);

        // 返回结果
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        LoginResult result = new LoginResult();
        result.setToken(token);
        result.setUserInfo(userVO);

        return R.createBySuccess(result);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("info")
    public R<UserVO> getCurrentUser() {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByErrorMessage("未登录");
        }

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return R.createByErrorMessage("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return R.createBySuccess(userVO);
    }

    @ApiOperation("检查用户名是否可用")
    @GetMapping("check-username")
    public R<Boolean> checkUsername(@RequestParam String username) {
        User existUser = userMapper.selectOne(new User().setUsername(username));
        if (existUser != null) {
            return R.createBySuccess(false);
        }
        return R.createBySuccess(true);
    }

    /**
     * 登录结果
     */
    public static class LoginResult {
        private String token;
        private UserVO userInfo;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserVO getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserVO userInfo) {
            this.userInfo = userInfo;
        }
    }
}


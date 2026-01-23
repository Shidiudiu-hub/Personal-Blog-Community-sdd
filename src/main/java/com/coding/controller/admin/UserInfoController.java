package com.coding.controller.admin;

import com.coding.common.Const;
import com.coding.starter.jwt.config.IJwtService;
import com.coding.utils.R;
import com.coding.vo.UcUserLoginParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author roma@guanweiming.com
 * @since 2025-03-16
 */
@Slf4j
@Api(tags = "用户信息表")
@RequestMapping(Const.ADMIN + "user-info")
@RequiredArgsConstructor
@RestController
public class UserInfoController {

    private final IJwtService jwtService;

    @ApiOperation("登录接口")
    @PostMapping("login")
    public R<String> login(@RequestBody @Validated UcUserLoginParam param) {
        if ("admin".equals(param.getLoginName()) && "123456".equals(param.getPassWord())) {
            String tokenVO = jwtService.createToken(String.valueOf(1), "api", 60000000L);
            return R.createBySuccess(tokenVO);
        }
        return R.createByErrorMessage("登录失败");
    }


}

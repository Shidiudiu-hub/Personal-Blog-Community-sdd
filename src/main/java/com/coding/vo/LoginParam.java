package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 登录参数
 *
 * @author roma@guanweiming.com
 * @since 2025-11-02
 */
@Data
public class LoginParam {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 32, message = "密码长度限制6~32")
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}


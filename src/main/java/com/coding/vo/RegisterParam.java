package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 注册参数
 *
 * @author roma@guanweiming.com
 * @since 2025-11-02
 */
@Data
public class RegisterParam {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 1, max = 50, message = "用户名长度限制1~50")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 32, message = "密码长度限制6~32")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @ApiModelProperty(value = "真实姓名", required = true)
    private String realName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty(value = "角色：0-普通用户，1-管理员，默认为0")
    private Integer role;
}


package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class UcUserLoginParam {


    @NotBlank(message = "登录信息不完整")
    @ApiModelProperty("登录账号")
    @Length(min = 1, max = 32, message = "登录账号长度不对")
    private String loginName;

    @Length(min = 6, max = 32, message = "密码长度限制6~32")
    @NotBlank(message = "请输入登录凭证")
    @ApiModelProperty("登录密码")
    private String passWord;
}

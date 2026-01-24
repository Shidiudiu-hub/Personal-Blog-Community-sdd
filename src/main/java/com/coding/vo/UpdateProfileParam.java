package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

/**
 * 更新个人资料参数
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class UpdateProfileParam {

    @Length(max = 50, message = "真实姓名长度不能超过50")
    @ApiModelProperty("真实姓名")
    private String realName;

    @Length(max = 20, message = "手机号长度不能超过20")
    @ApiModelProperty("手机号")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @ApiModelProperty("邮箱")
    private String email;

    @Length(max = 500, message = "头像URL长度不能超过500")
    @ApiModelProperty("头像URL")
    private String avatar;
}

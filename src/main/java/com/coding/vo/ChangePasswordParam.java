package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码参数
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class ChangePasswordParam {

    @NotBlank(message = "原密码不能为空")
    @ApiModelProperty(value = "原密码", required = true)
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Length(min = 6, max = 32, message = "新密码长度限制6~32")
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;
}

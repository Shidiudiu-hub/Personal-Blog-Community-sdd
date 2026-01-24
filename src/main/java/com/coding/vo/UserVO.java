package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户信息VO
 *
 * @author roma@guanweiming.com
 * @since 2025-11-02
 */
@Data
public class UserVO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("角色")
    private Integer role;

    @ApiModelProperty("头像URL")
    private String avatar;

    @ApiModelProperty("状态：1-正常，0-禁用")
    private Integer status;

    @ApiModelProperty("关注数")
    private Integer followCount;

    @ApiModelProperty("粉丝数")
    private Integer fanCount;
}


package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户个人资料 VO
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class UserProfileVO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("个人简介")
    private String bio;

    @ApiModelProperty("关注数")
    private Integer followCount;

    @ApiModelProperty("粉丝数")
    private Integer fanCount;

    @ApiModelProperty("文章数")
    private Integer articleCount;

    @ApiModelProperty("获赞数")
    private Integer likeCount;

    @ApiModelProperty("是否已关注（查看他人主页时）")
    private Boolean followed;

    @ApiModelProperty("注册时间")
    private LocalDateTime createTime;
}

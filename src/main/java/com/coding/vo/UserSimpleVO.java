package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户简要信息 VO
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class UserSimpleVO {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("个人简介")
    private String bio;

    @ApiModelProperty("关注数")
    private Integer followCount;

    @ApiModelProperty("粉丝数")
    private Integer fanCount;

    @ApiModelProperty("文章数")
    private Integer articleCount;

    @ApiModelProperty("是否已关注")
    private Boolean followed;

    @ApiModelProperty("是否互相关注")
    private Boolean mutual;

    @ApiModelProperty("关注时间")
    private LocalDateTime followTime;
}

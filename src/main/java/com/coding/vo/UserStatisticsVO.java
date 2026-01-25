package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户统计数据 VO
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class UserStatisticsVO {

    @ApiModelProperty("文章数")
    private Integer articleCount;

    @ApiModelProperty("总阅读量")
    private Long totalViewCount;

    @ApiModelProperty("获赞数")
    private Integer likeCount;

    @ApiModelProperty("被收藏数")
    private Integer collectCount;

    @ApiModelProperty("评论数")
    private Integer commentCount;

    @ApiModelProperty("关注数")
    private Integer followCount;

    @ApiModelProperty("粉丝数")
    private Integer fanCount;
}

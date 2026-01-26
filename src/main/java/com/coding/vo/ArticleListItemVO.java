package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表项 VO
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class ArticleListItemVO {

    @ApiModelProperty("文章ID")
    private Long articleId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("封面图")
    private String coverImage;

    @ApiModelProperty("作者用户ID")
    private Long authorUserId;

    @ApiModelProperty("作者名")
    private String authorName;

    @ApiModelProperty("作者头像")
    private String authorAvatar;

    @ApiModelProperty("作者角色：0-普通用户，1-管理员")
    private Integer authorRole;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("标签列表")
    private List<TagItem> tags;

    @ApiModelProperty("阅读量")
    private Integer viewCount;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("收藏数")
    private Integer collectCount;

    @ApiModelProperty("评论数")
    private Integer commentCount;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}

package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论 VO
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class CommentVO {

    @ApiModelProperty("评论ID")
    private Long commentId;

    @ApiModelProperty("文章ID")
    private Long articleId;

    @ApiModelProperty("评论用户ID")
    private Long userId;

    @ApiModelProperty("评论用户名")
    private String username;

    @ApiModelProperty("评论用户头像")
    private String avatar;

    @ApiModelProperty("父评论ID")
    private Long parentId;

    @ApiModelProperty("被回复用户ID")
    private Long replyUserId;

    @ApiModelProperty("被回复用户名")
    private String replyUsername;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("回复数")
    private Integer replyCount;

    @ApiModelProperty("是否已点赞")
    private Boolean liked;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("回复列表")
    private List<CommentVO> replies;
}

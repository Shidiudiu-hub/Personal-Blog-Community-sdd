package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发表评论入参
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class CommentAddParam {

    @NotNull(message = "文章ID不能为空")
    @ApiModelProperty(value = "文章ID", required = true)
    private Long articleId;

    @ApiModelProperty("父评论ID（回复时传）")
    private Long parentId;

    @ApiModelProperty("被回复用户ID（回复时传）")
    private Long replyUserId;

    @NotBlank(message = "评论内容不能为空")
    @ApiModelProperty(value = "评论内容", required = true)
    private String content;
}

package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 更新文章入参
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class ArticleUpdateParam {

    @NotNull(message = "文章ID不能为空")
    @ApiModelProperty(value = "文章ID", required = true)
    private Long articleId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容（Markdown）")
    private String content;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("封面图片URL")
    private String coverImage;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("标签ID列表")
    private List<Long> tagIds;

    @ApiModelProperty("状态：0-草稿，1-已发布")
    private Integer status;
}

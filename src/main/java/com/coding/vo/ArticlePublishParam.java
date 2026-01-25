package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 发布文章入参
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class ArticlePublishParam {

    @NotBlank(message = "标题不能为空")
    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @NotBlank(message = "内容不能为空")
    @ApiModelProperty(value = "内容（Markdown）", required = true)
    private String content;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("封面图片URL")
    private String coverImage;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("标签ID列表")
    private List<Long> tagIds;

    /**
     * 状态：0-草稿，1-已发布
     */
    @ApiModelProperty(value = "状态：0-草稿，1-已发布", example = "1")
    private Integer status;
}

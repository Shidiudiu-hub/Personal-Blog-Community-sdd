package com.coding.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 标签项（用于文章列表/详情的标签展示）
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
public class TagItem {

    @ApiModelProperty("标签ID")
    private Long tagId;

    @ApiModelProperty("标签名称")
    private String name;

    @ApiModelProperty("标签颜色")
    private String color;
}

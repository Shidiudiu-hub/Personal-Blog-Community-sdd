package com.coding.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类表
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Table(name = "category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    @Column(name = "name")
    @ApiModelProperty(value = "分类名称")
    private String name;

    @Column(name = "description")
    @ApiModelProperty(value = "分类描述")
    private String description;

    @Column(name = "sort_order")
    @ApiModelProperty(value = "排序序号")
    private Integer sortOrder;

    @Column(name = "article_count")
    @ApiModelProperty(value = "文章数量")
    private Integer articleCount;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "deleted")
    @ApiModelProperty(value = "逻辑删除：0-未删除，1-已删除")
    private Integer deleted;
}

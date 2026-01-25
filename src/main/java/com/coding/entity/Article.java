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
 * 文章表
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Table(name = "article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    @ApiModelProperty(value = "文章ID")
    private Long articleId;

    @Column(name = "user_id")
    @ApiModelProperty(value = "作者ID")
    private Long userId;

    @Column(name = "title")
    @ApiModelProperty(value = "文章标题")
    private String title;

    @Column(name = "content")
    @ApiModelProperty(value = "文章内容（Markdown格式）")
    private String content;

    @Column(name = "summary")
    @ApiModelProperty(value = "文章摘要")
    private String summary;

    @Column(name = "cover_image")
    @ApiModelProperty(value = "封面图片URL")
    private String coverImage;

    @Column(name = "category_id")
    @ApiModelProperty(value = "分类ID")
    private Long categoryId;

    /**
     * 状态：0-草稿，1-已发布，2-已删除
     */
    @Column(name = "status")
    @ApiModelProperty(value = "状态：0-草稿，1-已发布，2-已删除")
    private Integer status;

    @Column(name = "view_count")
    @ApiModelProperty(value = "阅读量")
    private Integer viewCount;

    @Column(name = "like_count")
    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @Column(name = "collect_count")
    @ApiModelProperty(value = "收藏数")
    private Integer collectCount;

    @Column(name = "comment_count")
    @ApiModelProperty(value = "评论数")
    private Integer commentCount;

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

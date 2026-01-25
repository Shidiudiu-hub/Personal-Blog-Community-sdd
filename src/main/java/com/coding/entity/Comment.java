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
 * 评论表
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Table(name = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    @ApiModelProperty(value = "评论ID")
    private Long commentId;

    @Column(name = "article_id")
    @ApiModelProperty(value = "文章ID")
    private Long articleId;

    @Column(name = "user_id")
    @ApiModelProperty(value = "评论用户ID")
    private Long userId;

    @Column(name = "parent_id")
    @ApiModelProperty(value = "父评论ID（0表示顶级评论）")
    private Long parentId;

    @Column(name = "reply_user_id")
    @ApiModelProperty(value = "被回复用户ID")
    private Long replyUserId;

    @Column(name = "content")
    @ApiModelProperty(value = "评论内容")
    private String content;

    @Column(name = "like_count")
    @ApiModelProperty(value = "点赞数")
    private Integer likeCount;

    @Column(name = "reply_count")
    @ApiModelProperty(value = "回复数")
    private Integer replyCount;

    @Column(name = "status")
    @ApiModelProperty(value = "状态：0-正常，1-已删除")
    private Integer status;

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

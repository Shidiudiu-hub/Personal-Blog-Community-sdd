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
 * 标签表
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Table(name = "tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    @ApiModelProperty(value = "标签ID")
    private Long tagId;

    @Column(name = "name")
    @ApiModelProperty(value = "标签名称")
    private String name;

    @Column(name = "color")
    @ApiModelProperty(value = "标签颜色（十六进制）")
    private String color;

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

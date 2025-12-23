package com.coding.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.swagger.annotations.ApiModelProperty;
import tk.mybatis.mapper.annotation.LogicDelete;


/**
* <p>
    * 用户表
    * </p>
*
* @author roma@guanweiming.com
* @since 2025-11-02
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Table(name="user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 用户ID
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
    * 用户名
    */
    @Column(name = "username")
    @ApiModelProperty(value = "用户名")
    private String username;

    /**
    * 密码（加密）
    */
    @Column(name = "password")
    @ApiModelProperty(value = "密码（加密）")
    private String password;

    /**
    * 真实姓名
    */
    @Column(name = "real_name")
    @ApiModelProperty(value = "真实姓名")
    private String realName;

    /**
    * 手机号
    */
    @Column(name = "phone")
    @ApiModelProperty(value = "手机号")
    private String phone;

    /**
    * 邮箱
    */
    @Column(name = "email")
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
    * 角色
    */
    @Column(name = "role")
    @ApiModelProperty(value = "角色")
    private Integer role;

    /**
    * 头像URL
    */
    @Column(name = "avatar")
    @ApiModelProperty(value = "头像URL")
    private String avatar;

    /**
    * 状态：1-正常，0-禁用
    */
    @Column(name = "status")
    @ApiModelProperty(value = "状态：1-正常，0-禁用")
    private Integer status;

    /**
    * 创建时间
    */
    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
    * 更新时间
    */
    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
    * 逻辑删除：0-未删除，1-已删除
    */
    @Column(name = "deleted")
    @ApiModelProperty(value = "逻辑删除：0-未删除，1-已删除")
    private Integer deleted;


}

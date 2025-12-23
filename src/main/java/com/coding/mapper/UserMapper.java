package com.coding.mapper;

import com.coding.entity.User;
import tk.mybatis.mapper.common.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author roma@guanweiming.com
 * @since 2025-11-02
 */
@Repository
public interface UserMapper extends Mapper<User> {

}

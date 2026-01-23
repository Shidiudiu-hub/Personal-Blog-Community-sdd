package com.coding.service;

import com.coding.entity.User;
import com.coding.utils.R;
import com.coding.utils.PageResult;
import com.coding.utils.RequestPage;
import org.springframework.validation.annotation.Validated;

/**
* <p>
    * 用户表 服务类
    * </p>
*
* @author roma@guanweiming.com
* @since 2025-11-02
*/
public interface IUserService{

    PageResult<User> page(Integer role, Integer status, @Validated RequestPage param);

    /**
    * 新增
    */
    R<String> add(User user);

    /**
    * 更新
    */
    R<String> update(User user);

    /**
    * 删除
    */
    R<String> delete(long id);

    /**
    * 根据主键 id 查询
    */
    R<User> findById(long id);
}

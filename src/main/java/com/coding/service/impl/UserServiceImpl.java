package com.coding.service.impl;

import com.coding.entity.User;
import com.coding.mapper.UserMapper;
import com.coding.service.IUserService;
import com.coding.mybatis.utils.DomainUtil;
import com.coding.utils.R;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.coding.utils.RequestPage;
import com.coding.utils.PageResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author roma@guanweiming.com
 * @since 2025-11-02
 */
@Slf4j
@AllArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;

    @Override
    public PageResult<User> page(Integer role, Integer status, @Validated RequestPage param) {
        PageHelper.startPage(param.getPage(),param.getSize(),DomainUtil.getKeyName(User.class) + " desc");

        User query = new User();
        if (role != null) {
            query.setRole(role);
        }
        if (status != null) {
            query.setStatus(status);
        }
        query.setDeleted(0);

        List<User> list = userMapper.select(query);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return PageResult.success(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public R<String> add(User user) {
        if (user == null) {
            return R.createByErrorMessage("添加失败");
        }
        user.setCreateTime(LocalDateTime.now());
        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0) {
            return R.createByErrorMessage("添加失败");
        }
        return R.createBySuccess();
    }

    @Override
    public R<String> delete(long id) {
        int resultCount = userMapper.deleteByPrimaryKey(id);
        if (resultCount == 0) {
            return R.createByErrorMessage("删除失败");
        }
        return R.createBySuccess();
    }

    @Override
    public R<String> update(User user) {
        if (user.getUserId() == null) {
            return R.createByErrorMessage("传参有误，请检查");
        }
        User record = userMapper.selectByPrimaryKey(user.getUserId());
        if (record == null) {
            return R.createByErrorMessage("更新失败，请检查");
        }
        user.setUpdateTime(LocalDateTime.now());
        int resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount == 0) {
            return R.createByErrorMessage("更新失败");
        }
        return R.createBySuccess();
    }

    @Override
    public R<User> findById(long id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            return R.createByErrorMessage("查找失败");
        }
        return R.createBySuccess(user);
    }

}

package com.coding.service.impl;

import com.coding.entity.Category;
import com.coding.mapper.CategoryMapper;
import com.coding.service.ICategoryService;
import com.coding.utils.R;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类服务实现
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@AllArgsConstructor
@Service
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> list() {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("deleted", 0);
        example.orderBy("sortOrder").asc();
        return categoryMapper.selectByExample(example);
    }

    @Override
    public R<String> add(Category category) {
        if (category == null || category.getName() == null || category.getName().trim().isEmpty()) {
            return R.createByErrorMessage("分类名称不能为空");
        }
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        if (category.getDeleted() == null) category.setDeleted(0);
        if (category.getSortOrder() == null) category.setSortOrder(0);
        if (category.getArticleCount() == null) category.setArticleCount(0);
        int cnt = categoryMapper.insertSelective(category);
        if (cnt == 0) {
            return R.createByErrorMessage("添加失败");
        }
        return R.createBySuccess();
    }

    @Override
    public R<String> update(Category category) {
        if (category == null || category.getCategoryId() == null) {
            return R.createByErrorMessage("参数有误");
        }
        Category exist = categoryMapper.selectByPrimaryKey(category.getCategoryId());
        if (exist == null) {
            return R.createByErrorMessage("分类不存在");
        }
        category.setUpdateTime(LocalDateTime.now());
        int cnt = categoryMapper.updateByPrimaryKeySelective(category);
        if (cnt == 0) {
            return R.createBySuccess();
        }
        return R.createBySuccess();
    }

    @Override
    public R<String> delete(Long categoryId) {
        if (categoryId == null) {
            return R.createByErrorMessage("参数有误");
        }
        Category exist = categoryMapper.selectByPrimaryKey(categoryId);
        if (exist == null) {
            return R.createByErrorMessage("分类不存在");
        }
        Category u = new Category();
        u.setCategoryId(categoryId);
        u.setDeleted(1);
        u.setUpdateTime(LocalDateTime.now());
        int cnt = categoryMapper.updateByPrimaryKeySelective(u);
        if (cnt == 0) {
            return R.createByErrorMessage("删除失败");
        }
        return R.createBySuccess();
    }
}

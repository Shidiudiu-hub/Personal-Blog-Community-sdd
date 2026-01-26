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
import java.util.ArrayList;
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

    /**
     * 将现有分类的 sortOrder 归一化为 1..N（按 sortOrder asc, categoryId asc）
     * 解决历史数据中 sortOrder 为空/重复/不连续的问题，保证后续“插入/移动排序”可稳定工作。
     */
    private void normalizeSortOrders() {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("deleted", 0);
        example.orderBy("sortOrder").asc();
        example.orderBy("categoryId").asc();
        List<Category> categories = categoryMapper.selectByExample(example);
        if (categories == null || categories.isEmpty()) {
            return;
        }
        int expected = 1;
        LocalDateTime now = LocalDateTime.now();
        for (Category c : categories) {
            Integer current = c.getSortOrder();
            if (current == null || current != expected) {
                Category u = new Category();
                u.setCategoryId(c.getCategoryId());
                u.setSortOrder(expected);
                u.setUpdateTime(now);
                categoryMapper.updateByPrimaryKeySelective(u);
            }
            expected++;
        }
    }

    /**
     * 获取当前未删除分类数量
     */
    private int countActiveCategories() {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("deleted", 0);
        return categoryMapper.selectCountByExample(example);
    }

    /**
     * 取未删除分类（按 sortOrder asc, categoryId asc）
     */
    private List<Category> listActiveOrderedAsc() {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("deleted", 0);
        example.orderBy("sortOrder").asc();
        example.orderBy("categoryId").asc();
        return categoryMapper.selectByExample(example);
    }

    /**
     * 取未删除分类（按 sortOrder desc, categoryId desc）
     */
    private List<Category> listActiveOrderedDesc() {
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("deleted", 0);
        example.orderBy("sortOrder").desc();
        example.orderBy("categoryId").desc();
        return categoryMapper.selectByExample(example);
    }

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
        // 先把历史数据的 sortOrder 归一化，确保后续“插入排序”不会产生重复
        normalizeSortOrders();

        LocalDateTime now = LocalDateTime.now();
        category.setCreateTime(now);
        category.setUpdateTime(now);
        if (category.getDeleted() == null) category.setDeleted(0);
        if (category.getArticleCount() == null) category.setArticleCount(0);

        // 计算目标排序：默认插入到末尾
        int activeCount = countActiveCategories();
        Integer desired = category.getSortOrder();
        if (desired == null) {
            desired = activeCount + 1;
        }
        // 约束到合法区间 [1, activeCount+1]
        if (desired < 1) desired = 1;
        if (desired > activeCount + 1) desired = activeCount + 1;
        category.setSortOrder(desired);

        // 插入排序：把 [desired, ...] 整体 +1（倒序更新避免冲突）
        List<Category> allDesc = listActiveOrderedDesc();
        if (allDesc != null && !allDesc.isEmpty()) {
            for (Category c : allDesc) {
                Integer so = c.getSortOrder();
                if (so != null && so >= desired) {
                    Category u = new Category();
                    u.setCategoryId(c.getCategoryId());
                    u.setSortOrder(so + 1);
                    u.setUpdateTime(now);
                    categoryMapper.updateByPrimaryKeySelective(u);
                }
            }
        }

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

        // 先归一化，避免历史重复导致“移动排序”仍然重复
        normalizeSortOrders();

        LocalDateTime now = LocalDateTime.now();

        Integer oldOrder = exist.getSortOrder();
        Integer newOrder = category.getSortOrder();
        // 未传 sortOrder 或者未变化：只更新其他字段
        if (newOrder == null || (oldOrder != null && newOrder.equals(oldOrder))) {
            category.setUpdateTime(now);
            int cnt = categoryMapper.updateByPrimaryKeySelective(category);
            if (cnt == 0) {
                return R.createBySuccess();
            }
            return R.createBySuccess();
        }

        // 约束 newOrder 到合法区间 [1, activeCount]
        int activeCount = countActiveCategories();
        if (newOrder < 1) newOrder = 1;
        if (newOrder > activeCount) newOrder = activeCount;

        // 如果 oldOrder 为空（理论上 normalize 后不会为空），给个兜底
        if (oldOrder == null || oldOrder < 1) {
            oldOrder = newOrder;
        }

        if (newOrder < oldOrder) {
            // 向前移动：区间 [newOrder, oldOrder-1] 全部 +1（倒序更新避免冲突）
            List<Category> desc = listActiveOrderedDesc();
            if (desc != null) {
                for (Category c : desc) {
                    if (c.getCategoryId().equals(category.getCategoryId())) continue;
                    Integer so = c.getSortOrder();
                    if (so != null && so >= newOrder && so < oldOrder) {
                        Category u = new Category();
                        u.setCategoryId(c.getCategoryId());
                        u.setSortOrder(so + 1);
                        u.setUpdateTime(now);
                        categoryMapper.updateByPrimaryKeySelective(u);
                    }
                }
            }
        } else if (newOrder > oldOrder) {
            // 向后移动：区间 [oldOrder+1, newOrder] 全部 -1（正序更新避免冲突）
            List<Category> asc = listActiveOrderedAsc();
            if (asc != null) {
                for (Category c : asc) {
                    if (c.getCategoryId().equals(category.getCategoryId())) continue;
                    Integer so = c.getSortOrder();
                    if (so != null && so <= newOrder && so > oldOrder) {
                        Category u = new Category();
                        u.setCategoryId(c.getCategoryId());
                        u.setSortOrder(so - 1);
                        u.setUpdateTime(now);
                        categoryMapper.updateByPrimaryKeySelective(u);
                    }
                }
            }
        }

        // 最后设置当前分类的新排序
        category.setSortOrder(newOrder);
        category.setUpdateTime(now);
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

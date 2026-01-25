package com.coding.service;

import com.coding.entity.Category;
import com.coding.utils.R;

import java.util.List;

/**
 * 分类服务接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
public interface ICategoryService {

    List<Category> list();

    R<String> add(Category category);

    R<String> update(Category category);

    R<String> delete(Long categoryId);
}

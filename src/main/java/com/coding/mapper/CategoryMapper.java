package com.coding.mapper;

import com.coding.entity.Category;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 分类表 Mapper 接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Repository
public interface CategoryMapper extends Mapper<Category> {

}

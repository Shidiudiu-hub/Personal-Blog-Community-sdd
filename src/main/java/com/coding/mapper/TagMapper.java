package com.coding.mapper;

import com.coding.entity.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 标签表 Mapper 接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Repository
public interface TagMapper extends Mapper<Tag> {

    /**
     * 热门标签（按文章数量降序）
     */
    List<Tag> selectHotTags();

    /**
     * 更新标签的文章数（仅统计已发布且未删除的文章）
     */
    void updateArticleCount(@Param("tagId") Long tagId);
}

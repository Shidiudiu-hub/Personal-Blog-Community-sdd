package com.coding.mapper;

import com.coding.entity.ArticleTag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 文章标签关联表 Mapper 接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Repository
public interface ArticleTagMapper extends Mapper<ArticleTag> {

    /**
     * 按文章ID删除关联
     */
    int deleteByArticleId(@Param("articleId") Long articleId);

    /**
     * 按文章ID查询关联的标签
     */
    List<ArticleTag> selectByArticleId(@Param("articleId") Long articleId);
}

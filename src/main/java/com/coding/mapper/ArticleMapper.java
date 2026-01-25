package com.coding.mapper;

import com.coding.entity.Article;
import com.coding.vo.ArticleDetailVO;
import com.coding.vo.ArticleListItemVO;
import com.coding.vo.TagItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 文章表 Mapper 接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Repository
public interface ArticleMapper extends Mapper<Article> {

    /**
     * 文章列表（含作者、分类、标签），用于社区列表与搜索
     */
    List<ArticleListItemVO> selectArticleList(
            @Param("categoryId") Long categoryId,
            @Param("tagId") Long tagId,
            @Param("keyword") String keyword,
            @Param("orderBy") String orderBy);

    /**
     * 文章详情（含作者、分类、标签）
     */
    ArticleDetailVO selectArticleDetail(@Param("articleId") Long articleId);

    /**
     * 按文章ID查询标签列表（供详情/列表 resultMap 的 collection 使用）
     */
    List<TagItem> selectTagsByArticleId(@Param("articleId") Long articleId);

    /**
     * 查询用户的文章列表（包括草稿和已发布）
     */
    List<Article> selectMyArticleList(@Param("userId") Long userId);
}

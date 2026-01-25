package com.coding.mapper;

import com.coding.entity.ArticleLike;
import com.coding.vo.ArticleListItemVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 文章点赞表 Mapper 接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Repository
public interface ArticleLikeMapper extends Mapper<ArticleLike> {

    /**
     * 查询用户是否点赞了某文章
     */
    ArticleLike selectByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 删除点赞记录
     */
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 查询用户点赞的文章列表
     */
    List<ArticleListItemVO> selectLikedArticlesByUserId(@Param("userId") Long userId);
}

package com.coding.mapper;

import com.coding.entity.ArticleCollect;
import com.coding.vo.ArticleListItemVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 文章收藏表 Mapper 接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Repository
public interface ArticleCollectMapper extends Mapper<ArticleCollect> {

    /**
     * 查询用户是否收藏了某文章
     */
    ArticleCollect selectByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 删除收藏记录
     */
    int deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    /**
     * 查询用户收藏的文章列表
     */
    List<ArticleListItemVO> selectCollectedArticlesByUserId(@Param("userId") Long userId);
}

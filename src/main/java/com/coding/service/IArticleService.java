package com.coding.service;

import com.coding.entity.Article;
import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.utils.RequestPage;
import com.coding.vo.ArticleDetailVO;
import com.coding.vo.ArticleListItemVO;
import com.coding.vo.ArticlePublishParam;
import com.coding.vo.ArticleUpdateParam;

/**
 * 文章服务接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
public interface IArticleService {

    /**
     * 发布文章
     */
    R<Long> publish(ArticlePublishParam param, Long userId);

    /**
     * 更新文章
     */
    R<String> update(ArticleUpdateParam param, Long userId);

    /**
     * 删除文章（软删除）
     */
    R<String> delete(Long articleId, Long userId);

    /**
     * 文章详情（草稿仅作者可看；阅读量+1）
     */
    R<ArticleDetailVO> getDetail(Long articleId, Long currentUserId);

    /**
     * 文章列表（社区，仅已发布）
     */
    PageResult<ArticleListItemVO> list(Long categoryId, Long tagId, String keyword, String orderBy, RequestPage param);

    /**
     * 我的文章列表
     */
    PageResult<Article> myList(Long userId, RequestPage param);

    /**
     * 搜索文章（按标题、内容）
     */
    PageResult<ArticleListItemVO> search(String keyword, RequestPage param);
}

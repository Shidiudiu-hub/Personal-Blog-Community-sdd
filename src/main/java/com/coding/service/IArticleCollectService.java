package com.coding.service;

import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.utils.RequestPage;
import com.coding.vo.ArticleListItemVO;

import java.util.Map;

/**
 * 文章收藏服务接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
public interface IArticleCollectService {

    /**
     * 收藏/取消收藏文章
     */
    R<String> collect(Long articleId, Long userId);

    /**
     * 获取收藏状态
     */
    R<Boolean> getCollectStatus(Long articleId, Long userId);

    /**
     * 批量获取收藏状态
     */
    Map<Long, Boolean> batchGetCollectStatus(java.util.List<Long> articleIds, Long userId);

    /**
     * 获取我的收藏列表
     */
    PageResult<ArticleListItemVO> myCollects(Long userId, RequestPage param);
}

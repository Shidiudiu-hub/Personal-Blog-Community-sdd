package com.coding.service;

import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.utils.RequestPage;
import com.coding.vo.ArticleListItemVO;

import java.util.Map;

/**
 * 文章点赞服务接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
public interface IArticleLikeService {

    /**
     * 点赞/取消点赞文章
     */
    R<String> like(Long articleId, Long userId);

    /**
     * 获取点赞状态
     */
    R<Boolean> getLikeStatus(Long articleId, Long userId);

    /**
     * 批量获取点赞状态
     */
    Map<Long, Boolean> batchGetLikeStatus(java.util.List<Long> articleIds, Long userId);

    /**
     * 获取我的点赞列表
     */
    PageResult<ArticleListItemVO> myLikes(Long userId, RequestPage param);
}

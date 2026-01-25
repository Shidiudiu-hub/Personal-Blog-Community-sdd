package com.coding.service;

import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.vo.CommentAddParam;
import com.coding.vo.CommentVO;

import java.util.List;

/**
 * 评论服务接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
public interface ICommentService {

    /**
     * 发表评论
     */
    R<Long> add(CommentAddParam param, Long userId);

    /**
     * 删除评论
     */
    R<String> delete(Long commentId, Long userId);

    /**
     * 获取文章的评论列表（树形结构）
     */
    R<List<CommentVO>> listByArticleId(Long articleId, Long currentUserId);

    /**
     * 获取评论的回复列表
     */
    R<List<CommentVO>> listReplies(Long parentId, Long currentUserId);

    /**
     * 点赞/取消点赞评论
     */
    R<String> like(Long commentId, Long userId);

    /**
     * 获取文章评论数
     */
    R<Integer> countByArticleId(Long articleId);
}

package com.coding.service.impl;

import com.coding.entity.Article;
import com.coding.entity.Comment;
import com.coding.entity.CommentLike;
import com.coding.mapper.ArticleMapper;
import com.coding.mapper.CommentLikeMapper;
import com.coding.mapper.CommentMapper;
import com.coding.service.ICommentService;
import com.coding.utils.R;
import com.coding.vo.CommentAddParam;
import com.coding.vo.CommentVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@AllArgsConstructor
@Service
public class CommentServiceImpl implements ICommentService {

    private final CommentMapper commentMapper;
    private final CommentLikeMapper commentLikeMapper;
    private final ArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Long> add(CommentAddParam param, Long userId) {
        // 检查文章是否存在
        Article article = articleMapper.selectByPrimaryKey(param.getArticleId());
        if (article == null || article.getDeleted() == 1) {
            return R.createByErrorMessage("文章不存在");
        }

        Comment comment = new Comment();
        comment.setArticleId(param.getArticleId());
        comment.setUserId(userId);
        comment.setParentId(param.getParentId() != null ? param.getParentId() : 0L);
        comment.setReplyUserId(param.getReplyUserId());
        comment.setContent(param.getContent());
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setStatus(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setDeleted(0);

        int cnt = commentMapper.insertSelective(comment);
        if (cnt == 0) {
            return R.createByErrorMessage("评论失败");
        }

        // 如果是回复，更新父评论的回复数
        if (param.getParentId() != null && param.getParentId() > 0) {
            commentMapper.updateReplyCount(param.getParentId(), 1);
        }

        // 更新文章评论数
        Article updateArticle = new Article();
        updateArticle.setArticleId(param.getArticleId());
        updateArticle.setCommentCount((article.getCommentCount() == null ? 0 : article.getCommentCount()) + 1);
        articleMapper.updateByPrimaryKeySelective(updateArticle);

        return R.createBySuccess(comment.getCommentId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> delete(Long commentId, Long userId) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if (comment == null) {
            return R.createByErrorMessage("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            return R.createByErrorMessage("无权限删除");
        }

        Comment update = new Comment();
        update.setCommentId(commentId);
        update.setDeleted(1);
        update.setUpdateTime(LocalDateTime.now());
        commentMapper.updateByPrimaryKeySelective(update);

        // 如果是回复，更新父评论的回复数
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            commentMapper.updateReplyCount(comment.getParentId(), -1);
        }

        // 更新文章评论数
        Article article = articleMapper.selectByPrimaryKey(comment.getArticleId());
        if (article != null && article.getCommentCount() != null && article.getCommentCount() > 0) {
            Article updateArticle = new Article();
            updateArticle.setArticleId(comment.getArticleId());
            updateArticle.setCommentCount(article.getCommentCount() - 1);
            articleMapper.updateByPrimaryKeySelective(updateArticle);
        }

        return R.createBySuccess();
    }

    @Override
    public R<List<CommentVO>> listByArticleId(Long articleId, Long currentUserId) {
        List<CommentVO> topComments = commentMapper.selectTopCommentsByArticleId(articleId);
        if (topComments == null || topComments.isEmpty()) {
            return R.createBySuccess(new ArrayList<>());
        }

        // 获取当前用户点赞的评论ID集合
        Set<Long> likedCommentIds = getLikedCommentIds(topComments.stream()
                .map(CommentVO::getCommentId).collect(Collectors.toList()), currentUserId);

        // 为每个顶级评论加载回复并设置点赞状态
        for (CommentVO comment : topComments) {
            comment.setLiked(likedCommentIds.contains(comment.getCommentId()));
            if (comment.getReplyCount() != null && comment.getReplyCount() > 0) {
                List<CommentVO> replies = commentMapper.selectRepliesByParentId(comment.getCommentId());
                if (replies != null) {
                    Set<Long> replyLikedIds = getLikedCommentIds(replies.stream()
                            .map(CommentVO::getCommentId).collect(Collectors.toList()), currentUserId);
                    for (CommentVO reply : replies) {
                        reply.setLiked(replyLikedIds.contains(reply.getCommentId()));
                    }
                    comment.setReplies(replies);
                }
            }
        }

        return R.createBySuccess(topComments);
    }

    @Override
    public R<List<CommentVO>> listReplies(Long parentId, Long currentUserId) {
        List<CommentVO> replies = commentMapper.selectRepliesByParentId(parentId);
        if (replies != null && !replies.isEmpty() && currentUserId != null) {
            Set<Long> likedIds = getLikedCommentIds(replies.stream()
                    .map(CommentVO::getCommentId).collect(Collectors.toList()), currentUserId);
            for (CommentVO reply : replies) {
                reply.setLiked(likedIds.contains(reply.getCommentId()));
            }
        }
        return R.createBySuccess(replies != null ? replies : new ArrayList<>());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> like(Long commentId, Long userId) {
        Comment comment = commentMapper.selectByPrimaryKey(commentId);
        if (comment == null || comment.getDeleted() == 1) {
            return R.createByErrorMessage("评论不存在");
        }

        CommentLike exist = commentLikeMapper.selectByCommentIdAndUserId(commentId, userId);
        if (exist != null) {
            // 取消点赞
            commentLikeMapper.deleteByCommentIdAndUserId(commentId, userId);
            commentMapper.updateLikeCount(commentId, -1);
            return R.createBySuccess("取消点赞成功");
        } else {
            // 点赞
            CommentLike like = new CommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            commentLikeMapper.insertSelective(like);
            commentMapper.updateLikeCount(commentId, 1);
            return R.createBySuccess("点赞成功");
        }
    }

    @Override
    public R<Integer> countByArticleId(Long articleId) {
        Comment query = new Comment();
        query.setArticleId(articleId);
        query.setDeleted(0);
        int count = commentMapper.selectCount(query);
        return R.createBySuccess(count);
    }

    private Set<Long> getLikedCommentIds(List<Long> commentIds, Long userId) {
        if (userId == null || commentIds == null || commentIds.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return commentIds.stream()
                .filter(id -> commentLikeMapper.selectByCommentIdAndUserId(id, userId) != null)
                .collect(Collectors.toSet());
    }
}

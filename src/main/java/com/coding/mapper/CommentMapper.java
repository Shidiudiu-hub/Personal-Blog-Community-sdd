package com.coding.mapper;

import com.coding.entity.Comment;
import com.coding.vo.CommentVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 评论表 Mapper 接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Repository
public interface CommentMapper extends Mapper<Comment> {

    /**
     * 查询文章的顶级评论列表（含用户信息）
     */
    List<CommentVO> selectTopCommentsByArticleId(@Param("articleId") Long articleId);

    /**
     * 查询某评论的回复列表（含用户信息）
     */
    List<CommentVO> selectRepliesByParentId(@Param("parentId") Long parentId);

    /**
     * 更新评论的回复数
     */
    int updateReplyCount(@Param("commentId") Long commentId, @Param("delta") int delta);

    /**
     * 更新评论的点赞数
     */
    int updateLikeCount(@Param("commentId") Long commentId, @Param("delta") int delta);
}

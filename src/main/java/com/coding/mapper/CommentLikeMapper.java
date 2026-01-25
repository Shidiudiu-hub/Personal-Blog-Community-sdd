package com.coding.mapper;

import com.coding.entity.CommentLike;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 评论点赞表 Mapper 接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Repository
public interface CommentLikeMapper extends Mapper<CommentLike> {

    /**
     * 查询用户是否点赞了某评论
     */
    CommentLike selectByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 删除点赞记录
     */
    int deleteByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);
}

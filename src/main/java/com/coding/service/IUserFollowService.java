package com.coding.service;

import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.utils.RequestPage;
import com.coding.vo.UserSimpleVO;

/**
 * 用户关注服务接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
public interface IUserFollowService {

    /**
     * 关注/取消关注用户
     */
    R<String> follow(Long followingId, Long followerId);

    /**
     * 获取关注状态
     */
    R<Boolean> getFollowStatus(Long followingId, Long followerId);

    /**
     * 获取关注列表
     */
    PageResult<UserSimpleVO> followingList(Long userId, Long currentUserId, RequestPage param);

    /**
     * 获取粉丝列表
     */
    PageResult<UserSimpleVO> followerList(Long userId, Long currentUserId, RequestPage param);
}

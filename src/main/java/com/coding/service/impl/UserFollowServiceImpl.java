package com.coding.service.impl;

import com.coding.entity.User;
import com.coding.entity.UserFollow;
import com.coding.mapper.UserFollowMapper;
import com.coding.mapper.UserMapper;
import com.coding.service.IUserFollowService;
import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.utils.RequestPage;
import com.coding.vo.UserSimpleVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
 * 用户关注服务实现
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@AllArgsConstructor
@Service
public class UserFollowServiceImpl implements IUserFollowService {

    private final UserFollowMapper userFollowMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> follow(Long followingId, Long followerId) {
        if (followingId.equals(followerId)) {
            return R.createByErrorMessage("不能关注自己");
        }

        User targetUser = userMapper.selectByPrimaryKey(followingId);
        if (targetUser == null || targetUser.getDeleted() == 1) {
            return R.createByErrorMessage("用户不存在");
        }

        UserFollow exist = userFollowMapper.selectByFollowerAndFollowing(followerId, followingId);
        if (exist != null) {
            // 取消关注
            userFollowMapper.deleteByFollowerAndFollowing(followerId, followingId);
            // 更新关注者的关注数
            User follower = userMapper.selectByPrimaryKey(followerId);
            if (follower != null) {
                User updateFollower = new User();
                updateFollower.setUserId(followerId);
                updateFollower.setFollowCount(Math.max(0, (follower.getFollowCount() == null ? 0 : follower.getFollowCount()) - 1));
                userMapper.updateByPrimaryKeySelective(updateFollower);
            }
            // 更新被关注者的粉丝数
            User updateTarget = new User();
            updateTarget.setUserId(followingId);
            updateTarget.setFanCount(Math.max(0, (targetUser.getFanCount() == null ? 0 : targetUser.getFanCount()) - 1));
            userMapper.updateByPrimaryKeySelective(updateTarget);
            return R.createBySuccess("取消关注成功");
        } else {
            // 关注
            UserFollow follow = new UserFollow();
            follow.setFollowerId(followerId);
            follow.setFollowingId(followingId);
            follow.setCreateTime(LocalDateTime.now());
            userFollowMapper.insertSelective(follow);
            // 更新关注者的关注数
            User follower = userMapper.selectByPrimaryKey(followerId);
            if (follower != null) {
                User updateFollower = new User();
                updateFollower.setUserId(followerId);
                updateFollower.setFollowCount((follower.getFollowCount() == null ? 0 : follower.getFollowCount()) + 1);
                userMapper.updateByPrimaryKeySelective(updateFollower);
            }
            // 更新被关注者的粉丝数
            User updateTarget = new User();
            updateTarget.setUserId(followingId);
            updateTarget.setFanCount((targetUser.getFanCount() == null ? 0 : targetUser.getFanCount()) + 1);
            userMapper.updateByPrimaryKeySelective(updateTarget);
            return R.createBySuccess("关注成功");
        }
    }

    @Override
    public R<Boolean> getFollowStatus(Long followingId, Long followerId) {
        if (followerId == null) {
            return R.createBySuccess(false);
        }
        UserFollow exist = userFollowMapper.selectByFollowerAndFollowing(followerId, followingId);
        return R.createBySuccess(exist != null);
    }

    @Override
    public PageResult<UserSimpleVO> followingList(Long userId, Long currentUserId, RequestPage param) {
        PageHelper.startPage(param.getPage(), param.getSize());
        List<UserSimpleVO> list = userFollowMapper.selectFollowingList(userId);
        PageInfo<UserSimpleVO> pageInfo = new PageInfo<>(list);

        // 设置当前用户是否关注了这些用户
        if (currentUserId != null && list != null && !list.isEmpty()) {
            List<Long> userIds = list.stream().map(UserSimpleVO::getUserId).collect(Collectors.toList());
            List<Long> followingIds = userFollowMapper.selectFollowingIdsByFollowerId(currentUserId, userIds);
            Set<Long> followingSet = followingIds != null ? followingIds.stream().collect(Collectors.toSet()) : java.util.Collections.emptySet();
            for (UserSimpleVO vo : list) {
                vo.setFollowed(followingSet.contains(vo.getUserId()));
                // 检查是否互相关注
                if (vo.getFollowed()) {
                    UserFollow reverse = userFollowMapper.selectByFollowerAndFollowing(vo.getUserId(), currentUserId);
                    vo.setMutual(reverse != null);
                } else {
                    vo.setMutual(false);
                }
            }
        }

        return PageResult.success(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageResult<UserSimpleVO> followerList(Long userId, Long currentUserId, RequestPage param) {
        PageHelper.startPage(param.getPage(), param.getSize());
        List<UserSimpleVO> list = userFollowMapper.selectFollowerList(userId);
        PageInfo<UserSimpleVO> pageInfo = new PageInfo<>(list);

        // 设置当前用户是否关注了这些粉丝
        if (currentUserId != null && list != null && !list.isEmpty()) {
            List<Long> userIds = list.stream().map(UserSimpleVO::getUserId).collect(Collectors.toList());
            List<Long> followingIds = userFollowMapper.selectFollowingIdsByFollowerId(currentUserId, userIds);
            Set<Long> followingSet = followingIds != null ? followingIds.stream().collect(Collectors.toSet()) : java.util.Collections.emptySet();
            for (UserSimpleVO vo : list) {
                vo.setFollowed(followingSet.contains(vo.getUserId()));
                // 检查是否互相关注
                if (vo.getFollowed()) {
                    vo.setMutual(true);
                } else {
                    vo.setMutual(false);
                }
            }
        }

        return PageResult.success(pageInfo.getTotal(), pageInfo.getList());
    }
}

package com.coding.controller;

import com.coding.common.Const;
import com.coding.service.IUserFollowService;
import com.coding.utils.HttpKit;
import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.utils.RequestPage;
import com.coding.vo.UserSimpleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户关注接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@Api(tags = "用户关注")
@RequestMapping(Const.API + "user")
@RequiredArgsConstructor
@RestController
public class FollowController {

    private final IUserFollowService userFollowService;

    @ApiOperation("关注/取消关注用户")
    @PostMapping("follow")
    public R<String> follow(@RequestParam @ApiParam(value = "被关注用户ID", required = true) Long userId) {
        Long currentUserId = HttpKit.getUserId();
        if (currentUserId == null) {
            return R.createByNeedLogin();
        }
        return userFollowService.follow(userId, currentUserId);
    }

    @ApiOperation("获取关注状态")
    @GetMapping("follow-status")
    public R<Boolean> followStatus(@RequestParam @ApiParam(value = "被关注用户ID", required = true) Long userId) {
        return userFollowService.getFollowStatus(userId, HttpKit.getUserId());
    }

    @ApiOperation("获取关注列表")
    @GetMapping("following-list")
    public R<PageResult<UserSimpleVO>> followingList(
            @RequestParam(required = false) @ApiParam("用户ID，不传则查当前用户") Long userId,
            @Validated RequestPage param) {
        Long currentUserId = HttpKit.getUserId();
        Long targetUserId = userId != null ? userId : currentUserId;
        if (targetUserId == null) {
            return R.createByNeedLogin();
        }
        return R.createBySuccess(userFollowService.followingList(targetUserId, currentUserId, param));
    }

    @ApiOperation("获取粉丝列表")
    @GetMapping("follower-list")
    public R<PageResult<UserSimpleVO>> followerList(
            @RequestParam(required = false) @ApiParam("用户ID，不传则查当前用户") Long userId,
            @Validated RequestPage param) {
        Long currentUserId = HttpKit.getUserId();
        Long targetUserId = userId != null ? userId : currentUserId;
        if (targetUserId == null) {
            return R.createByNeedLogin();
        }
        return R.createBySuccess(userFollowService.followerList(targetUserId, currentUserId, param));
    }
}

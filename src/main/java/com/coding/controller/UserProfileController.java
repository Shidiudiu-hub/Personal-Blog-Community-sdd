package com.coding.controller;

import com.coding.common.Const;
import com.coding.entity.Article;
import com.coding.entity.User;
import com.coding.mapper.ArticleMapper;
import com.coding.mapper.UserMapper;
import com.coding.utils.HttpKit;
import com.coding.utils.PasswordUtil;
import com.coding.utils.R;
import com.coding.vo.ChangePasswordParam;
import com.coding.vo.UpdateProfileParam;
import com.coding.vo.UserProfileVO;
import com.coding.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户个人信息接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@Api(tags = "用户个人信息")
@RequestMapping(Const.API + "user")
@RequiredArgsConstructor
@RestController
public class UserProfileController {

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;

    @ApiOperation("获取个人资料")
    @GetMapping("profile")
    public R<UserVO> getProfile() {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByErrorMessage("未登录");
        }

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return R.createByErrorMessage("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return R.createBySuccess(userVO);
    }

    @ApiOperation("更新个人资料")
    @PostMapping("update-profile")
    public R<String> updateProfile(@RequestBody @Valid UpdateProfileParam param) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByErrorMessage("未登录");
        }

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return R.createByErrorMessage("用户不存在");
        }

        // 更新用户信息
        if (param.getRealName() != null) {
            user.setRealName(param.getRealName());
        }
        if (param.getPhone() != null) {
            user.setPhone(param.getPhone());
        }
        if (param.getEmail() != null) {
            user.setEmail(param.getEmail());
        }
        if (param.getAvatar() != null) {
            user.setAvatar(param.getAvatar());
        }
        user.setUpdateTime(LocalDateTime.now());

        int resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount == 0) {
            return R.createByErrorMessage("更新失败");
        }

        return R.createBySuccess("更新成功");
    }

    @ApiOperation("更新头像")
    @PostMapping("update-avatar")
    public R<String> updateAvatar(@RequestParam String avatar) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByErrorMessage("未登录");
        }

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return R.createByErrorMessage("用户不存在");
        }

        user.setAvatar(avatar);
        user.setUpdateTime(LocalDateTime.now());

        int resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount == 0) {
            return R.createByErrorMessage("更新失败");
        }

        return R.createBySuccess("更新成功");
    }

    @ApiOperation("修改密码")
    @PostMapping("change-password")
    public R<String> changePassword(@RequestBody @Valid ChangePasswordParam param) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByErrorMessage("未登录");
        }

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return R.createByErrorMessage("用户不存在");
        }

        // 验证原密码
        if (!PasswordUtil.matches(param.getOldPassword(), user.getPassword())) {
            return R.createByErrorMessage("原密码错误");
        }

        // 更新密码
        user.setPassword(PasswordUtil.encode(param.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());

        int resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount == 0) {
            return R.createByErrorMessage("修改失败");
        }

        return R.createBySuccess("修改成功");
    }

    @ApiOperation("获取用户统计数据")
    @GetMapping("statistics")
    public R<Map<String, Object>> getStatistics(
            @RequestParam(required = false) @ApiParam("用户ID，不传则查当前用户") Long userId) {
        Long targetUserId = userId;
        if (targetUserId == null) {
            targetUserId = HttpKit.getUserId();
        }
        if (targetUserId == null) {
            return R.createByErrorMessage("未登录");
        }

        User user = userMapper.selectByPrimaryKey(targetUserId);
        if (user == null) {
            return R.createByErrorMessage("用户不存在");
        }

        // 统计文章数
        Article articleQuery = new Article();
        articleQuery.setUserId(targetUserId);
        articleQuery.setDeleted(0);
        articleQuery.setStatus(1);
        int articleCount = articleMapper.selectCount(articleQuery);

        // 统计总获赞数（所有文章的点赞数之和）
        // 这里简化处理，实际可以用 SQL SUM 聚合
        int likeCount = 0;
        int collectCount = 0;
        Long totalViewCount = 0L;

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("followCount", user.getFollowCount() != null ? user.getFollowCount() : 0);
        statistics.put("fanCount", user.getFanCount() != null ? user.getFanCount() : 0);
        statistics.put("articleCount", articleCount);
        statistics.put("likeCount", likeCount);
        statistics.put("collectCount", collectCount);
        statistics.put("totalViewCount", totalViewCount);

        return R.createBySuccess(statistics);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("info")
    public R<UserProfileVO> getInfo(
            @RequestParam(required = false) @ApiParam("用户ID，不传则查当前用户") Long userId) {
        Long targetUserId = userId;
        if (targetUserId == null) {
            targetUserId = HttpKit.getUserId();
        }
        if (targetUserId == null) {
            return R.createByErrorMessage("用户不存在");
        }

        User user = userMapper.selectByPrimaryKey(targetUserId);
        if (user == null || user.getDeleted() == 1) {
            return R.createByErrorMessage("用户不存在");
        }

        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setFollowCount(user.getFollowCount() != null ? user.getFollowCount() : 0);
        vo.setFanCount(user.getFanCount() != null ? user.getFanCount() : 0);
        vo.setCreateTime(user.getCreateTime());

        // 统计文章数
        Article articleQuery = new Article();
        articleQuery.setUserId(targetUserId);
        articleQuery.setDeleted(0);
        articleQuery.setStatus(1);
        int articleCount = articleMapper.selectCount(articleQuery);
        vo.setArticleCount(articleCount);

        return R.createBySuccess(vo);
    }
}

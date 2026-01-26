package com.coding.controller;

import com.coding.common.Const;
import com.coding.entity.Tag;
import com.coding.entity.User;
import com.coding.mapper.UserMapper;
import com.coding.service.ITagService;
import com.coding.utils.HttpKit;
import com.coding.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@Api(tags = "标签")
@RequestMapping(Const.API + "tag")
@RequiredArgsConstructor
@RestController
public class TagController {

    private final ITagService tagService;
    private final UserMapper userMapper;

    @ApiOperation("标签列表")
    @GetMapping("list")
    public R<List<Tag>> list() {
        return R.createBySuccess(tagService.list());
    }

    @ApiOperation("热门标签")
    @GetMapping("hot")
    public R<List<Tag>> hot() {
        return R.createBySuccess(tagService.hot());
    }

    @ApiOperation("新增标签")
    @PostMapping("add")
    public R<String> add(@RequestBody Tag tag) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        // 检查是否是管理员
        if (!isAdmin(userId)) {
            return R.createByErrorMessage("无权限操作，需要管理员权限");
        }
        return tagService.add(tag);
    }

    @ApiOperation("更新标签")
    @PostMapping("update")
    public R<String> update(@RequestBody Tag tag) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        // 检查是否是管理员
        if (!isAdmin(userId)) {
            return R.createByErrorMessage("无权限操作，需要管理员权限");
        }
        return tagService.update(tag);
    }

    @ApiOperation("删除标签")
    @PostMapping("delete")
    public R<String> delete(@RequestParam @ApiParam(value = "标签ID", required = true) Long tagId) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        // 检查是否是管理员
        if (!isAdmin(userId)) {
            return R.createByErrorMessage("无权限操作，需要管理员权限");
        }
        return tagService.delete(tagId);
    }

    /**
     * 检查用户是否是管理员
     */
    private boolean isAdmin(Long userId) {
        try {
            User user = userMapper.selectByPrimaryKey(userId);
            return user != null && user.getRole() != null && user.getRole() == 1;
        } catch (Exception e) {
            log.warn("检查管理员权限失败 - userId: {}", userId, e);
            return false;
        }
    }
}

package com.coding.controller;

import com.coding.common.Const;
import com.coding.entity.Tag;
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
        if (HttpKit.getUserId() == null) {
            return R.createByNeedLogin();
        }
        return tagService.add(tag);
    }

    @ApiOperation("更新标签")
    @PostMapping("update")
    public R<String> update(@RequestBody Tag tag) {
        if (HttpKit.getUserId() == null) {
            return R.createByNeedLogin();
        }
        return tagService.update(tag);
    }

    @ApiOperation("删除标签")
    @PostMapping("delete")
    public R<String> delete(@RequestParam @ApiParam(value = "标签ID", required = true) Long tagId) {
        if (HttpKit.getUserId() == null) {
            return R.createByNeedLogin();
        }
        return tagService.delete(tagId);
    }
}

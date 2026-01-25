package com.coding.controller;

import com.coding.common.Const;
import com.coding.entity.Category;
import com.coding.service.ICategoryService;
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
 * 分类接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@Api(tags = "分类")
@RequestMapping(Const.API + "category")
@RequiredArgsConstructor
@RestController
public class CategoryController {

    private final ICategoryService categoryService;

    @ApiOperation("分类列表")
    @GetMapping("list")
    public R<List<Category>> list() {
        return R.createBySuccess(categoryService.list());
    }

    @ApiOperation("新增分类")
    @PostMapping("add")
    public R<String> add(@RequestBody Category category) {
        if (HttpKit.getUserId() == null) {
            return R.createByNeedLogin();
        }
        return categoryService.add(category);
    }

    @ApiOperation("更新分类")
    @PostMapping("update")
    public R<String> update(@RequestBody Category category) {
        if (HttpKit.getUserId() == null) {
            return R.createByNeedLogin();
        }
        return categoryService.update(category);
    }

    @ApiOperation("删除分类")
    @PostMapping("delete")
    public R<String> delete(@RequestParam @ApiParam(value = "分类ID", required = true) Long categoryId) {
        if (HttpKit.getUserId() == null) {
            return R.createByNeedLogin();
        }
        return categoryService.delete(categoryId);
    }
}

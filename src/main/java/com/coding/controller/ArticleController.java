package com.coding.controller;

import com.coding.common.Const;
import com.coding.entity.Article;
import com.coding.service.IArticleService;
import com.coding.utils.HttpKit;
import com.coding.utils.R;
import com.coding.utils.PageResult;
import com.coding.utils.RequestPage;
import com.coding.vo.ArticleDetailVO;
import com.coding.vo.ArticleListItemVO;
import com.coding.vo.ArticlePublishParam;
import com.coding.vo.ArticleUpdateParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 文章接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@Api(tags = "文章")
@RequestMapping(Const.API + "article")
@RequiredArgsConstructor
@RestController
public class ArticleController {

    private final IArticleService articleService;

    @ApiOperation("发布文章")
    @PostMapping("publish")
    public R<Long> publish(@RequestBody @Valid ArticlePublishParam param) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return articleService.publish(param, userId);
    }

    @ApiOperation("更新文章")
    @PostMapping("update")
    public R<String> update(@RequestBody @Valid ArticleUpdateParam param) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return articleService.update(param, userId);
    }

    @ApiOperation("删除文章")
    @PostMapping("delete")
    public R<String> delete(@RequestParam @ApiParam(value = "文章ID", required = true) Long articleId) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return articleService.delete(articleId, userId);
    }

    @ApiOperation("我的文章列表")
    @GetMapping("my-list")
    public R<PageResult<Article>> myList(@Validated RequestPage param) {
        Long userId = HttpKit.getUserId();
        log.info("myList 接口调用 - userId: {}, param: page={}, size={}", userId, param.getPage(), param.getSize());
        if (userId == null) {
            log.warn("myList 用户未登录");
            return R.createByNeedLogin();
        }
        PageResult<Article> result = articleService.myList(userId, param);
        log.info("myList 返回结果: {}", result);
        return R.createBySuccess(result);
    }

    @ApiOperation("文章列表")
    @GetMapping("list")
    public R<PageResult<ArticleListItemVO>> list(
            @RequestParam(required = false) @ApiParam("分类ID") Long categoryId,
            @RequestParam(required = false) @ApiParam("标签ID") Long tagId,
            @RequestParam(required = false) @ApiParam("关键词") String keyword,
            @RequestParam(required = false) @ApiParam("排序：latest-最新,hot-最热,likes-最多点赞") String orderBy,
            @Validated RequestPage param) {
        log.info("list 接口调用 - categoryId: {}, tagId: {}, keyword: {}, orderBy: {}, page: {}, size: {}", 
                categoryId, tagId, keyword, orderBy, param.getPage(), param.getSize());
        PageResult<ArticleListItemVO> result = articleService.list(categoryId, tagId, keyword, orderBy, param);
        log.info("list 返回结果: {}", result);
        return R.createBySuccess(result);
    }

    @ApiOperation("文章详情")
    @GetMapping("detail")
    public R<ArticleDetailVO> detail(@RequestParam @ApiParam(value = "文章ID", required = true) Long articleId) {
        return articleService.getDetail(articleId, HttpKit.getUserId());
    }

    @ApiOperation("搜索文章")
    @GetMapping("search")
    public R<PageResult<ArticleListItemVO>> search(
            @RequestParam @ApiParam(value = "关键词", required = true) String keyword,
            @Validated RequestPage param) {
        return R.createBySuccess(articleService.search(keyword, param));
    }

    @ApiOperation("测试接口 - 检查响应格式")
    @GetMapping("test")
    public R<String> test() {
        log.info("测试接口被调用");
        return R.createBySuccess("测试成功 - 如果你看到这个消息，说明后端正常工作");
    }
}

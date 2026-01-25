package com.coding.controller;

import com.coding.common.Const;
import com.coding.service.IArticleCollectService;
import com.coding.service.IArticleLikeService;
import com.coding.utils.HttpKit;
import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.utils.RequestPage;
import com.coding.vo.ArticleListItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞收藏接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@Api(tags = "点赞收藏")
@RequestMapping(Const.API + "article")
@RequiredArgsConstructor
@RestController
public class InteractionController {

    private final IArticleLikeService articleLikeService;
    private final IArticleCollectService articleCollectService;

    @ApiOperation("点赞/取消点赞文章")
    @PostMapping("like")
    public R<String> like(@RequestParam @ApiParam(value = "文章ID", required = true) Long articleId) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return articleLikeService.like(articleId, userId);
    }

    @ApiOperation("获取文章点赞状态")
    @GetMapping("like-status")
    public R<Boolean> likeStatus(@RequestParam @ApiParam(value = "文章ID", required = true) Long articleId) {
        return articleLikeService.getLikeStatus(articleId, HttpKit.getUserId());
    }

    @ApiOperation("我的点赞列表")
    @GetMapping("my-likes")
    public R<PageResult<ArticleListItemVO>> myLikes(@Validated RequestPage param) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return R.createBySuccess(articleLikeService.myLikes(userId, param));
    }

    @ApiOperation("收藏/取消收藏文章")
    @PostMapping("collect")
    public R<String> collect(@RequestParam @ApiParam(value = "文章ID", required = true) Long articleId) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return articleCollectService.collect(articleId, userId);
    }

    @ApiOperation("获取文章收藏状态")
    @GetMapping("collect-status")
    public R<Boolean> collectStatus(@RequestParam @ApiParam(value = "文章ID", required = true) Long articleId) {
        return articleCollectService.getCollectStatus(articleId, HttpKit.getUserId());
    }

    @ApiOperation("我的收藏列表")
    @GetMapping("my-collects")
    public R<PageResult<ArticleListItemVO>> myCollects(@Validated RequestPage param) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return R.createBySuccess(articleCollectService.myCollects(userId, param));
    }
}

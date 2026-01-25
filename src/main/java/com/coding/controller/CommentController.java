package com.coding.controller;

import com.coding.common.Const;
import com.coding.service.ICommentService;
import com.coding.utils.HttpKit;
import com.coding.utils.R;
import com.coding.vo.CommentAddParam;
import com.coding.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 评论接口
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@Api(tags = "评论")
@RequestMapping(Const.API + "comment")
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final ICommentService commentService;

    @ApiOperation("发表评论")
    @PostMapping("add")
    public R<Long> add(@RequestBody @Valid CommentAddParam param) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return commentService.add(param, userId);
    }

    @ApiOperation("删除评论")
    @PostMapping("delete")
    public R<String> delete(@RequestParam @ApiParam(value = "评论ID", required = true) Long commentId) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return commentService.delete(commentId, userId);
    }

    @ApiOperation("获取文章评论列表")
    @GetMapping("list")
    public R<List<CommentVO>> list(@RequestParam @ApiParam(value = "文章ID", required = true) Long articleId) {
        return commentService.listByArticleId(articleId, HttpKit.getUserId());
    }

    @ApiOperation("获取评论的回复列表")
    @GetMapping("replies")
    public R<List<CommentVO>> replies(@RequestParam @ApiParam(value = "父评论ID", required = true) Long parentId) {
        return commentService.listReplies(parentId, HttpKit.getUserId());
    }

    @ApiOperation("点赞/取消点赞评论")
    @PostMapping("like")
    public R<String> like(@RequestParam @ApiParam(value = "评论ID", required = true) Long commentId) {
        Long userId = HttpKit.getUserId();
        if (userId == null) {
            return R.createByNeedLogin();
        }
        return commentService.like(commentId, userId);
    }

    @ApiOperation("获取文章评论数")
    @GetMapping("count")
    public R<Integer> count(@RequestParam @ApiParam(value = "文章ID", required = true) Long articleId) {
        return commentService.countByArticleId(articleId);
    }
}

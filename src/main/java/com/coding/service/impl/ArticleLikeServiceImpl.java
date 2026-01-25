package com.coding.service.impl;

import com.coding.entity.Article;
import com.coding.entity.ArticleLike;
import com.coding.mapper.ArticleLikeMapper;
import com.coding.mapper.ArticleMapper;
import com.coding.service.IArticleLikeService;
import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.utils.RequestPage;
import com.coding.vo.ArticleListItemVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章点赞服务实现
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@AllArgsConstructor
@Service
public class ArticleLikeServiceImpl implements IArticleLikeService {

    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> like(Long articleId, Long userId) {
        Article article = articleMapper.selectByPrimaryKey(articleId);
        if (article == null || article.getDeleted() == 1) {
            return R.createByErrorMessage("文章不存在");
        }

        ArticleLike exist = articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
        if (exist != null) {
            // 取消点赞
            articleLikeMapper.deleteByArticleIdAndUserId(articleId, userId);
            // 更新文章点赞数
            Article update = new Article();
            update.setArticleId(articleId);
            update.setLikeCount(Math.max(0, (article.getLikeCount() == null ? 0 : article.getLikeCount()) - 1));
            articleMapper.updateByPrimaryKeySelective(update);
            return R.createBySuccess("取消点赞成功");
        } else {
            // 点赞
            ArticleLike like = new ArticleLike();
            like.setArticleId(articleId);
            like.setUserId(userId);
            like.setCreateTime(LocalDateTime.now());
            articleLikeMapper.insertSelective(like);
            // 更新文章点赞数
            Article update = new Article();
            update.setArticleId(articleId);
            update.setLikeCount((article.getLikeCount() == null ? 0 : article.getLikeCount()) + 1);
            articleMapper.updateByPrimaryKeySelective(update);
            return R.createBySuccess("点赞成功");
        }
    }

    @Override
    public R<Boolean> getLikeStatus(Long articleId, Long userId) {
        if (userId == null) {
            return R.createBySuccess(false);
        }
        ArticleLike exist = articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
        return R.createBySuccess(exist != null);
    }

    @Override
    public Map<Long, Boolean> batchGetLikeStatus(List<Long> articleIds, Long userId) {
        Map<Long, Boolean> result = new HashMap<>();
        if (userId == null || articleIds == null || articleIds.isEmpty()) {
            return result;
        }
        for (Long articleId : articleIds) {
            ArticleLike exist = articleLikeMapper.selectByArticleIdAndUserId(articleId, userId);
            result.put(articleId, exist != null);
        }
        return result;
    }

    @Override
    public PageResult<ArticleListItemVO> myLikes(Long userId, RequestPage param) {
        PageHelper.startPage(param.getPage(), param.getSize());
        List<ArticleListItemVO> list = articleLikeMapper.selectLikedArticlesByUserId(userId);
        PageInfo<ArticleListItemVO> pageInfo = new PageInfo<>(list);
        return PageResult.success(pageInfo.getTotal(), pageInfo.getList());
    }
}

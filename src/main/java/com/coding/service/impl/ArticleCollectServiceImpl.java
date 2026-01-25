package com.coding.service.impl;

import com.coding.entity.Article;
import com.coding.entity.ArticleCollect;
import com.coding.mapper.ArticleCollectMapper;
import com.coding.mapper.ArticleMapper;
import com.coding.service.IArticleCollectService;
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
 * 文章收藏服务实现
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@AllArgsConstructor
@Service
public class ArticleCollectServiceImpl implements IArticleCollectService {

    private final ArticleCollectMapper articleCollectMapper;
    private final ArticleMapper articleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> collect(Long articleId, Long userId) {
        Article article = articleMapper.selectByPrimaryKey(articleId);
        if (article == null || article.getDeleted() == 1) {
            return R.createByErrorMessage("文章不存在");
        }

        ArticleCollect exist = articleCollectMapper.selectByArticleIdAndUserId(articleId, userId);
        if (exist != null) {
            // 取消收藏
            articleCollectMapper.deleteByArticleIdAndUserId(articleId, userId);
            // 更新文章收藏数
            Article update = new Article();
            update.setArticleId(articleId);
            update.setCollectCount(Math.max(0, (article.getCollectCount() == null ? 0 : article.getCollectCount()) - 1));
            articleMapper.updateByPrimaryKeySelective(update);
            return R.createBySuccess("取消收藏成功");
        } else {
            // 收藏
            ArticleCollect collect = new ArticleCollect();
            collect.setArticleId(articleId);
            collect.setUserId(userId);
            collect.setCreateTime(LocalDateTime.now());
            articleCollectMapper.insertSelective(collect);
            // 更新文章收藏数
            Article update = new Article();
            update.setArticleId(articleId);
            update.setCollectCount((article.getCollectCount() == null ? 0 : article.getCollectCount()) + 1);
            articleMapper.updateByPrimaryKeySelective(update);
            return R.createBySuccess("收藏成功");
        }
    }

    @Override
    public R<Boolean> getCollectStatus(Long articleId, Long userId) {
        if (userId == null) {
            return R.createBySuccess(false);
        }
        ArticleCollect exist = articleCollectMapper.selectByArticleIdAndUserId(articleId, userId);
        return R.createBySuccess(exist != null);
    }

    @Override
    public Map<Long, Boolean> batchGetCollectStatus(List<Long> articleIds, Long userId) {
        Map<Long, Boolean> result = new HashMap<>();
        if (userId == null || articleIds == null || articleIds.isEmpty()) {
            return result;
        }
        for (Long articleId : articleIds) {
            ArticleCollect exist = articleCollectMapper.selectByArticleIdAndUserId(articleId, userId);
            result.put(articleId, exist != null);
        }
        return result;
    }

    @Override
    public PageResult<ArticleListItemVO> myCollects(Long userId, RequestPage param) {
        PageHelper.startPage(param.getPage(), param.getSize());
        List<ArticleListItemVO> list = articleCollectMapper.selectCollectedArticlesByUserId(userId);
        PageInfo<ArticleListItemVO> pageInfo = new PageInfo<>(list);
        return PageResult.success(pageInfo.getTotal(), pageInfo.getList());
    }
}

package com.coding.service.impl;

import com.coding.entity.Article;
import com.coding.entity.ArticleTag;
import com.coding.mapper.ArticleMapper;
import com.coding.mapper.ArticleTagMapper;
import com.coding.mapper.CategoryMapper;
import com.coding.mapper.TagMapper;
import com.coding.service.IArticleService;
import com.coding.utils.PageResult;
import com.coding.utils.R;
import com.coding.utils.RequestPage;
import com.coding.vo.ArticleDetailVO;
import com.coding.vo.ArticleListItemVO;
import com.coding.vo.ArticlePublishParam;
import com.coding.vo.ArticleUpdateParam;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章服务实现
 *
 * @author roma@guanweiming.com
 * @since 2026-01-23
 */
@Slf4j
@AllArgsConstructor
@Service
public class ArticleServiceImpl implements IArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Long> publish(ArticlePublishParam param, Long userId) {
        Article article = new Article();
        article.setUserId(userId);
        article.setTitle(param.getTitle());
        article.setContent(param.getContent());
        article.setSummary(param.getSummary());
        article.setCoverImage(param.getCoverImage());
        article.setCategoryId(param.getCategoryId());
        article.setStatus(param.getStatus() != null ? param.getStatus() : 1);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCollectCount(0);
        article.setCommentCount(0);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        article.setDeleted(0);

        int cnt = articleMapper.insertSelective(article);
        if (cnt == 0) {
            return R.createByErrorMessage("发布失败");
        }
        Long articleId = article.getArticleId();
        if (articleId == null) {
            return R.createByErrorMessage("发布失败");
        }

        if (param.getTagIds() != null && !param.getTagIds().isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long tagId : param.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(articleId);
                at.setTagId(tagId);
                at.setCreateTime(now);
                articleTagMapper.insertSelective(at);
            }
        }
        
        // 更新分类和标签的文章数（仅当文章已发布时）
        if (article.getStatus() != null && article.getStatus() == 1) {
            if (article.getCategoryId() != null) {
                categoryMapper.updateArticleCount(article.getCategoryId());
            }
            if (param.getTagIds() != null && !param.getTagIds().isEmpty()) {
                for (Long tagId : param.getTagIds()) {
                    tagMapper.updateArticleCount(tagId);
                }
            }
        }
        
        return R.createBySuccess(articleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> update(ArticleUpdateParam param, Long userId) {
        Article exist = articleMapper.selectByPrimaryKey(param.getArticleId());
        if (exist == null) {
            return R.createByErrorMessage("文章不存在");
        }
        if (!exist.getUserId().equals(userId)) {
            return R.createByErrorMessage("无权限修改");
        }

        Article article = new Article();
        article.setArticleId(param.getArticleId());
        if (param.getTitle() != null) article.setTitle(param.getTitle());
        if (param.getContent() != null) article.setContent(param.getContent());
        if (param.getSummary() != null) article.setSummary(param.getSummary());
        if (param.getCoverImage() != null) article.setCoverImage(param.getCoverImage());
        if (param.getCategoryId() != null) article.setCategoryId(param.getCategoryId());
        if (param.getStatus() != null) article.setStatus(param.getStatus());
        article.setUpdateTime(LocalDateTime.now());

        int cnt = articleMapper.updateByPrimaryKeySelective(article);
        if (cnt == 0) {
            return R.createBySuccess();
        }

        // 记录旧的分类ID和标签ID，用于更新文章数
        Long oldCategoryId = exist.getCategoryId();
        List<ArticleTag> oldTags = articleTagMapper.selectByArticleId(param.getArticleId());
        List<Long> oldTagIds = oldTags != null ? oldTags.stream()
                .map(ArticleTag::getTagId)
                .distinct()
                .collect(java.util.stream.Collectors.toList()) : new ArrayList<>();
        
        articleTagMapper.deleteByArticleId(param.getArticleId());
        if (param.getTagIds() != null && !param.getTagIds().isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (Long tagId : param.getTagIds()) {
                ArticleTag at = new ArticleTag();
                at.setArticleId(param.getArticleId());
                at.setTagId(tagId);
                at.setCreateTime(now);
                articleTagMapper.insertSelective(at);
            }
        }
        
        // 更新分类和标签的文章数
        // 获取更新后的文章状态
        Article updatedArticle = articleMapper.selectByPrimaryKey(param.getArticleId());
        Integer newStatus = updatedArticle != null ? updatedArticle.getStatus() : exist.getStatus();
        Long newCategoryId = param.getCategoryId() != null ? param.getCategoryId() : exist.getCategoryId();
        List<Long> newTagIds = param.getTagIds() != null ? param.getTagIds() : oldTagIds;
        
        // 如果文章已发布，更新相关分类和标签的文章数
        if (newStatus != null && newStatus == 1) {
            // 更新旧分类的文章数（如果分类改变了）
            if (oldCategoryId != null && !oldCategoryId.equals(newCategoryId)) {
                categoryMapper.updateArticleCount(oldCategoryId);
            }
            // 更新新分类的文章数
            if (newCategoryId != null) {
                categoryMapper.updateArticleCount(newCategoryId);
            }
            
            // 更新旧标签的文章数（如果标签改变了）
            for (Long oldTagId : oldTagIds) {
                if (!newTagIds.contains(oldTagId)) {
                    tagMapper.updateArticleCount(oldTagId);
                }
            }
            // 更新新标签的文章数
            for (Long newTagId : newTagIds) {
                tagMapper.updateArticleCount(newTagId);
            }
        } else {
            // 如果文章不是已发布状态，也需要更新旧分类和标签的文章数（因为可能从已发布变为草稿）
            if (exist.getStatus() != null && exist.getStatus() == 1) {
                if (oldCategoryId != null) {
                    categoryMapper.updateArticleCount(oldCategoryId);
                }
                for (Long oldTagId : oldTagIds) {
                    tagMapper.updateArticleCount(oldTagId);
                }
            }
        }
        
        return R.createBySuccess();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> delete(Long articleId, Long userId) {
        Article exist = articleMapper.selectByPrimaryKey(articleId);
        if (exist == null) {
            return R.createByErrorMessage("文章不存在");
        }
        if (!exist.getUserId().equals(userId)) {
            return R.createByErrorMessage("无权限删除");
        }
        
        // 记录删除前的分类ID和标签ID，用于更新文章数
        Long categoryId = exist.getCategoryId();
        List<ArticleTag> tags = articleTagMapper.selectByArticleId(articleId);
        List<Long> tagIds = tags != null ? tags.stream()
                .map(ArticleTag::getTagId)
                .distinct()
                .collect(java.util.stream.Collectors.toList()) : new ArrayList<>();
        
        Article u = new Article();
        u.setArticleId(articleId);
        u.setDeleted(1);
        u.setUpdateTime(LocalDateTime.now());
        int cnt = articleMapper.updateByPrimaryKeySelective(u);
        if (cnt == 0) {
            return R.createByErrorMessage("删除失败");
        }
        
        // 如果文章是已发布状态，更新分类和标签的文章数
        if (exist.getStatus() != null && exist.getStatus() == 1) {
            if (categoryId != null) {
                categoryMapper.updateArticleCount(categoryId);
            }
            for (Long tagId : tagIds) {
                tagMapper.updateArticleCount(tagId);
            }
        }
        
        return R.createBySuccess();
    }

    @Override
    public R<ArticleDetailVO> getDetail(Long articleId, Long currentUserId) {
        log.info("getDetail 调用 - articleId: {}, currentUserId: {}", articleId, currentUserId);
        Article article = articleMapper.selectByPrimaryKey(articleId);
        if (article == null) {
            log.warn("文章不存在 - articleId: {}", articleId);
            return R.createByErrorMessage("文章不存在");
        }
        // 检查是否已删除
        if (article.getDeleted() != null && article.getDeleted() == 1) {
            log.warn("文章已删除 - articleId: {}", articleId);
            return R.createByErrorMessage("文章已删除");
        }
        
        log.info("文章信息 - articleId: {}, userId: {}, status: {} (类型: {}), deleted: {}", 
                articleId, article.getUserId(), article.getStatus(), 
                article.getStatus() != null ? article.getStatus().getClass().getName() : "null",
                article.getDeleted());
        
        // 检查草稿权限：草稿只能作者查看
        // 兼容 Integer 和 int 类型
        Integer status = article.getStatus();
        boolean isDraft = status != null && (status == 0 || status.equals(0));
        log.info("草稿判断 - status: {}, isDraft: {}", status, isDraft);
        
        if (isDraft) {
            // 草稿状态
            log.info("检测到草稿 - articleId: {}, articleUserId: {}, currentUserId: {}", 
                    articleId, article.getUserId(), currentUserId);
            if (currentUserId == null) {
                log.warn("草稿权限检查失败 - currentUserId 为 null");
                return R.createByErrorMessage("无权限查看该草稿");
            }
            // 确保用户ID类型一致（都转为Long比较）
            Long articleUserId = article.getUserId();
            if (articleUserId == null) {
                log.warn("草稿权限检查失败 - articleUserId 为 null");
                return R.createByErrorMessage("无权限查看该草稿");
            }
            // 使用 equals 比较，确保类型一致
            if (!articleUserId.equals(currentUserId)) {
                log.warn("草稿权限检查失败 - articleUserId: {} (类型: {}), currentUserId: {} (类型: {})", 
                        articleUserId, articleUserId.getClass().getName(), 
                        currentUserId, currentUserId.getClass().getName());
                return R.createByErrorMessage("无权限查看该草稿");
            }
            log.info("草稿权限检查通过");
        }

        // 只有已发布的文章才增加阅读量（草稿不增加）
        if (article.getStatus() != null && article.getStatus() == 1) {
            Article inc = new Article();
            inc.setArticleId(articleId);
            inc.setViewCount((article.getViewCount() == null ? 0 : article.getViewCount()) + 1);
            inc.setUpdateTime(LocalDateTime.now());
            articleMapper.updateByPrimaryKeySelective(inc);
        }

        // 查询文章详情（包括草稿）
        ArticleDetailVO vo = articleMapper.selectArticleDetail(articleId);
        if (vo == null) {
            log.warn("文章详情查询返回null - articleId: {}, status: {}, deleted: {}", 
                    articleId, article.getStatus(), article.getDeleted());
            return R.createByErrorMessage("文章不存在或已删除");
        }
        return R.createBySuccess(vo);
    }

    @Override
    public PageResult<ArticleListItemVO> list(Long categoryId, Long tagId, String keyword, String orderBy, RequestPage param) {
        PageHelper.startPage(param.getPage(), param.getSize());
        List<ArticleListItemVO> list = articleMapper.selectArticleList(categoryId, tagId, keyword, orderBy);
        PageInfo<ArticleListItemVO> pageInfo = new PageInfo<>(list);
        return PageResult.success(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageResult<Article> myList(Long userId, RequestPage param) {
        log.info("myList 查询参数 - userId: {}, page: {}, size: {}", userId, param.getPage(), param.getSize());
        // 如果 userId 为 null，返回空列表
        if (userId == null) {
            log.warn("myList userId 为空，返回空列表");
            return PageResult.success(0L, new ArrayList<>());
        }
        // 使用自定义 SQL 查询用户的文章列表
        PageHelper.startPage(param.getPage(), param.getSize());
        List<Article> list = articleMapper.selectMyArticleList(userId);
        log.info("myList 查询结果 - userId: {}, 查询到 {} 条文章", userId, list.size());
        PageInfo<Article> pageInfo = new PageInfo<>(list);
        return PageResult.success(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageResult<ArticleListItemVO> search(String keyword, RequestPage param) {
        return list(null, null, keyword, "latest", param);
    }
}

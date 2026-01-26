<template>
  <div class="article-detail">
    <el-card v-loading="loading">
      <template v-if="article">
        <div class="header">
          <h1 class="title">{{ article.title }}</h1>
          <div class="meta">
            <div class="author-info" @click="goUserProfile(article.authorUserId)">
              <el-avatar :size="32" :src="article.authorAvatar">{{ article.authorName?.charAt(0) }}</el-avatar>
              <span class="author">{{ article.authorName }}</span>
              <el-tag v-if="article.authorRole === 1" size="small" type="warning" style="margin-left: 6px">管理员</el-tag>
            </div>
            <span class="time">{{ formatTime(article.createTime) }}</span>
            <span class="counts">
              阅读 {{ article.viewCount || 0 }} · 点赞 {{ article.likeCount || 0 }} · 评论 {{ article.commentCount || 0 }}
            </span>
          </div>
          <div class="tags">
            <el-tag v-if="article.categoryName" size="small" type="info">{{ article.categoryName }}</el-tag>
            <el-tag
              v-for="t in (article.tags || [])"
              :key="t.tagId"
              size="small"
              :style="{ marginLeft: '8px', color: t.color || '#409EFF', borderColor: t.color || '#409EFF' }"
            >
              {{ t.name }}
            </el-tag>
          </div>
        </div>
        <div v-if="article.coverImage" class="cover">
          <img :src="article.coverImage" :alt="article.title" />
        </div>
        <div class="content markdown-body" v-html="renderedContent" />

        <!-- 操作栏 -->
        <div class="interaction-bar">
          <el-button 
            :type="liked ? 'primary' : 'default'" 
            :icon="Star"
            @click="handleLike"
            :loading="likeLoading"
          >
            {{ liked ? '已点赞' : '点赞' }} {{ article.likeCount || 0 }}
          </el-button>
          <el-button 
            :type="collected ? 'warning' : 'default'" 
            :icon="StarFilled"
            @click="handleCollect"
            :loading="collectLoading"
          >
            {{ collected ? '已收藏' : '收藏' }} {{ article.collectCount || 0 }}
          </el-button>
          <el-button @click="goBack">返回</el-button>
          <el-button v-if="canEdit" type="primary" @click="goEdit">编辑</el-button>
        </div>

        <!-- 评论区 -->
        <div class="comment-section">
          <h3>评论 ({{ article.commentCount || 0 }})</h3>
          
          <!-- 评论输入框 -->
          <div class="comment-input">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              placeholder="写下你的评论..."
              maxlength="500"
              show-word-limit
            />
            <div class="comment-submit">
              <el-button type="primary" @click="submitComment" :loading="submitting" :disabled="!commentContent.trim()">
                发表评论
              </el-button>
            </div>
          </div>

          <!-- 评论列表 -->
          <div v-loading="commentsLoading" class="comment-list">
            <div v-if="comments.length === 0 && !commentsLoading" class="empty-comment">
              暂无评论，快来发表第一条评论吧~
            </div>
            <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
              <div class="comment-main">
                <el-avatar :size="36" :src="comment.avatar" @click="goUserProfile(comment.userId)">
                  {{ comment.username?.charAt(0) }}
                </el-avatar>
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="comment-author" @click="goUserProfile(comment.userId)">{{ comment.username }}</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <div class="comment-content">{{ comment.content }}</div>
                  <div class="comment-actions">
                    <span class="action-btn" :class="{ active: comment.liked }" @click="handleCommentLike(comment)">
                      <el-icon><Star /></el-icon> {{ comment.likeCount || 0 }}
                    </span>
                    <span class="action-btn" @click="replyTo(comment)">
                      <el-icon><ChatDotRound /></el-icon> 回复
                    </span>
                    <span v-if="comment.userId === currentUserId" class="action-btn delete" @click="deleteComment(comment)">
                      删除
                    </span>
                  </div>
                </div>
              </div>

              <!-- 回复列表 -->
              <div v-if="comment.replies && comment.replies.length > 0" class="reply-list">
                <div v-for="reply in comment.replies" :key="reply.commentId" class="reply-item">
                  <el-avatar :size="28" :src="reply.avatar" @click="goUserProfile(reply.userId)">
                    {{ reply.username?.charAt(0) }}
                  </el-avatar>
                  <div class="reply-body">
                    <div class="reply-header">
                      <span class="reply-author" @click="goUserProfile(reply.userId)">{{ reply.username }}</span>
                      <template v-if="reply.replyUsername">
                        <span class="reply-to">回复</span>
                        <span class="reply-target">@{{ reply.replyUsername }}</span>
                      </template>
                      <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                    </div>
                    <div class="reply-content">{{ reply.content }}</div>
                    <div class="reply-actions">
                      <span class="action-btn" :class="{ active: reply.liked }" @click="handleCommentLike(reply)">
                        <el-icon><Star /></el-icon> {{ reply.likeCount || 0 }}
                      </span>
                      <span class="action-btn" @click="replyTo(comment, reply)">
                        <el-icon><ChatDotRound /></el-icon> 回复
                      </span>
                      <span v-if="reply.userId === currentUserId" class="action-btn delete" @click="deleteComment(reply)">
                        删除
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 回复输入框 -->
              <div v-if="replyingTo?.commentId === comment.commentId" class="reply-input">
                <el-input
                  v-model="replyContent"
                  type="textarea"
                  :rows="2"
                  :placeholder="`回复 @${replyTarget?.username || comment.username}`"
                  maxlength="500"
                />
                <div class="reply-submit">
                  <el-button size="small" @click="cancelReply">取消</el-button>
                  <el-button size="small" type="primary" @click="submitReply" :loading="submitting" :disabled="!replyContent.trim()">
                    回复
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
      <el-empty v-else-if="!loading" description="文章不存在或已删除" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, StarFilled, ChatDotRound } from '@element-plus/icons-vue'
import ArticleApi from '@/api/ArticleApi'
import CommentApi from '@/api/CommentApi'
import { marked } from 'marked'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const article = ref(null)
const liked = ref(false)
const collected = ref(false)
const likeLoading = ref(false)
const collectLoading = ref(false)

// 评论相关
const comments = ref([])
const commentsLoading = ref(false)
const commentContent = ref('')
const submitting = ref(false)
const replyingTo = ref(null)
const replyTarget = ref(null)
const replyContent = ref('')

const currentUserId = computed(() => userStore.userInfo?.userId)

const canEdit = computed(() => {
  if (!article.value || !userStore.userInfo) return false
  return String(article.value.authorUserId) === String(userStore.userInfo.userId)
})

const renderedContent = computed(() => {
  const c = article.value?.content
  if (!c) return ''
  try {
    return marked.parse(c)
  } catch {
    return c
  }
})

function formatTime(t) {
  if (!t) return '-'
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 2592000000) return Math.floor(diff / 86400000) + '天前'
  return d.toLocaleDateString()
}

function goBack() {
  router.back()
}

function goEdit() {
  if (!article.value?.articleId) return
  router.push({ name: 'ArticlePublish', query: { articleId: article.value.articleId } })
}

function goUserProfile(userId) {
  if (!userId) return
  router.push({ name: 'UserProfile', params: { id: userId } })
}

async function loadDetail() {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await ArticleApi.detail(Number(id))
    console.log('文章详情原始响应:', res)
    
    // 处理嵌套的 R 格式响应
    let articleData = null
    if (res && typeof res === 'object') {
      // 如果 res 有 data 字段
      if (res.data !== undefined) {
        // 检查 res.data 是否又是一个 R 格式对象（有 status 字段）
        if (res.data.status !== undefined && res.data.data !== undefined) {
          // res.data 是 R 格式，数据在 res.data.data 中
          articleData = res.data.data
        } else {
          // res.data 就是文章数据
          articleData = res.data
        }
      } else {
        // res 直接就是文章数据
        articleData = res
      }
    }
    
    article.value = articleData
    console.log('解析后的文章数据:', article.value)
    
    if (article.value) {
      loadInteractionStatus()
      loadComments()
    } else {
      console.warn('文章数据为空')
    }
  } catch (e) {
    console.error('加载文章详情失败:', e)
    console.error('错误详情:', e.message)
    article.value = null
    
    // 如果是权限错误或文章不存在/已删除，跳转回我的文章页面
    const errorMsg = e?.message || ''
    if (errorMsg.includes('无权限') || errorMsg.includes('不存在') || errorMsg.includes('已删除')) {
      ElMessage.error(errorMsg)
      setTimeout(() => {
        router.push({ name: 'ArticleMy' })
      }, 1500)
    }
  } finally {
    loading.value = false
  }
}

async function loadInteractionStatus() {
  if (!article.value?.articleId) return
  try {
    const [likeRes, collectRes] = await Promise.all([
      ArticleApi.getLikeStatus(article.value.articleId),
      ArticleApi.getCollectStatus(article.value.articleId)
    ])
    liked.value = likeRes?.data || false
    collected.value = collectRes?.data || false
  } catch (e) {
    console.error('加载互动状态失败:', e)
  }
}

async function handleLike() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  likeLoading.value = true
  try {
    await ArticleApi.like(article.value.articleId)
    liked.value = !liked.value
    article.value.likeCount = (article.value.likeCount || 0) + (liked.value ? 1 : -1)
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    likeLoading.value = false
  }
}

async function handleCollect() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  collectLoading.value = true
  try {
    await ArticleApi.collect(article.value.articleId)
    collected.value = !collected.value
    article.value.collectCount = (article.value.collectCount || 0) + (collected.value ? 1 : -1)
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    collectLoading.value = false
  }
}

async function loadComments() {
  if (!article.value?.articleId) return
  commentsLoading.value = true
  try {
    const res = await CommentApi.list(article.value.articleId)
    comments.value = res?.data || []
  } catch (e) {
    console.error('加载评论失败:', e)
    comments.value = []
  } finally {
    commentsLoading.value = false
  }
}

async function submitComment() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  if (!commentContent.value.trim()) return
  submitting.value = true
  try {
    await CommentApi.add({
      articleId: article.value.articleId,
      content: commentContent.value.trim()
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    article.value.commentCount = (article.value.commentCount || 0) + 1
    loadComments()
  } catch (e) {
    ElMessage.error(e?.message || '评论失败')
  } finally {
    submitting.value = false
  }
}

function replyTo(comment, target = null) {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  replyingTo.value = comment
  replyTarget.value = target
  replyContent.value = ''
}

function cancelReply() {
  replyingTo.value = null
  replyTarget.value = null
  replyContent.value = ''
}

async function submitReply() {
  if (!userStore.token || !replyContent.value.trim()) return
  submitting.value = true
  try {
    await CommentApi.add({
      articleId: article.value.articleId,
      parentId: replyingTo.value.commentId,
      replyUserId: replyTarget.value?.userId || replyingTo.value.userId,
      content: replyContent.value.trim()
    })
    ElMessage.success('回复成功')
    cancelReply()
    article.value.commentCount = (article.value.commentCount || 0) + 1
    loadComments()
  } catch (e) {
    ElMessage.error(e?.message || '回复失败')
  } finally {
    submitting.value = false
  }
}

async function handleCommentLike(comment) {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await CommentApi.like(comment.commentId)
    comment.liked = !comment.liked
    comment.likeCount = (comment.likeCount || 0) + (comment.liked ? 1 : -1)
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

async function deleteComment(comment) {
  await ElMessageBox.confirm('确定删除这条评论？', '提示', { type: 'warning' })
  try {
    await CommentApi.delete(comment.commentId)
    ElMessage.success('删除成功')
    article.value.commentCount = Math.max(0, (article.value.commentCount || 0) - 1)
    loadComments()
  } catch (e) {
    ElMessage.error(e?.message || '删除失败')
  }
}

onMounted(loadDetail)
</script>

<style lang="scss" scoped>
.article-detail {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
.header {
  margin-bottom: 20px;
  .title {
    margin: 0 0 12px;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    line-height: 1.4;
  }
  .meta {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 14px;
    color: #606266;
    margin-bottom: 12px;
    .author-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      &:hover .author { color: #409EFF; }
    }
    .author { font-weight: 500; }
    .time { color: #909399; }
    .counts { margin-left: auto; color: #909399; }
  }
}
.cover {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  img { width: 100%; display: block; }
}
.content {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  margin-bottom: 24px;
}
.interaction-bar {
  padding: 16px 0;
  border-top: 1px solid var(--el-border-color-lighter);
  border-bottom: 1px solid var(--el-border-color-lighter);
  display: flex;
  gap: 12px;
}
.comment-section {
  margin-top: 24px;
  h3 {
    margin: 0 0 16px;
    font-size: 18px;
    font-weight: 600;
  }
}
.comment-input {
  margin-bottom: 20px;
  .comment-submit {
    margin-top: 12px;
    text-align: right;
  }
}
.comment-list {
  min-height: 100px;
}
.empty-comment {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}
.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  &:last-child { border-bottom: none; }
}
.comment-main {
  display: flex;
  gap: 12px;
}
.comment-body {
  flex: 1;
  min-width: 0;
}
.comment-header {
  margin-bottom: 6px;
  .comment-author {
    font-weight: 500;
    color: #303133;
    cursor: pointer;
    &:hover { color: #409EFF; }
  }
  .comment-time {
    margin-left: 12px;
    font-size: 12px;
    color: #909399;
  }
}
.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  word-break: break-word;
}
.comment-actions, .reply-actions {
  margin-top: 8px;
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #909399;
  .action-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    cursor: pointer;
    &:hover { color: #409EFF; }
    &.active { color: #409EFF; }
    &.delete:hover { color: #F56C6C; }
  }
}
.reply-list {
  margin-left: 48px;
  margin-top: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}
.reply-item {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  &:not(:last-child) { border-bottom: 1px solid #eee; }
}
.reply-body {
  flex: 1;
  min-width: 0;
}
.reply-header {
  margin-bottom: 4px;
  font-size: 13px;
  .reply-author {
    font-weight: 500;
    cursor: pointer;
    &:hover { color: #409EFF; }
  }
  .reply-to { margin: 0 4px; color: #909399; }
  .reply-target { color: #409EFF; }
  .reply-time { margin-left: 12px; color: #909399; }
}
.reply-content {
  font-size: 13px;
  line-height: 1.5;
  color: #606266;
}
.reply-input {
  margin-left: 48px;
  margin-top: 12px;
  .reply-submit {
    margin-top: 8px;
    text-align: right;
  }
}
</style>

<style lang="scss">
.markdown-body {
  word-break: break-word;
  h1, h2, h3 { margin-top: 1em; margin-bottom: 0.5em; font-weight: 600; }
  h1 { font-size: 1.5em; }
  h2 { font-size: 1.3em; }
  h3 { font-size: 1.1em; }
  p { margin: 0.5em 0; }
  ul, ol { padding-left: 1.5em; margin: 0.5em 0; }
  code { background: #f5f5f5; padding: 2px 6px; border-radius: 4px; font-size: 0.9em; }
  pre { background: #f5f5f5; padding: 12px; border-radius: 4px; overflow-x: auto; margin: 0.5em 0; }
  pre code { background: none; padding: 0; }
  blockquote { border-left: 4px solid #ddd; margin: 0.5em 0; padding-left: 1em; color: #666; }
  img { max-width: 100%; }
  a { color: #409eff; }
}
</style>

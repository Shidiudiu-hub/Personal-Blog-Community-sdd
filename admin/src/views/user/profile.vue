<template>
  <div class="user-profile">
    <el-card v-loading="loading" class="profile-card">
      <template v-if="user">
        <div class="profile-header">
          <el-avatar :size="80" :src="user.avatar">{{ user.username?.charAt(0) }}</el-avatar>
          <div class="profile-info">
            <h2 class="username">{{ user.username }}</h2>
            <p class="bio">{{ user.bio || '这个人很懒，什么都没留下~' }}</p>
            <div class="stats">
              <span class="stat-item" @click="activeTab = 'articles'">
                <strong>{{ statistics.articleCount || 0 }}</strong> 文章
              </span>
              <span class="stat-item" @click="activeTab = 'following'">
                <strong>{{ user.followCount || 0 }}</strong> 关注
              </span>
              <span class="stat-item" @click="activeTab = 'followers'">
                <strong>{{ user.fanCount || 0 }}</strong> 粉丝
              </span>
              <span class="stat-item">
                <strong>{{ statistics.likeCount || 0 }}</strong> 获赞
              </span>
            </div>
          </div>
          <div v-if="!isCurrentUser" class="profile-actions">
            <el-button 
              :type="followed ? 'default' : 'primary'" 
              @click="handleFollow"
              :loading="followLoading"
            >
              {{ followed ? '已关注' : '关注' }}
            </el-button>
          </div>
          <div v-else class="profile-actions">
            <el-button @click="goSettings">编辑资料</el-button>
          </div>
        </div>

        <el-tabs v-model="activeTab" class="profile-tabs">
          <el-tab-pane label="文章" name="articles">
            <div v-loading="articlesLoading" class="article-list">
              <el-empty v-if="!articlesLoading && articles.length === 0" description="暂无文章" />
              <div v-for="item in articles" :key="item.articleId" class="article-item" @click="goDetail(item.articleId)">
                <h4 class="article-title">{{ item.title }}</h4>
                <p class="article-summary">{{ item.summary || '无摘要' }}</p>
                <div class="article-meta">
                  <span>{{ formatTime(item.createTime) }}</span>
                  <span>阅读 {{ item.viewCount || 0 }}</span>
                  <span>点赞 {{ item.likeCount || 0 }}</span>
                </div>
              </div>
              <div class="pagination-wrap">
                <el-pagination
                  v-model:current-page="articlePage"
                  v-model:page-size="articleSize"
                  :total="articleTotal"
                  layout="total, prev, pager, next"
                  @current-change="loadArticles"
                />
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="关注" name="following">
            <div v-loading="followingLoading" class="user-list">
              <el-empty v-if="!followingLoading && followingList.length === 0" description="暂无关注" />
              <div v-for="item in followingList" :key="item.userId" class="user-item">
                <el-avatar :size="48" :src="item.avatar" @click="goUserProfile(item.userId)">
                  {{ item.username?.charAt(0) }}
                </el-avatar>
                <div class="user-info" @click="goUserProfile(item.userId)">
                  <div class="user-name">{{ item.username }}</div>
                  <div class="user-stats">
                    {{ item.fanCount || 0 }} 粉丝 · {{ item.followCount || 0 }} 关注
                  </div>
                </div>
                <el-button 
                  v-if="currentUserId"
                  size="small" 
                  :type="item.followed ? 'default' : 'primary'"
                  @click.stop="handleFollowUser(item)"
                >
                  {{ item.followed ? (item.mutual ? '互相关注' : '已关注') : '关注' }}
                </el-button>
              </div>
              <div class="pagination-wrap">
                <el-pagination
                  v-model:current-page="followingPage"
                  :total="followingTotal"
                  :page-size="10"
                  layout="total, prev, pager, next"
                  @current-change="loadFollowing"
                />
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="粉丝" name="followers">
            <div v-loading="followersLoading" class="user-list">
              <el-empty v-if="!followersLoading && followersList.length === 0" description="暂无粉丝" />
              <div v-for="item in followersList" :key="item.userId" class="user-item">
                <el-avatar :size="48" :src="item.avatar" @click="goUserProfile(item.userId)">
                  {{ item.username?.charAt(0) }}
                </el-avatar>
                <div class="user-info" @click="goUserProfile(item.userId)">
                  <div class="user-name">{{ item.username }}</div>
                  <div class="user-stats">
                    {{ item.fanCount || 0 }} 粉丝 · {{ item.followCount || 0 }} 关注
                  </div>
                </div>
                <el-button 
                  v-if="currentUserId"
                  size="small" 
                  :type="item.followed ? 'default' : 'primary'"
                  @click.stop="handleFollowUser(item)"
                >
                  {{ item.followed ? (item.mutual ? '互相关注' : '已关注') : '回关' }}
                </el-button>
              </div>
              <div class="pagination-wrap">
                <el-pagination
                  v-model:current-page="followersPage"
                  :total="followersTotal"
                  :page-size="10"
                  layout="total, prev, pager, next"
                  @current-change="loadFollowers"
                />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </template>
      <el-empty v-else-if="!loading" description="用户不存在" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import UserApi from '@/api/UserApi'
import ArticleApi from '@/api/ArticleApi'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const user = ref(null)
const statistics = ref({})
const followed = ref(false)
const followLoading = ref(false)
const activeTab = ref('articles')

// 文章列表
const articles = ref([])
const articlesLoading = ref(false)
const articlePage = ref(1)
const articleSize = ref(10)
const articleTotal = ref(0)

// 关注列表
const followingList = ref([])
const followingLoading = ref(false)
const followingPage = ref(1)
const followingTotal = ref(0)

// 粉丝列表
const followersList = ref([])
const followersLoading = ref(false)
const followersPage = ref(1)
const followersTotal = ref(0)

const currentUserId = computed(() => userStore.userInfo?.userId)
const targetUserId = computed(() => Number(route.params.id) || currentUserId.value)
const isCurrentUser = computed(() => currentUserId.value && currentUserId.value === targetUserId.value)

function formatTime(t) {
  if (!t) return '-'
  return new Date(t).toLocaleDateString()
}

function goDetail(articleId) {
  router.push({ name: 'ArticleDetail', params: { id: articleId } })
}

function goUserProfile(userId) {
  if (userId === targetUserId.value) return
  router.push({ name: 'UserProfile', params: { id: userId } })
}

function goSettings() {
  router.push({ name: 'UserSettings' })
}

async function loadUserInfo() {
  loading.value = true
  try {
    const res = await UserApi.getInfo(targetUserId.value)
    user.value = res?.data || null
    if (user.value && currentUserId.value) {
      const statusRes = await UserApi.getFollowStatus(targetUserId.value)
      followed.value = statusRes?.data || false
    }
  } catch (e) {
    console.error('加载用户信息失败:', e)
    user.value = null
  } finally {
    loading.value = false
  }
}

async function loadStatistics() {
  try {
    const res = await UserApi.getStatistics(targetUserId.value)
    statistics.value = res?.data || {}
  } catch (e) {
    console.error('加载统计数据失败:', e)
  }
}

async function loadArticles() {
  articlesLoading.value = true
  try {
    const res = await ArticleApi.list({
      userId: targetUserId.value,
      page: articlePage.value,
      size: articleSize.value
    })
    
    // 处理嵌套的 R 格式响应
    let pageResult = null
    let totalValue = 0
    
    if (res && typeof res === 'object' && res.data) {
      if (res.data.status !== undefined && res.data.data !== undefined) {
        pageResult = res.data.data
        totalValue = res.data.recordsTotal ?? res.data.total ?? 0
      } else if (Array.isArray(res.data)) {
        pageResult = res.data
        totalValue = res.data.length
      } else {
        pageResult = res.data
        totalValue = res.data.total ?? res.data.recordsTotal ?? 0
      }
    }
    
    if (Array.isArray(pageResult)) {
      articles.value = pageResult
    } else if (pageResult && typeof pageResult === 'object') {
      articles.value = pageResult.list || pageResult.rows || pageResult.records || pageResult.data || []
    } else {
      articles.value = []
    }
    
    articleTotal.value = typeof totalValue === 'string' ? parseInt(totalValue, 10) : (totalValue || 0)
  } catch (e) {
    console.error('加载文章失败:', e)
    articles.value = []
    articleTotal.value = 0
  } finally {
    articlesLoading.value = false
  }
}

async function loadFollowing() {
  followingLoading.value = true
  try {
    const res = await UserApi.getFollowingList({
      userId: targetUserId.value,
      page: followingPage.value,
      size: 10
    })
    
    // 处理嵌套的 R 格式响应
    let pageResult = null
    let totalValue = 0
    
    if (res && typeof res === 'object' && res.data) {
      if (res.data.status !== undefined && res.data.data !== undefined) {
        pageResult = res.data.data
        totalValue = res.data.recordsTotal ?? res.data.total ?? 0
      } else if (Array.isArray(res.data)) {
        pageResult = res.data
        totalValue = res.data.length
      } else {
        pageResult = res.data
        totalValue = res.data.total ?? res.data.recordsTotal ?? 0
      }
    }
    
    if (Array.isArray(pageResult)) {
      followingList.value = pageResult
    } else if (pageResult && typeof pageResult === 'object') {
      followingList.value = pageResult.list || pageResult.rows || pageResult.records || pageResult.data || []
    } else {
      followingList.value = []
    }
    
    followingTotal.value = typeof totalValue === 'string' ? parseInt(totalValue, 10) : (totalValue || 0)
  } catch (e) {
    console.error('加载关注列表失败:', e)
    followingList.value = []
    followingTotal.value = 0
  } finally {
    followingLoading.value = false
  }
}

async function loadFollowers() {
  followersLoading.value = true
  try {
    const res = await UserApi.getFollowerList({
      userId: targetUserId.value,
      page: followersPage.value,
      size: 10
    })
    
    // 处理嵌套的 R 格式响应
    let pageResult = null
    let totalValue = 0
    
    if (res && typeof res === 'object' && res.data) {
      if (res.data.status !== undefined && res.data.data !== undefined) {
        pageResult = res.data.data
        totalValue = res.data.recordsTotal ?? res.data.total ?? 0
      } else if (Array.isArray(res.data)) {
        pageResult = res.data
        totalValue = res.data.length
      } else {
        pageResult = res.data
        totalValue = res.data.total ?? res.data.recordsTotal ?? 0
      }
    }
    
    if (Array.isArray(pageResult)) {
      followersList.value = pageResult
    } else if (pageResult && typeof pageResult === 'object') {
      followersList.value = pageResult.list || pageResult.rows || pageResult.records || pageResult.data || []
    } else {
      followersList.value = []
    }
    
    followersTotal.value = typeof totalValue === 'string' ? parseInt(totalValue, 10) : (totalValue || 0)
  } catch (e) {
    console.error('加载粉丝列表失败:', e)
    followersList.value = []
    followersTotal.value = 0
  } finally {
    followersLoading.value = false
  }
}

async function handleFollow() {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  followLoading.value = true
  try {
    await UserApi.follow(targetUserId.value)
    followed.value = !followed.value
    user.value.fanCount = (user.value.fanCount || 0) + (followed.value ? 1 : -1)
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    followLoading.value = false
  }
}

async function handleFollowUser(item) {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    await UserApi.follow(item.userId)
    item.followed = !item.followed
    if (item.followed) {
      // 检查是否互关
      const statusRes = await UserApi.getFollowStatus(item.userId)
      item.mutual = statusRes?.data || false
    } else {
      item.mutual = false
    }
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  }
}

watch(activeTab, (val) => {
  if (val === 'articles' && articles.value.length === 0) loadArticles()
  if (val === 'following' && followingList.value.length === 0) loadFollowing()
  if (val === 'followers' && followersList.value.length === 0) loadFollowers()
})

watch(() => route.params.id, () => {
  articlePage.value = 1
  followingPage.value = 1
  followersPage.value = 1
  articles.value = []
  followingList.value = []
  followersList.value = []
  loadUserInfo()
  loadStatistics()
  loadArticles()
})

onMounted(() => {
  loadUserInfo()
  loadStatistics()
  loadArticles()
})
</script>

<style lang="scss" scoped>
.user-profile {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}
.profile-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}
.profile-info {
  flex: 1;
  .username {
    margin: 0 0 8px;
    font-size: 24px;
    font-weight: 600;
  }
  .bio {
    margin: 0 0 12px;
    color: #606266;
    font-size: 14px;
  }
}
.stats {
  display: flex;
  gap: 24px;
  .stat-item {
    cursor: pointer;
    color: #606266;
    font-size: 14px;
    &:hover { color: #409EFF; }
    strong {
      display: block;
      font-size: 18px;
      color: #303133;
    }
  }
}
.profile-tabs {
  margin-top: 20px;
}
.article-list {
  min-height: 200px;
}
.article-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  &:hover .article-title { color: #409EFF; }
  .article-title {
    margin: 0 0 8px;
    font-size: 16px;
    font-weight: 500;
  }
  .article-summary {
    margin: 0 0 8px;
    font-size: 14px;
    color: #606266;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .article-meta {
    font-size: 13px;
    color: #909399;
    span { margin-right: 16px; }
  }
}
.user-list {
  min-height: 200px;
}
.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  .el-avatar { cursor: pointer; }
}
.user-info {
  flex: 1;
  cursor: pointer;
  .user-name {
    font-weight: 500;
    &:hover { color: #409EFF; }
  }
  .user-stats {
    font-size: 13px;
    color: #909399;
  }
}
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

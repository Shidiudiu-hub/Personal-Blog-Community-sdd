<template>
  <div class="dashboard">
    <div class="main-wrap">
      <div class="article-list">
        <div class="filter-bar">
          <el-select
            v-model="filters.categoryId"
            placeholder="分类"
            clearable
            style="width: 140px; margin-right: 12px"
            @change="loadList"
          >
            <el-option
              v-for="c in categories"
              :key="c.categoryId"
              :label="c.name"
              :value="c.categoryId"
            />
          </el-select>
          <el-select
            v-model="filters.tagId"
            placeholder="标签"
            clearable
            style="width: 140px; margin-right: 12px"
            @change="loadList"
          >
            <el-option v-for="t in tags" :key="t.tagId" :label="t.name" :value="t.tagId" />
          </el-select>
          <el-select
            v-model="filters.orderBy"
            placeholder="排序"
            style="width: 120px; margin-right: 12px"
            @change="loadList"
          >
            <el-option label="最新" value="latest" />
            <el-option label="最热" value="hot" />
            <el-option label="最多点赞" value="likes" />
          </el-select>
          <el-input
            v-model="filters.keyword"
            placeholder="搜索标题、内容"
            clearable
            style="width: 200px; margin-right: 12px"
            @keyup.enter="loadList"
          />
          <el-button type="primary" @click="loadList">搜索</el-button>
        </div>

        <div v-loading="loading" class="cards">
          <el-empty v-if="!loading && list.length === 0" description="暂无文章" />
          <div
            v-for="item in list"
            :key="item.articleId"
            class="article-card"
            @click="goDetail(item.articleId)"
          >
            <div v-if="item.coverImage" class="cover">
              <img :src="item.coverImage" :alt="item.title" />
            </div>
            <div class="card-body">
              <h3 class="title">{{ item.title }}</h3>
              <p class="summary">{{ item.summary || '无摘要' }}</p>
              <div class="meta">
                <span class="author">
                  <el-avatar :size="20" :src="item.authorAvatar">{{ item.authorName?.charAt(0) }}</el-avatar>
                  {{ item.authorName }}
                </span>
                <span class="time">{{ formatTime(item.createTime) }}</span>
                <span class="counts">
                  <el-icon><View /></el-icon> {{ item.viewCount || 0 }}
                  <el-icon class="ml"><Star /></el-icon> {{ item.likeCount || 0 }}
                  <el-icon class="ml"><ChatDotRound /></el-icon> {{ item.commentCount || 0 }}
                </span>
              </div>
              <div class="tags">
                <el-tag v-if="item.categoryName" size="small" type="info">{{ item.categoryName }}</el-tag>
                <el-tag
                  v-for="tag in (item.tags || [])"
                  :key="tag.tagId"
                  size="small"
                  :style="{ color: tag.color || '#409EFF', borderColor: tag.color || '#409EFF' }"
                  style="margin-left: 6px"
                >
                  {{ tag.name }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[10, 20, 30]"
            layout="total, sizes, prev, pager, next"
            @current-change="loadList"
            @size-change="loadList"
          />
        </div>
      </div>

      <div class="sidebar">
        <el-card shadow="never" class="side-card">
          <template #header>
            <span>写文章</span>
          </template>
          <el-button type="primary" style="width: 100%" @click="goPublish">发布文章</el-button>
        </el-card>
        <el-card shadow="never" class="side-card">
          <template #header>
            <span>热门标签</span>
          </template>
          <div class="tag-cloud">
            <el-tag
              v-for="t in hotTags"
              :key="t.tagId"
              size="small"
              class="tag-item"
              :style="{ color: t.color || '#409EFF' }"
              @click="filterByTag(t.tagId)"
            >
              {{ t.name }}
            </el-tag>
            <span v-if="hotTags.length === 0" class="text-muted">暂无</span>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { View, Star, ChatDotRound } from '@element-plus/icons-vue'
import ArticleApi from '@/api/ArticleApi'
import CategoryApi from '@/api/CategoryApi'
import TagApi from '@/api/TagApi'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const categories = ref([])
const tags = ref([])
const hotTags = ref([])

const filters = reactive({
  categoryId: null,
  tagId: null,
  orderBy: 'latest',
  keyword: ''
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

function goDetail(id) {
  router.push({ name: 'ArticleDetail', params: { id } })
}

function goPublish() {
  router.push({ name: 'ArticlePublish' })
}

function filterByTag(tagId) {
  filters.tagId = tagId
  page.value = 1
  loadList()
}

async function loadList() {
  loading.value = true
  try {
    console.log('请求参数:', {
      categoryId: filters.categoryId || undefined,
      tagId: filters.tagId || undefined,
      keyword: filters.keyword || undefined,
      orderBy: filters.orderBy,
      page: page.value,
      size: size.value
    })
    const res = await ArticleApi.list({
      categoryId: filters.categoryId || undefined,
      tagId: filters.tagId || undefined,
      keyword: filters.keyword || undefined,
      orderBy: filters.orderBy,
      page: page.value,
      size: size.value
    })
    console.log('首页文章列表原始响应:', res)
    console.log('响应类型:', typeof res)
    console.log('res.data:', res?.data)
    
    // 根据日志，res.data 的结构可能是: { status: 0, data: Array(8), recordsTotal: '8' }
    // 这说明 res.data 可能又是一个 R 格式对象，需要再取一层
    let pageResult = null
    let totalValue = 0
    
    if (res && typeof res === 'object' && res.data) {
      // 检查 res.data 是否又是一个 R 格式对象（有 status 字段）
      if (res.data.status !== undefined && res.data.data !== undefined) {
        // res.data 是 R 格式，数据在 res.data.data 中
        pageResult = res.data.data
        // 总数在 res.data.recordsTotal 或 res.data.total
        totalValue = res.data.recordsTotal ?? res.data.total ?? 0
      } else if (Array.isArray(res.data)) {
        // res.data 直接是数组
        pageResult = res.data
        totalValue = res.data.length
      } else {
        // res.data 是 PageResult 对象
        pageResult = res.data
        totalValue = res.data.total ?? res.data.recordsTotal ?? 0
      }
    }
    
    console.log('PageResult:', pageResult)
    console.log('Total:', totalValue)
    
    // 处理列表数据
    if (Array.isArray(pageResult)) {
      list.value = pageResult
    } else if (pageResult && typeof pageResult === 'object') {
      list.value = pageResult.list || pageResult.rows || pageResult.records || pageResult.data || []
    } else {
      list.value = []
    }
    
    // 处理总数（可能是字符串，需要转换为数字）
    total.value = typeof totalValue === 'string' ? parseInt(totalValue, 10) : (totalValue || 0)
    
    console.log('最终文章列表:', list.value)
    console.log('最终总数:', total.value)
    console.log('列表长度:', list.value.length)
  } catch (e) {
    console.error('加载文章列表失败:', e)
    console.error('错误详情:', e.message, e.stack)
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    const res = await CategoryApi.list()
    // 处理 R 格式: { status: 0, msg: '...', data: [...] }
    // 或直接数组格式: [...]
    if (Array.isArray(res)) {
      categories.value = res
    } else if (res?.data && Array.isArray(res.data)) {
      categories.value = res.data
    } else {
      categories.value = []
    }
  } catch (e) {
    console.error('加载分类失败:', e)
    categories.value = []
  }
}

async function loadTags() {
  try {
    const res = await TagApi.list()
    // 处理 R 格式: { status: 0, msg: '...', data: [...] }
    // 或直接数组格式: [...]
    if (Array.isArray(res)) {
      tags.value = res
    } else if (res?.data && Array.isArray(res.data)) {
      tags.value = res.data
    } else {
      tags.value = []
    }
  } catch (e) {
    console.error('加载标签失败:', e)
    tags.value = []
  }
}

async function loadHotTags() {
  try {
    const res = await TagApi.hot()
    // 处理 R 格式: { status: 0, msg: '...', data: [...] }
    // 或直接数组格式: [...]
    if (Array.isArray(res)) {
      hotTags.value = res
    } else if (res?.data && Array.isArray(res.data)) {
      hotTags.value = res.data
    } else {
      hotTags.value = []
    }
  } catch (e) {
    console.error('加载热门标签失败:', e)
    hotTags.value = []
  }
}

onMounted(() => {
  loadList()
  loadCategories()
  loadTags()
  loadHotTags()
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
}
.main-wrap {
  display: flex;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}
.article-list {
  flex: 1;
  min-width: 0;
}
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 16px;
}
.cards {
  min-height: 200px;
}
.article-card {
  display: flex;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  margin-bottom: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  }
}
.cover {
  width: 180px;
  flex-shrink: 0;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}
.card-body {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
  .title {
    margin: 0 0 8px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .summary {
    margin: 0 0 12px;
    font-size: 13px;
    color: #606266;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .meta {
    font-size: 12px;
    color: #909399;
    margin-bottom: 8px;
    .author {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      margin-right: 16px;
    }
    .ml { margin-left: 8px; }
  }
  .tags {
    margin-top: auto;
  }
}
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.sidebar {
  width: 260px;
  flex-shrink: 0;
}
.side-card {
  margin-bottom: 16px;
  :deep(.el-card__header) {
    font-weight: 600;
    padding: 12px 16px;
  }
}
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  .tag-item {
    cursor: pointer;
  }
}
.text-muted {
  color: #909399;
  font-size: 13px;
}
</style>

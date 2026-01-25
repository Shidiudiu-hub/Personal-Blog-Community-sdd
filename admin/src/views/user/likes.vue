<template>
  <div class="my-likes">
    <el-card>
      <template #header>
        <span>我的点赞</span>
      </template>
      <div v-loading="loading" class="article-list">
        <el-empty v-if="!loading && list.length === 0" description="暂无点赞的文章" />
        <div v-for="item in list" :key="item.articleId" class="article-item" @click="goDetail(item.articleId)">
          <div v-if="item.coverImage" class="cover">
            <img :src="item.coverImage" :alt="item.title" />
          </div>
          <div class="article-body">
            <h4 class="title">{{ item.title }}</h4>
            <p class="summary">{{ item.summary || '无摘要' }}</p>
            <div class="meta">
              <span class="author">
                <el-avatar :size="20" :src="item.authorAvatar">{{ item.authorName?.charAt(0) }}</el-avatar>
                {{ item.authorName }}
              </span>
              <span class="stats">
                阅读 {{ item.viewCount || 0 }} · 点赞 {{ item.likeCount || 0 }}
              </span>
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ArticleApi from '@/api/ArticleApi'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

function goDetail(id) {
  router.push({ name: 'ArticleDetail', params: { id } })
}

async function loadList() {
  loading.value = true
  try {
    const res = await ArticleApi.myLikes({ page: page.value, size: size.value })
    console.log('点赞列表原始响应:', res)
    
    // 处理嵌套的 R 格式响应
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
    
    console.log('点赞列表数据:', list.value, '总数:', total.value)
  } catch (e) {
    console.error('加载点赞列表失败:', e)
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(loadList)
</script>

<style lang="scss" scoped>
.my-likes {
  padding: 20px;
}
.article-list {
  min-height: 200px;
}
.article-item {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
  cursor: pointer;
  &:hover .title { color: #409EFF; }
}
.cover {
  width: 120px;
  height: 80px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
  img { width: 100%; height: 100%; object-fit: cover; }
}
.article-body {
  flex: 1;
  min-width: 0;
  .title {
    margin: 0 0 8px;
    font-size: 16px;
    font-weight: 500;
  }
  .summary {
    margin: 0 0 8px;
    font-size: 14px;
    color: #606266;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .meta {
    display: flex;
    align-items: center;
    gap: 16px;
    font-size: 13px;
    color: #909399;
    .author {
      display: flex;
      align-items: center;
      gap: 6px;
    }
  }
}
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

<template>
  <div class="my-articles">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的文章</span>
          <el-button type="primary" @click="goPublish">写文章</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="70" />
        <el-table-column prop="likeCount" label="点赞" width="70" />
        <el-table-column prop="commentCount" label="评论" width="70" />
        <el-table-column prop="createTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="goDetail(row.articleId)">查看</el-button>
            <el-button link type="primary" size="small" @click="goEdit(row.articleId)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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
import { ref, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import ArticleApi from '@/api/ArticleApi'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

function goPublish() {
  router.push({ name: 'ArticlePublish' })
}

function goDetail(id) {
  router.push({ name: 'ArticleDetail', params: { id } })
}

function goEdit(id) {
  router.push({ name: 'ArticlePublish', query: { articleId: id } })
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除「${row.title}」？`, '提示', {
    type: 'warning'
  })
  try {
    await ArticleApi.delete(row.articleId)
    ElMessage.success('已删除')
    loadList()
  } catch (e) {
    ElMessage.error(e?.message || '删除失败')
  }
}

async function loadList() {
  loading.value = true
  try {
    console.log('我的文章请求参数:', { page: page.value, size: size.value })
    const res = await ArticleApi.myList({ page: page.value, size: size.value })
    console.log('我的文章列表原始响应:', res)
    console.log('响应类型:', typeof res)
    console.log('res.data:', res?.data)
    
    // 根据日志，res.data 的结构是: { status: 0, data: Array(8), recordsTotal: '8' }
    // 这说明 res.data 又是一个 R 格式对象，需要再取一层
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
    console.error('加载我的文章失败:', e)
    console.error('错误详情:', e.message, e.stack)
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

onMounted(loadList)
onActivated(loadList)
</script>

<style lang="scss" scoped>
.my-articles {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>

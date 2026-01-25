<template>
  <div class="publish-page">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑文章' : '发布文章' }}</span>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" style="max-width: 800px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content" required>
          <div ref="editorRef" class="editor-wrap" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="选填，不填则自动截取" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="封面图片 URL，选填" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" clearable style="width: 200px">
            <el-option v-for="c in categories" :key="c.categoryId" :label="c.name" :value="c.categoryId" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tagIds" multiple placeholder="选择标签" clearable style="width: 100%">
            <el-option v-for="t in tags" :key="t.tagId" :label="t.name" :value="t.tagId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button :loading="saving" @click="saveDraft">保存草稿</el-button>
          <el-button type="primary" :loading="saving" @click="publish">发布</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Editor from '@toast-ui/editor'
import '@toast-ui/editor/dist/toastui-editor.css'
import ArticleApi from '@/api/ArticleApi'
import CategoryApi from '@/api/CategoryApi'
import TagApi from '@/api/TagApi'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const editorRef = ref(null)
let editorInstance = null

const categories = ref([])
const tags = ref([])
const saving = ref(false)

const isEdit = computed(() => !!route.query.articleId)

const form = reactive({
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  categoryId: null,
  tagIds: [],
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [
    {
      validator: (rule, value, callback) => {
        const content = getContent()
        if (!content || content.trim() === '') {
          callback(new Error('请输入内容'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

function goBack() {
  router.push({ path: '/dashboard' })
}

function getContent() {
  return editorInstance ? editorInstance.getMarkdown() : form.content
}

async function saveDraft() {
  form.content = getContent()
  // 草稿允许标题和内容为空，但至少要有标题
  if (!form.title || form.title.trim() === '') {
    ElMessage.warning('请输入标题')
    return
  }
  await submit(0)
}

async function publish() {
  form.content = getContent()
  // 先验证标题
  try {
    await formRef.value?.validateField('title')
  } catch {
    return
  }
  // 验证内容
  if (!form.content || form.content.trim() === '') {
    ElMessage.warning('请输入文章内容')
    // 手动触发 content 字段的验证错误
    formRef.value?.validateField('content').catch(() => {})
    return
  }
  // 验证通过，提交
  await submit(1)
}

async function submit(status) {
  saving.value = true
  try {
    const payload = {
      title: form.title,
      content: form.content,
      summary: form.summary || undefined,
      coverImage: form.coverImage || undefined,
      categoryId: form.categoryId || undefined,
      tagIds: form.tagIds?.length ? form.tagIds : undefined,
      status
    }
    if (isEdit.value) {
      payload.articleId = Number(route.query.articleId)
      await ArticleApi.update(payload)
      ElMessage.success(status === 1 ? '更新成功' : '草稿已保存')
      // 如果是草稿，跳转到我的文章列表；如果是发布，跳转到详情页
      if (status === 0) {
        router.push({ name: 'ArticleMy' })
      } else {
        router.push({ name: 'ArticleDetail', params: { id: payload.articleId } })
      }
    } else {
      const res = await ArticleApi.publish(payload)
      const id = res?.data ?? res
      ElMessage.success(status === 1 ? '发布成功' : '草稿已保存')
      if (id) {
        // 如果是草稿，跳转到我的文章列表；如果是发布，跳转到详情页
        if (status === 0) {
          router.push({ name: 'ArticleMy' })
        } else {
          router.push({ name: 'ArticleDetail', params: { id } })
        }
      } else {
        goBack()
      }
    }
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    saving.value = false
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

async function loadDetail() {
  if (!isEdit.value) return
  try {
    const res = await ArticleApi.detail(Number(route.query.articleId))
    console.log('编辑页面文章详情原始响应:', res)
    
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
    
    if (!articleData) {
      ElMessage.error('文章不存在或已删除')
      // 跳转回我的文章页面
      setTimeout(() => {
        router.push({ name: 'ArticleMy' })
      }, 1500)
      return
    }
    
    console.log('解析后的文章数据:', articleData)
    
    form.title = articleData.title || ''
    form.summary = articleData.summary || ''
    form.coverImage = articleData.coverImage || ''
    form.categoryId = articleData.categoryId || null
    form.tagIds = (articleData.tags || []).map(t => t.tagId) || []
    form.content = articleData.content || ''
    if (editorInstance) {
      editorInstance.setMarkdown(form.content)
    }
  } catch (e) {
    console.error('加载文章失败:', e)
    const errorMsg = e?.message || '加载文章失败'
    ElMessage.error(errorMsg)
    
    // 如果是权限错误或文章不存在/已删除，跳转回我的文章页面
    if (errorMsg.includes('无权限') || errorMsg.includes('不存在') || errorMsg.includes('已删除')) {
      setTimeout(() => {
        router.push({ name: 'ArticleMy' })
      }, 1500)
    }
  }
}

onMounted(() => {
  loadCategories()
  loadTags()
  if (editorRef.value) {
    editorInstance = new Editor({
      el: editorRef.value,
      initialEditType: 'markdown',
      previewStyle: 'vertical',
      height: '400px',
      initialValue: form.content
    })
    // 监听编辑器内容变化，更新 form.content
    editorInstance.on('change', () => {
      form.content = editorInstance.getMarkdown()
    })
  }
  loadDetail()
})

onBeforeUnmount(() => {
  editorInstance?.destroy?.()
  editorInstance = null
})
</script>

<style lang="scss" scoped>
.publish-page {
  padding: 20px;
}
.editor-wrap {
  width: 100%;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
  overflow: hidden;
}
:deep(.toastui-editor-default-ui) {
  border: none;
}
</style>

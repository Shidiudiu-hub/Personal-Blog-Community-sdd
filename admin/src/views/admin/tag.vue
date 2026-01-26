<template>
  <div class="tag-management">
    <el-card v-if="!isAdmin">
      <el-result
        icon="warning"
        title="无权限访问"
        sub-title="此页面仅管理员可访问"
      >
        <template #extra>
          <el-button type="primary" @click="goBack">返回首页</el-button>
        </template>
      </el-result>
    </el-card>
    <el-card v-else>
      <template #header>
        <div class="card-header">
          <span>标签管理</span>
          <el-button type="primary" @click="handleAdd">新增标签</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="tagId" label="ID" width="80" />
        <el-table-column prop="name" label="标签名称" min-width="150" />
        <el-table-column label="颜色" width="120">
          <template #default="{ row }">
            <div class="color-display">
              <span
                class="color-block"
                :style="{ backgroundColor: row.color || '#409EFF' }"
              />
              <span>{{ row.color || '#409EFF' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="articleCount" label="文章数" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入标签名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="颜色">
          <div class="color-picker">
            <el-color-picker v-model="form.color" />
            <el-input
              v-model="form.color"
              placeholder="#409EFF"
              style="width: 150px; margin-left: 10px"
              maxlength="20"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="saving">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import TagApi from '@/api/TagApi'

const router = useRouter()
const userStore = useUserStore()

const isAdmin = computed(() => {
  return userStore.userInfo?.role === 1
})

const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增标签')
const saving = ref(false)
const formRef = ref(null)

function goBack() {
  router.push('/dashboard')
}

const form = reactive({
  tagId: null,
  name: '',
  color: '#409EFF'
})

const rules = {
  name: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

async function loadList() {
  loading.value = true
  try {
    const res = await TagApi.list()
    // 处理 R 格式响应: { status: 0, msg: '...', data: [...] }
    if (Array.isArray(res)) {
      list.value = res
    } else if (res?.data) {
      // 检查 res.data 是否又是一个 R 格式对象
      if (res.data.status !== undefined && res.data.data !== undefined && Array.isArray(res.data.data)) {
        list.value = res.data.data
      } else if (Array.isArray(res.data)) {
        list.value = res.data
      } else {
        list.value = []
      }
    } else {
      list.value = []
    }
  } catch (e) {
    console.error('加载标签列表失败:', e)
    list.value = []
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  dialogTitle.value = '新增标签'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑标签'
  form.tagId = row.tagId
  form.name = row.name || ''
  form.color = row.color || '#409EFF'
  dialogVisible.value = true
}

function resetForm() {
  form.tagId = null
  form.name = ''
  form.color = '#409EFF'
  formRef.value?.resetFields()
}

async function submitForm() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    if (form.tagId) {
      await TagApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await TagApi.add(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除标签「${row.name}」？`, '提示', { type: 'warning' })
  try {
    await TagApi.delete(row.tagId)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    ElMessage.error(e?.message || '删除失败')
  }
}

onMounted(loadList)
</script>

<style lang="scss" scoped>
.tag-management {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.color-display {
  display: flex;
  align-items: center;
  gap: 8px;
}
.color-block {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid #ddd;
  display: inline-block;
}
.color-picker {
  display: flex;
  align-items: center;
}
</style>

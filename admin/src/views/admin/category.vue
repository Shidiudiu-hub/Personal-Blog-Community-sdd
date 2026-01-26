<template>
  <div class="category-management">
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
          <span>分类管理</span>
          <div class="header-actions">
            <el-switch
              v-model="dragMode"
              active-text="拖拽排序"
              inactive-text="表格模式"
              style="margin-right: 12px"
            />
            <el-button type="primary" @click="handleAdd">新增分类</el-button>
          </div>
        </div>
      </template>

      <div v-if="dragMode" v-loading="loading" class="drag-wrap">
        <div class="drag-tip">
          拖动左侧把手可调整排序，松手后会自动保存（后端会自动挤开其它分类，保证排序不重复）。
        </div>

        <div class="drag-header">
          <div class="cell drag">排序</div>
          <div class="cell id">ID</div>
          <div class="cell name">分类名称</div>
          <div class="cell desc">描述</div>
          <div class="cell count">文章数</div>
          <div class="cell time">创建时间</div>
          <div class="cell ops">操作</div>
        </div>

        <Draggable
          v-model="list"
          item-key="categoryId"
          handle=".drag-handle"
          :animation="150"
          ghost-class="drag-ghost"
          @end="onDragEnd"
        >
          <template #item="{ element, index }">
            <div class="drag-row">
              <div class="cell drag">
                <el-icon class="drag-handle"><Rank /></el-icon>
                <span class="sort-num">{{ index + 1 }}</span>
              </div>
              <div class="cell id">{{ element.categoryId }}</div>
              <div class="cell name">{{ element.name }}</div>
              <div class="cell desc">{{ element.description || '-' }}</div>
              <div class="cell count">{{ element.articleCount ?? 0 }}</div>
              <div class="cell time">{{ element.createTime }}</div>
              <div class="cell ops">
                <el-button link type="primary" size="small" @click="handleEdit(element)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDelete(element)">删除</el-button>
              </div>
            </div>
          </template>
        </Draggable>
      </div>

      <el-table v-else v-loading="loading" :data="list" stripe>
        <el-table-column prop="categoryId" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="100" />
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
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
          <div style="font-size: 12px; color: #909399; margin-top: 5px;">
            数字越小越靠前，用于控制分类在列表中的显示顺序。如果设置的排序数已存在，系统会自动调整已存在分类的排序数。
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
import { Rank } from '@element-plus/icons-vue'
import Draggable from 'vuedraggable'
import { useUserStore } from '@/store/user'
import CategoryApi from '@/api/CategoryApi'

const router = useRouter()
const userStore = useUserStore()

const isAdmin = computed(() => {
  return userStore.userInfo?.role === 1
})

const loading = ref(false)
const list = ref([])
const dragMode = ref(true)
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const saving = ref(false)
const formRef = ref(null)

function goBack() {
  router.push('/dashboard')
}

const form = reactive({
  categoryId: null,
  name: '',
  description: '',
  sortOrder: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

async function loadList() {
  loading.value = true
  try {
    const res = await CategoryApi.list()
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
    console.error('加载分类列表失败:', e)
    list.value = []
  } finally {
    loading.value = false
  }
}

async function onDragEnd(evt) {
  const oldIndex = evt?.oldIndex
  const newIndex = evt?.newIndex
  if (oldIndex === undefined || newIndex === undefined || oldIndex === newIndex) return

  // 备份，失败时回滚
  const prev = list.value.slice()
  const moved = list.value[newIndex]
  if (!moved?.categoryId) return

  try {
    // newIndex 是 0-based，后端使用 1..N
    await CategoryApi.update({ categoryId: moved.categoryId, sortOrder: newIndex + 1 })
    ElMessage.success('排序已更新')
    await loadList()
  } catch (e) {
    list.value = prev
    ElMessage.error(e?.message || '排序更新失败')
  }
}

function handleAdd() {
  dialogTitle.value = '新增分类'
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑分类'
  form.categoryId = row.categoryId
  form.name = row.name || ''
  form.description = row.description || ''
  form.sortOrder = row.sortOrder || 0
  dialogVisible.value = true
}

function resetForm() {
  form.categoryId = null
  form.name = ''
  form.description = ''
  form.sortOrder = 0
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
    if (form.categoryId) {
      await CategoryApi.update(form)
      ElMessage.success('更新成功')
    } else {
      await CategoryApi.add(form)
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
  await ElMessageBox.confirm(`确定删除分类「${row.name}」？`, '提示', { type: 'warning' })
  try {
    await CategoryApi.delete(row.categoryId)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    ElMessage.error(e?.message || '删除失败')
  }
}

onMounted(loadList)
</script>

<style lang="scss" scoped>
.category-management {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-actions {
  display: flex;
  align-items: center;
}

.drag-wrap {
  width: 100%;
}
.drag-tip {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}
.drag-header,
.drag-row {
  display: grid;
  grid-template-columns: 110px 80px 180px 1fr 90px 180px 140px;
  gap: 10px;
  align-items: center;
}
.drag-header {
  font-size: 12px;
  color: #909399;
  padding: 10px 12px;
  background: #fafafa;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 8px;
}
.drag-row {
  padding: 10px 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  margin-bottom: 8px;
  background: #fff;
}
.drag-row:hover {
  background: #fcfcfc;
}
.cell {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.cell.desc {
  white-space: nowrap;
}
.cell.ops {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.cell.drag {
  display: flex;
  align-items: center;
  gap: 8px;
}
.drag-handle {
  cursor: grab;
  color: #606266;
}
.drag-handle:active {
  cursor: grabbing;
}
.sort-num {
  font-variant-numeric: tabular-nums;
  color: #303133;
}
.drag-ghost {
  opacity: 0.6;
}
</style>

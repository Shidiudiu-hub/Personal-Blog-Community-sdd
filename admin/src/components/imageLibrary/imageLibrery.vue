<template>
  <el-dialog v-model="imageLibraryDialog" title="图片库" width="1000px" center append-to-body>
    <el-row :gutter="20" style="height: 650px; overflow-y: auto">
      <el-col :span="4" v-for="item in imageLibraryList" :key="item.id">
        <el-image :src="item.url" fit="contain" />
        <div style="display: flex; justify-content: center; margin: 10px auto 20px">
          <el-button type="primary" @click="handleImageLibrarySelect(item)">选择</el-button>
        </div>
      </el-col>
    </el-row>
    <template #footer>
      <div style="display: flex; justify-content: center">
        <el-pagination
          hide-on-single-page
          background
          layout="prev, pager, next"
          :total="imageLibraryTotal"
          :page-size="imageLibraryPageSize"
          :current-page="imageLibraryCurrentPage"
          @size-change="handleImageLibrarySizeChange"
          @current-change="handleImageLibraryCurrentChange"
        />
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, toRefs } from 'vue'
import AdminApi from '@/api/AdminApi'
import { ElMessage } from 'element-plus'
const emit = defineEmits(['handleImageLibrarySelect'])
const props = defineProps({
  imageLibraryDialog: {
    type: Boolean,
    default: false
  },
  imageLibraryFrom: {
    type: String,
    default: ''
  }
})
const { imageLibraryDialog, imageLibraryFrom } = toRefs(props)
const imageLibraryList = ref([])
const imageLibraryTotal = ref(0)
const imageLibraryPageSize = ref(12)
const imageLibraryCurrentPage = ref(1)

// 获取图片库
const getImageLibraryList = async () => {
  const imageLibraryListData = await AdminApi.adminImageInfoPageGet({
    page: imageLibraryCurrentPage.value,
    size: imageLibraryPageSize.value
  })
  if (imageLibraryListData.status === 0) {
    if (imageLibraryListData.data.length > 0) {
      imageLibraryList.value = imageLibraryListData.data
    } else {
      ElMessage({
        message: '图片库为空',
        type: 'warning',
        duration: 5 * 1000
      })
      imageLibraryList.value = []
    }
    imageLibraryTotal.value = imageLibraryListData.recordsTotal
  }
}

// 分页
const handleImageLibrarySizeChange = val => {
  imageLibraryPageSize.value = val
  getImageLibraryList()
}

// 分页
const handleImageLibraryCurrentChange = val => {
  imageLibraryCurrentPage.value = val
  getImageLibraryList()
}
const handleImageLibrarySelect = item => {
  emit('handleImageLibrarySelect', item, imageLibraryFrom.value)
}
onMounted(() => {
  getImageLibraryList()
})
</script>

<style scoped></style>

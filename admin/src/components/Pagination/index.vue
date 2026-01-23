<template>
  <div class="pagination-container">
    <!-- 分页插件 -->
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="[10, 20, 30, 40]"
      :small="small"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      background
    />
  </div>
</template>

<script setup>
import { ref, toRefs } from 'vue'
const emit = defineEmits(['size-change', 'current-change'])
const props = defineProps({
  total: {
    type: Number,
    default: 0
  },
  pageSize: {
    type: Number,
    default: 10
  },
  currentPage: {
    type: Number,
    default: 1
  }
})
const { total, pageSize, currentPage } = toRefs(props)
const small = ref(false)
// 当前页码
const handleSizeChange = val => {
  emit('size-change', val)
}
// 每页条数
const handleCurrentChange = val => {
  emit('current-change', val)
}
</script>

<style lang="scss" scoped>
.pagination-container {
  width: 100%;
  height: 70px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 20px 20px 20px;
  box-sizing: border-box;
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
}
</style>

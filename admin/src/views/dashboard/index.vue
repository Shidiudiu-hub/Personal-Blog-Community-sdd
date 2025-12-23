<template>
  <div class="dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统首页</span>
        </div>
      </template>
      <div class="content">
        <h2>欢迎使用心理咨询系统</h2>
        <p>当前用户角色: {{ roleName }}</p>
        <p>用户ID: {{ userInfo?.userId }}</p>
        <p>用户名: {{ userInfo?.username }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo || {})

const roleName = computed(() => {
  const roles = {
    1: '学生',
    2: '咨询师',
    3: '管理员'
  }
  return roles[userInfo.value.role] || '未知'
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;

  .content {
    text-align: center;
    padding: 20px;

    h2 {
      margin-bottom: 20px;
      color: #303133;
    }

    p {
      margin-bottom: 10px;
      color: #606266;
    }
  }
}
</style>

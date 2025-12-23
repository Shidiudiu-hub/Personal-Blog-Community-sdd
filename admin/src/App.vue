<template>
  <ElConfigProvider :size="size" :z-index="zIndex">
    <router-view></router-view>
  </ElConfigProvider>
</template>

<script setup>
import { ElConfigProvider } from 'element-plus'
import { reactive, toRefs, computed } from 'vue'
import { useSettingsStore } from '@/store/settings'

const settingsStore = useSettingsStore()

const state = reactive({
  zIndex: 3000
})

const { zIndex } = toRefs(state)

const size = computed(() => settingsStore.size)
</script>

<style lang="scss">
html,
body,
#app {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>

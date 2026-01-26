<template>
  <template v-if="!item.hidden && hasPermission">
    <template
      v-if="
        hasOneShowingChild(item.children, item) &&
        (!onlyOneChild.children || onlyOneChild.noShowingChildren) &&
        !item.alwaysShow
      "
    >
      <AppLink v-if="onlyOneChild.meta && checkChildPermission(onlyOneChild)" :to="resolvePath(onlyOneChild.path)">
        <el-menu-item :index="resolvePath(onlyOneChild.path)">
          <Item v-if="onlyOneChild.meta.icon || item.meta" :icon="onlyOneChild.meta.icon || item.meta.icon" />
          <template #title>{{ onlyOneChild.meta?.title }}</template>
        </el-menu-item>
      </AppLink>
    </template>

    <el-sub-menu v-else :index="resolvePath(item.path)">
      <template v-if="item.meta" #title>
        <Item :icon="item.meta.icon" />
        <span>{{ item.meta.title }}</span>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </template>
</template>

<script setup>
import path from 'path-browserify'
import { isExternal } from '@/utils/validate'
import AppLink from './Link'
import Item from './Item'
import { reactive, toRefs, computed } from 'vue'
import { useUserStore } from '@/store/user'

const props = defineProps({
  // 每一个router Item
  item: {
    type: Object,
    required: true
  },
  // 基础路径，用于拼接
  basePath: {
    type: String,
    default: ''
  }
})

const userStore = useUserStore()

// 检查是否有权限访问该路由
const hasPermission = computed(() => {
  const item = props.item
  if (!item.meta || !item.meta.roles || item.meta.roles.length === 0) {
    return true // 没有角色限制，允许访问
  }
  
  if (!userStore.userInfo) {
    return false // 未登录，拒绝访问
  }
  
  const userRole = userStore.userInfo.role
  return item.meta.roles.includes(userRole)
})

const state = reactive({
  onlyOneChild: null
})

const { onlyOneChild } = toRefs(state)

// 检查子路由是否有权限
const checkChildPermission = (child) => {
  if (!child.meta || !child.meta.roles || child.meta.roles.length === 0) {
    return true
  }
  if (!userStore.userInfo) {
    return false
  }
  const userRole = userStore.userInfo.role
  return child.meta.roles.includes(userRole)
}

const hasOneShowingChild = (children = [], parent) => {
  const showingChildren = children.filter(item => {
    if (item.hidden) {
      return false
    }
    // 检查权限
    if (!checkChildPermission(item)) {
      return false
    }
    // Temp set(will be used if only has one showing child)
    state.onlyOneChild = item
    return true
  })

  // When there is only one child router, the child router is displayed by default
  if (showingChildren.length === 1) {
    return true
  }

  // Show parent if there are no child router to display
  if (showingChildren.length === 0) {
    state.onlyOneChild = { ...parent, path: '', noShowingChildren: true }
    return true
  }

  return false
}

/**
 * @method resolvePath 处理路径
 * @param {*} routePath
 */
const resolvePath = routePath => {
  if (isExternal(routePath)) {
    return routePath
  }
  if (isExternal(props.basePath)) {
    return props.basePath
  }
  return path.resolve(props.basePath, routePath)
}
</script>
<style lang="scss" scoped>
a:-webkit-any-link {
  text-decoration: none !important;
}
</style>

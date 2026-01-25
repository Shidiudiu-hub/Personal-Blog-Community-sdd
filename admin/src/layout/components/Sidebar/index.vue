<template>
  <div class="sidebar-container">
    <el-scrollbar class="vertical-menus-scrollbar">
      <div class="sidebar-logo">
        <svg-icon name="dashboard" class="logo-icon" />
        <span class="logo-text">个人博客社区</span>
      </div>
      <el-menu mode="vertical" :default-active="activeMenu" :collapse="menuCollapse"
        :background-color="menuBackgroundColor" :text-color="menuTextColor" :active-text-color="menuActiveTextColor"
        :unique-opened="menuUniqueOpened" class="vertical-menus">
        <SidebarItem v-for="route in routes" :key="route.path" :item="route" :base-path="route.path" />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import { useSettingsStore } from '@/store/settings'
import { usePermissionStore } from '@/store/permission'
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import SidebarItem from '@/layout/components/Sidebar/SidebarItem'
import SvgIcon from '@/components/SvgIcon/index.vue'

const settingsStore = useSettingsStore()
const showLogo = computed(() => settingsStore.menuLogo)
const menuCollapse = computed(() => settingsStore.menuCollapse)
const menuBackgroundColor = computed(() => settingsStore.menuBackgroundColor)
const menuTextColor = computed(() => settingsStore.menuTextColor)
const menuActiveTextColor = computed(() => settingsStore.menuActiveTextColor)
const menuUniqueOpened = computed(() => settingsStore.menuUniqueOpened)
const menuActiveBackgroundColor = computed(() => settingsStore.menuActiveBackgroundColor)
const layoutMode = computed(() => settingsStore.layoutMode)
const menuWidth = computed({
  get() {
    return settingsStore.menuCollapse ? '64px' : `${settingsStore.menuWidth}px`
  }
})

const menuHeight = computed({
  get() {
    let menuHeight = ''
    // 默认布局
    switch (layoutMode.value) {
      case 'Default':
        // logo 50
        menuHeight = showLogo.value ? `calc(100% - 50px)` : '100%'
        break
      case 'Classic':
        menuHeight = showLogo.value ? 'calc(100vh - 50px)' : 'calc(100vh - 0px)'
        break
      default:
        break
    }
    return menuHeight
  }
})

const route = useRoute()
const activeMenu = computed(() => {
  const { meta, path } = route
  // if set path, the sidebar will highlight the path you set
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})

const permissionStore = usePermissionStore()
const routes = computed(() => permissionStore.routes)
</script>

<style lang="scss" scoped>
.sidebar-logo {
  height: 66px;
  line-height: 66px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 15px;

  .logo-icon {
    width: 24px;
    height: 24px;
    margin-right: 8px;
    flex-shrink: 0;
  }

  .logo-text {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.sidebar-container {
  transition: width 0.3s;
  width: v-bind(menuWidth);
  background-color: v-bind(menuBackgroundColor);

  .vertical-menus-scrollbar {
    height: v-bind(menuHeight);

    .vertical-menus {
      border: none;
      user-select: none;
    }
  }
}
</style>

<style lang="scss">
.vertical-menus {

  .el-sub-menu .svg-icon,
  .el-menu-item .svg-icon {
    width: 24px;
  }

  .el-menu-item.is-active {
    background-color: v-bind(menuActiveBackgroundColor);
  }
}
</style>

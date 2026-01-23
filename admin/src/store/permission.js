import { defineStore } from 'pinia'
import { asyncRoutes, constantRoutes } from '@/router'
import { useUserStore } from '@/store/user'
import { setLocalStorageItem, getLocalStorageItem, removeLocalStorageItem } from '@/utils/storage'

/**
 * 检查用户是否有权限访问该路由
 * @param {Object} userInfo 用户信息
 * @param {Object} route 路由对象
 */
function hasPermission(userInfo, route) {
  // 如果没有meta信息，允许访问
  if (!route.meta) {
    return true
  }

  // 检查角色权限
  if (route.meta.roles && route.meta.roles.length > 0) {
    if (!userInfo) {
      return false
    }

    // 获取用户角色，支持单角色和多角色
    const userRoles = Array.isArray(userInfo.roles)
      ? userInfo.roles
      : (userInfo.role !== undefined && userInfo.role !== null
        ? [userInfo.role]
        : [])

    // 如果用户没有角色，拒绝访问
    if (userRoles.length === 0) {
      return false
    }

    // 检查用户角色是否在允许的角色列表中
    const hasRole = route.meta.roles.some(role => userRoles.includes(role))
    return hasRole
  }

  // 如果没有设置角色权限要求，则允许访问
  return true
}

/**
 * 递归过滤异步路由表
 * @param routes asyncRoutes
 * @param userInfo 用户信息
 */
export function filterAsyncRoutes(routes, userInfo) {
  const res = []

  routes.forEach(route => {
    const tmp = { ...route }

    // 检查是否有子路由有权限
    const hasChildPermission = tmp.children && filterAsyncRoutes(tmp.children, userInfo).length > 0

    // 特殊处理：根路径路由应该总是显示
    const isRootRoute = route.path === '/' && route.children && route.children.length > 0

    // 如果当前路由有权限或者子路由有权限，都应该保留当前路由
    if (hasPermission(userInfo, tmp) || hasChildPermission || isRootRoute) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, userInfo)
      }
      res.push(tmp)
    }
  })

  return res
}

export const usePermissionStore = defineStore('permission', {
  state: () => {
    return {
      routes: [],
      addRoutes: []
    }
  },
  actions: {
    /**
     * @method generateRoutes
     */
    generateRoutes() {
      const userStore = useUserStore()

      return new Promise(resolve => {
        // 确保用户信息存在
        if (!userStore.userInfo) {
          this.addRoutes = []
          this.routes = constantRoutes
          // 清除缓存
          removeLocalStorageItem('userRoutes')
          resolve([])
          return
        }

        const accessedRoutes = filterAsyncRoutes(asyncRoutes, userStore.userInfo)
        this.addRoutes = accessedRoutes
        this.routes = constantRoutes.concat(accessedRoutes)

        // 将路由信息保存到本地缓存（递归保存完整路由结构，排除component）
        if (accessedRoutes && accessedRoutes.length > 0) {
          // 递归函数：提取路由信息（排除component等不可序列化的属性）
          const extractRouteInfo = (route) => {
            const routeInfo = {
              path: route.path,
              name: route.name,
              redirect: route.redirect,
              meta: route.meta,
              hidden: route.hidden,
              alwaysShow: route.alwaysShow
            }
            // 如果有子路由，递归提取
            if (route.children && route.children.length > 0) {
              routeInfo.children = route.children.map(child => extractRouteInfo(child))
            }
            return routeInfo
          }

          const routesCache = {
            userRole: userStore.userInfo.role || userStore.userInfo.roles,
            routes: accessedRoutes.map(route => extractRouteInfo(route))
          }
          setLocalStorageItem('userRoutes', routesCache)
        } else {
          removeLocalStorageItem('userRoutes')
        }

        resolve(accessedRoutes)
      })
    },

    /**
     * 从缓存恢复路由
     */
    restoreRoutesFromCache() {
      const userStore = useUserStore()

      // 检查缓存中是否有路由信息
      const routesCache = getLocalStorageItem('userRoutes')

      if (!routesCache || !userStore.userInfo) {
        return []
      }

      // 检查用户角色是否匹配
      const userRole = userStore.userInfo.role || (userStore.userInfo.roles && userStore.userInfo.roles[0])
      const cacheRole = routesCache.userRole

      // 角色不匹配，清除缓存
      if (userRole !== cacheRole) {
        removeLocalStorageItem('userRoutes')
        return []
      }

      // 从缓存恢复路由（重新生成以确保组件正确加载）
      const accessedRoutes = filterAsyncRoutes(asyncRoutes, userStore.userInfo)
      this.addRoutes = accessedRoutes
      this.routes = constantRoutes.concat(accessedRoutes)

      return accessedRoutes
    },

    /**
     * 重置路由
     */
    resetRoutes() {
      this.routes = []
      this.addRoutes = []
      removeLocalStorageItem('userRoutes')
    }
  }
})

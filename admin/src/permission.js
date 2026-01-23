import router from './router'
import { useUserStore } from '@/store/user'
import { usePermissionStore } from '@/store/permission'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getCookieItem } from './utils/storage'
import getPageTitle from './utils/get-page-title'
import { nextTick } from 'vue'

// 路由添加标记，防止重复添加
let isRoutesAdded = false

// 重置路由标记的函数，供 resetRouter 调用
window.__resetRoutesFlag = () => {
  isRoutesAdded = false
}

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/auth-redirect', '/register']

router.beforeEach(async (to, from, next) => {
  NProgress.start()
  document.title = getPageTitle(to.meta.title)
  const hasToken = getCookieItem('token') || ''
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  if (hasToken) {
    if (to.path === '/login') {
      // 如果已经登录，访问登录页则重定向到首页
      next({ path: '/' })
      NProgress.done()
    } else {
      // 先检查目标路由是否已存在（可能在刷新前已添加）
      let toRoute = router.resolve(to)

      // 如果路由已经添加过（全局标记）且路由匹配成功，直接通过
      // 但要排除404路由（/:pathMatch(.*)*）
      if (isRoutesAdded && toRoute.matched.length > 0) {
        // 检查是否匹配到了404路由
        const is404Route = toRoute.matched.some(m => m.path === '/:pathMatch(.*)*' || m.path.includes('404'))
        if (!is404Route) {
          next()
          NProgress.done()
          return
        }
      }

      try {
        let accessRoutes = []
        const hasRoles = userStore.userInfo && (userStore.userInfo.role !== undefined || (userStore.userInfo.roles && userStore.userInfo.roles.length > 0))

        // 如果路由未添加或路由不匹配，尝试恢复或生成路由
        // 优先从缓存恢复（适用于刷新场景）
        if (hasRoles) {
          accessRoutes = permissionStore.restoreRoutesFromCache()
        }

        // 如果缓存中没有，则重新生成
        if (accessRoutes.length === 0 && hasRoles) {
          accessRoutes = await permissionStore.generateRoutes()
        }

        // 记录路由是否是新添加的（用于后续导航判断）
        const routesWereAdded = accessRoutes && accessRoutes.length > 0 && !isRoutesAdded

        // 只有当有路由需要添加时才添加
        if (accessRoutes && accessRoutes.length > 0) {
          // 先移除可能存在的旧路由（防止重复添加）
          accessRoutes.forEach(route => {
            if (route.name) {
              try {
                router.removeRoute(route.name)
              } catch (e) {
                // 忽略移除失败的情况
              }
            }
            router.addRoute(route)
          })
          // 标记路由已添加
          isRoutesAdded = true

          // 等待一个tick，确保路由完全注册
          await nextTick()
        }

        // 添加路由后，重新检查目标路由是否存在
        // 等待路由完全注册
        await nextTick()
        toRoute = router.resolve(to)

        // 检查是否匹配到了404路由（通配符路由）
        const is404Route = toRoute.matched.some(m => {
          const path = m.path
          return path === '/:pathMatch(.*)*' || path.includes('404') || path === '/:pathMatch(.*)'
        })

        if (toRoute.matched.length === 0 || is404Route) {
          // 如果路由不存在或匹配到了404路由，说明路由未正确添加
          // 尝试重新解析路由，可能需要等待一下
          await nextTick()
          toRoute = router.resolve(to)
          const retryIs404 = toRoute.matched.some(m => m.path === '/:pathMatch(.*)*' || m.path.includes('404'))

          if (retryIs404 || toRoute.matched.length === 0) {
            next({ path: '/', replace: true })
            NProgress.done()
            return
          }
        }

        // 路由存在且不是404路由，直接通过
        // 如果路由是新添加的，使用replace方式重新导航以确保路由正确激活
        // 这样可以避免刷新时404路由优先级问题
        if (routesWereAdded) {
          next({ ...to, replace: true })
        } else {
          // 直接通过，让Vue Router处理导航
          next()
        }
        NProgress.done()
      } catch (error) {
        await userStore.resetToken()
        ElMessage.error(error.message || 'Has Error')
        next(`/login?redirect=${to.path}`)
        NProgress.done()
      }
    }
  } else {
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单，直接进入
      next()
    } else {
      // 其他页面需要登录后才能访问
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

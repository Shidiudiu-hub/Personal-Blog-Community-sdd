import { useUserStore } from '@/store/user'
import { hasRole } from '@/utils/permission'

export default app => {
  app.directive('hasPermission', {
    mounted(el, binding) {
      const userStore = useUserStore()
      const { value } = binding
      const userInfo = userStore.userInfo

      if (!userInfo) {
        el.parentNode?.removeChild(el)
        return
      }

      // 检查角色权限
      if (value) {
        const hasPermission = hasRole(userInfo, value)
        if (!hasPermission) {
          el.parentNode?.removeChild(el)
        }
      }
    },

    updated(el, binding) {
      // 更新时也执行权限检查
      const userStore = useUserStore()
      const { value } = binding
      const userInfo = userStore.userInfo

      if (!userInfo) {
        el.parentNode?.removeChild(el)
        return
      }

      // 检查角色权限
      if (value) {
        const hasPermission = hasRole(userInfo, value)
        if (!hasPermission) {
          el.parentNode?.removeChild(el)
        }
      }
    }
  })
}

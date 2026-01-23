import { defineStore } from 'pinia'
import { setCookieItem, getCookieItem, removeCookieItem } from '@/utils/storage'
import { resetRouter } from '@/router/index'
import { useTagsViewStore } from '@/store/tagsView'
import { usePermissionStore } from '@/store/permission'

export const useUserStore = defineStore('user', {
  state: () => {
    const token = getCookieItem('token') || ''
    const userInfo = getCookieItem('userInfo') || null

    return {
      token: token,
      userInfo: userInfo,
    }
  },
  actions: {
    setToken({ token }) {
      this.token = token
      setCookieItem('token', token)
    },
    setUserInfo({ userInfo }) {
      this.userInfo = userInfo
      setCookieItem('userInfo', userInfo)
    },
    logout() {
      const tagsViewStore = useTagsViewStore()
      const permissionStore = usePermissionStore()
      return new Promise(resolve => {
        this.token = ''
        this.userInfo = null

        removeCookieItem('token')
        removeCookieItem('userInfo')

        resetRouter()

        // 重置权限存储中的路由信息
        permissionStore.resetRoutes()

        tagsViewStore.delAllViews()

        resolve()
      })
    },
    resetToken() {
      const permissionStore = usePermissionStore()
      return new Promise(resolve => {
        this.token = ''

        // 不再清除userInfo，因为登录时就不保存userInfo了
        removeCookieItem('token')

        // 重置权限存储中的路由信息
        permissionStore.resetRoutes()

        resolve()
      })
    }
  }
})

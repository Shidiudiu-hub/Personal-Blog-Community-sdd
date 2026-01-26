import { createRouter, createWebHashHistory } from 'vue-router'
import Layout from '@/layout/layout'
import { usePermissionStore } from '@/store/permission'

/**
 * 当设置 true 的时候该路由不会在侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 *
 * hidden: true
 *
 * 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 * 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 * 若你想不管路由下面的 children 声明的个数都显示你的根路由
 * 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 *
 * alwaysShow: true
 *
 * 因为路由的设计模式，只能当路由为目录时，才可设置此选项，或者当当前路由只有一个菜单的时候，会自动生成外层目录，如果设置了noRedirect，则不会在breadcrumb中重定向
 *
 * redirect: 'noRedirect'
 *
 * 用于退出登录时清空动态添加的路由信息，路由的name字段必须为路由的文件名，缓存页面时需要
 * 在 3.2.34 或以上的版本中，使用 <script setup> 的单文件组件会自动根据文件名生成对应的 name 选项，无需再页面上手动声明name。
 * name必须和你的文件名保持一致，否则会导致不缓存, 所以文件名不要使用index
 *
 * name:'router-name'
 *
 * meta : {
    设置该路由在侧边栏和面包屑中展示的名字
    title: 'title'

    设置该路由的图标
    icon: 'menu'

    如果设置为true，则不会被 <keep-alive> 缓存(默认 true)
    noCache: true

    如果设置为true，它则会固定在tags-view中(默认 false, 只在经典布局中展示)
    affix: false

    如果设置为false，则不会在breadcrumb面包屑中显示(默认 true，只在经典布局中展示)
    breadcrumb: false

    当路由设置了该属性，则会高亮相对应的侧边栏。
    这在某些场景非常有用，比如：一个文章的列表页路由为：/article/list
    点击文章进入文章详情页，这时候路由为/article/1，但你想在侧边栏高亮文章列表的路由，就可以进行如下设置
    activeMenu: '/example/list'如果设置了path，侧边栏会高亮显示你设置的路径
  }
 */

/**
 * 代表那些不需要动态判断权限的路由，如登录页、404、等通用页面。
 * 没有权限要求的页面
 * 所有角色都可以访问
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register/index'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: {
      title: '首页',
      icon: 'dashboard'
    },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '首页',
          icon: 'dashboard'
        }
      },
      {
        path: 'article/publish',
        name: 'ArticlePublish',
        component: () => import('@/views/article/publish.vue'),
        meta: {
          title: '发布文章',
          icon: 'edit'
        }
      },
      {
        path: 'article/my',
        name: 'ArticleMy',
        component: () => import('@/views/article/my.vue'),
        meta: {
          title: '我的文章',
          icon: 'list'
        }
      },
      {
        path: 'article/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/article/detail.vue'),
        meta: {
          title: '文章详情',
          icon: 'documentation'
        },
        hidden: true
      },
      {
        path: 'user/likes',
        name: 'UserLikes',
        component: () => import('@/views/user/likes.vue'),
        meta: {
          title: '我的点赞',
          icon: 'star'
        }
      },
      {
        path: 'user/collects',
        name: 'UserCollects',
        component: () => import('@/views/user/collects.vue'),
        meta: {
          title: '我的收藏',
          icon: 'collection'
        }
      },
      {
        path: 'user/settings',
        name: 'UserSettings',
        component: () => import('@/views/user/settings.vue'),
        meta: {
          title: '个人设置',
          icon: 'setting'
        }
      },
      {
        path: 'user/:id',
        name: 'UserProfile',
        component: () => import('@/views/user/profile.vue'),
        meta: {
          title: '用户主页',
          icon: 'user'
        },
        hidden: true
      },
      {
        path: 'admin/category',
        name: 'AdminCategory',
        component: () => import('@/views/admin/category.vue'),
        meta: {
          title: '分类管理',
          icon: 'list',
          roles: [1] // 仅管理员可访问
        }
      },
      {
        path: 'admin/tag',
        name: 'AdminTag',
        component: () => import('@/views/admin/tag.vue'),
        meta: {
          title: '标签管理',
          icon: 'price-tag',
          roles: [1] // 仅管理员可访问
        }
      }
    ]
  },
  // 404路由必须放在最后，确保动态路由优先匹配
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
]

/**
 * 有权限要求的页面
 * 只有登录用户才可以访问这些页面
 * 所有业务页面都需要登录权限
 */
export const asyncRoutes = []

const router = createRouter({
  history: createWebHashHistory(),
  routes: constantRoutes
})

/**
 * @method resetRouter
 */
export const resetRouter = () => {
  const permissionStore = usePermissionStore()

  // 移除所有动态添加的路由
  permissionStore.addRoutes.forEach(route => {
    const { name } = route
    if (name) {
      router.removeRoute(name)
    }
  })

  // 清空权限存储中的路由信息
  permissionStore.resetRoutes()

  // 重置路由添加标记（需要在 permission.js 中访问，这里通过 window 传递）
  if (window.__resetRoutesFlag) {
    window.__resetRoutesFlag()
  }
}

export default router

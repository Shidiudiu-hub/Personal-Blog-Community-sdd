# 项目模板 - 前端管理后台

## 项目简介

这是一个基于 Vue 3 + Element Plus 的前端管理后台模板项目，提供了基础的认证、权限管理等功能。

### 前端技术栈

- **框架**: Vue 3
- **构建工具**: Vite
- **包管理**: pnpm
- **UI 框架**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP 客户端**: Axios
- **样式**: SCSS

## 项目结构

```
admin/
├── src/
│   ├── api/              # 接口请求
│   ├── components/       # 公共组件
│   ├── directives/       # 自定义指令
│   ├── hooks/            # 自定义Hooks
│   ├── layout/           # 布局组件
│   ├── router/           # 路由配置
│   ├── store/            # 状态管理
│   ├── styles/           # 全局样式
│   ├── utils/            # 工具函数
│   └── views/            # 页面组件
├── public/               # 静态资源
└── index.html            # 入口HTML
```

### 前端环境

- Node.js >= 18
- pnpm >= 9.6.0

### 前端安装

```bash
# 进入前端目录
cd admin

# 安装依赖
pnpm install

# 开发环境运行
pnpm run dev

# 生产环境构建
pnpm run build
```

### 环境配置

- 开发环境配置: `.env.development`
- 生产环境配置: `.env.production`

## 功能说明

- 用户登录/注册
- 权限管理
- 路由权限控制
- 基础布局组件

更多功能请根据实际需求进行开发。

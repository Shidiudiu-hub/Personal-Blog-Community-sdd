<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="login-welcome">
          <h1 class="welcome-title">个人博客社区</h1>
          <p class="welcome-subtitle">分享知识，交流思想</p>
          <div class="welcome-description">
            <p>发布你的文章，分享你的见解</p>
            <p>与社区成员互动交流</p>
          </div>
        </div>
      </div>
      <div class="login-right">
        <el-form ref="refLoginForm" :model="loginForm" :rules="loginRules" class="login-form animated-form"
          autocomplete="on" label-width="80px">
          <div class="title-container">
            <h3 class="title">用户登录</h3>
            <p class="subtitle">Login to Your Account</p>
          </div>

          <el-form-item label="用户名" prop="username">
            <el-input ref="refUsername" v-model="loginForm.username" placeholder="请输入用户名" name="username" type="text"
              tabindex="1" autocomplete="on" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input ref="refPassword" v-model="loginForm.password" :type="passwordType" placeholder="请输入密码"
              name="password" tabindex="2" autocomplete="on" @keyup.enter="handleLogin" />
            <span class="show-pwd" @click="showPwd()">
              <svg-icon :name="passwordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>

          <el-button :loading="loading" type="primary" size="large" class="login-button" @click.prevent="handleLogin">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>

          <div class="tips">
            <span class="link-type" @click="goRegister">还没有账号？立即注册</span>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { nextTick, reactive, toRefs } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useRouter, useRoute } from 'vue-router'
import AuthApi from '@/api/AuthApi'

const router = useRouter()
const route = useRoute()

const state = reactive({
  refLoginForm: null,
  refUsername: null,
  refPassword: null,
  loginForm: {
    username: '',
    password: ''
  },
  loginRules: {
    username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
    password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
  },
  passwordType: 'password',
  loading: false,
  showDialog: false,
  redirect: undefined,
  otherQuery: {}
})

const { refLoginForm, refUsername, refPassword, loginForm, loginRules, passwordType, loading } = toRefs(state)

const getOtherQuery = query => {
  return Object.keys(query).reduce((acc, cur) => {
    if (cur !== 'redirect') {
      acc[cur] = query[cur]
    }
    return acc
  }, {})
}

const showPwd = () => {
  if (state.passwordType === 'password') {
    state.passwordType = ''
  } else {
    state.passwordType = 'password'
  }
  nextTick(() => {
    if (state.refPassword) {
      state.refPassword.focus()
    }
  })
}

const handleLogin = async () => {
  if (!state.refLoginForm) return
  await state.refLoginForm.validate(async valid => {
    if (valid) {
      state.loading = true
      try {
        const response = await AuthApi.login({
          username: state.loginForm.username,
          password: state.loginForm.password
        })

        // 响应拦截器已经处理了status !== 0的情况，这里直接使用data
        const userStore = useUserStore()
        const loginData = response.data
        const token = loginData?.token
        const userInfo = loginData?.userInfo

        if (!token) {
          ElMessage.error('登录失败：未获取到token')
          state.loading = false
          return
        }

        userStore.setToken({ token })
        if (userInfo) {
          userStore.setUserInfo({ userInfo })
        }

        state.loading = false
        ElMessage.success('登录成功')

        // 根据用户角色跳转
        setTimeout(() => {
          router.push('/')
        })
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
        state.loading = false
      }
    }
  })
}

const goRegister = () => {
  router.push({ path: '/register', query: state.otherQuery })
}

const init = () => {
  const { query } = route
  if (query) {
    state.redirect = query.redirect
    state.otherQuery = getOtherQuery(query)
  }
}

init()
</script>
<style lang="scss" scoped>
$bg: #f5f7fa;
$dark_gray: #889aa4;
$light_gray: #333;
$primary-color: #409eff;
$secondary-color: #67c23a;
$welcome-bg: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  .login-box {
    display: flex;
    width: 900px;
    height: 500px;
    background: #fff;
    border-radius: 10px;
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .login-left {
      flex: 1;
      background: $welcome-bg;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;

      .login-welcome {
        text-align: center;
        padding: 40px;

        .welcome-title {
          font-size: 32px;
          font-weight: bold;
          margin-bottom: 20px;
          letter-spacing: 2px;
        }

        .welcome-subtitle {
          font-size: 18px;
          margin-bottom: 30px;
          opacity: 0.9;
        }

        .welcome-description {
          font-size: 16px;
          line-height: 1.8;
          opacity: 0.8;
        }
      }
    }

    .login-right {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px;

      .login-form {
        width: 100%;
        max-width: 350px;

        .title-container {
          text-align: center;
          margin-bottom: 40px;

          .title {
            font-size: 28px;
            color: #333;
            margin: 0 auto 10px;
            font-weight: bold;
          }

          .subtitle {
            font-size: 14px;
            color: $dark_gray;
            margin: 0;
          }
        }

        .svg-container {
          padding: 6px 5px 6px 15px;
          color: $dark_gray;
          vertical-align: middle;
          display: inline-block;
        }

        .show-pwd {
          position: absolute;
          right: 10px;
          top: 0px;
          font-size: 16px;
          color: $dark_gray;
          cursor: pointer;
          user-select: none;
        }
      }
    }
  }

  .tips {
    font-size: 14px;
    color: #666;
    margin-top: 20px;
    text-align: center;

    .link-type {
      color: $primary-color;
      cursor: pointer;
      transition: color 0.3s;
      font-weight: 500;

      &:hover {
        color: #66b1ff;
        text-decoration: underline;
      }
    }
  }

  .login-button {
    width: 100%;
    background: $primary-color;
    border-color: $primary-color;
    margin-top: 20px;
    transition: all 0.3s;
    height: 45px;
    font-size: 16px;

    &:hover {
      background: #66b1ff;
      border-color: #66b1ff;
      transform: translateY(-2px);
      box-shadow: 0 15px 30px rgba(24, 144, 255, 0.3);
    }
  }

  .animated-form {
    animation: form-appear 0.8s ease-out;
  }

  @keyframes form-appear {
    from {
      opacity: 0;
      transform: translateY(-20px);
    }

    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
}
</style>

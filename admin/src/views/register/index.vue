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
        <el-form ref="refRegisterForm" :model="registerForm" :rules="registerRules" class="login-form animated-form"
          autocomplete="on" label-width="100px">
          <div class="title-container">
            <h3 class="title">用户注册</h3>
            <p class="subtitle">Create Your Account</p>
          </div>

          <el-form-item label="用户名" prop="username">
            <el-input ref="refUsername" v-model="registerForm.username" placeholder="请输入用户名" name="username" type="text"
              autocomplete="on" />
          </el-form-item>

          <el-form-item label="密码" prop="password">
            <el-input ref="refPassword" v-model="registerForm.password" :type="passwordType" placeholder="请输入密码（6-32位）"
              name="password" autocomplete="on" />
            <span class="show-pwd" @click="showPwd()">
              <svg-icon :name="passwordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>

          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" name="realName" type="text" />
          </el-form-item>

          <el-form-item label="角色">
            <el-select v-model="registerForm.role" placeholder="请选择角色（默认普通用户）" style="width: 100%">
              <el-option label="普通用户" :value="0" />
              <el-option label="管理员" :value="1" />
            </el-select>
          </el-form-item>


          <el-form-item label="手机号" prop="phone">
            <el-input v-model="registerForm.phone" maxlength="11" placeholder="请输入手机号（选填）" name="phone" type="text" />
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="registerForm.email" maxlength="50" placeholder="请输入邮箱（选填）" name="email" type="text" />
          </el-form-item>

          <el-button :loading="loading" type="primary" size="large" class="register-button"
            @click.prevent="handleRegister">
            {{ loading ? '注册中...' : '注册' }}
          </el-button>

          <div class="tips">
            <span class="link-type" @click="goLogin">已有账号？立即登录</span>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { nextTick, reactive, toRefs } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import AuthApi from '@/api/AuthApi'

const router = useRouter()

const state = reactive({
  refRegisterForm: null,
  refUsername: null,
  refPassword: null,
  registerForm: {
    username: '',
    password: '',
    realName: '',
    role: null,
    phone: '',
    email: ''
  },
  registerRules: {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 1, max: 50, message: '用户名长度限制1~50', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 32, message: '密码长度限制6~32', trigger: 'blur' }
    ],
    realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
    email: [
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
  },
  passwordType: 'password',
  loading: false
})

const { refRegisterForm, refUsername, refPassword, registerForm, registerRules, passwordType, loading } = toRefs(state)

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

const handleRegister = async () => {
  if (!state.refRegisterForm) return
  await state.refRegisterForm.validate(async valid => {
    if (valid) {
      state.loading = true
      try {
        const data = {
          username: state.registerForm.username.trim(),
          password: state.registerForm.password,
          realName: state.registerForm.realName.trim()
        }

        // 角色可选，如果不选则默认为普通用户(0)
        if (state.registerForm.role !== null && state.registerForm.role !== undefined) {
          data.role = state.registerForm.role
        }

        // 手机号和邮箱：只有非空字符串才发送，避免发送空字符串导致验证失败
        const phone = state.registerForm.phone?.trim()
        if (phone) {
          data.phone = phone
        }
        const email = state.registerForm.email?.trim()
        if (email) {
          data.email = email
        }

        // 响应拦截器已经处理了status !== 0的情况
        await AuthApi.register(data)

        ElMessage.success('注册成功，请登录')
        state.loading = false
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
        state.loading = false
      }
    }
  })
}

const goLogin = () => {
  router.push('/login')
}
</script>

<style lang="scss" scoped>
$bg: #f5f7fa;
$dark_gray: #889aa4;
$light_gray: #333;
$primary-color: #409eff;
$secondary-color: #67c23a;
$welcome-bg: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

.register-container {
  min-height: 100vh;
  width: 100%;
  background-color: $bg;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  .register-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 40px 35px;
    box-sizing: border-box;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 8px;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.3);
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    display: inline-block;
    width: 30px;
  }

  .title-container {
    position: relative;
    margin-bottom: 40px;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 10px auto;
      text-align: center;
      font-weight: bold;
    }

    .subtitle {
      font-size: 16px;
      color: $dark_gray;
      margin: 0px auto 0px auto;
      text-align: center;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}

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
    height: 650px;
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
          margin-bottom: 30px;

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
          top: 0;
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

  .register-button {
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

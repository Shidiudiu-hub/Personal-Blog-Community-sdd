<template>
  <div class="user-settings">
    <el-card>
      <template #header>
        <span>个人设置</span>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="profile">
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px" style="max-width: 500px">
            <el-form-item label="头像">
              <div class="avatar-upload">
                <el-avatar :size="80" :src="profileForm.avatar">{{ profileForm.username?.charAt(0) }}</el-avatar>
                <el-input v-model="profileForm.avatar" placeholder="头像URL" style="margin-left: 16px; flex: 1" />
              </div>
            </el-form-item>
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input v-model="profileForm.bio" type="textarea" :rows="3" placeholder="介绍一下自己吧" maxlength="200" show-word-limit />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px" style="max-width: 500px">
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword" :loading="changingPassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import UserApi from '@/api/UserApi'

const userStore = useUserStore()
const activeTab = ref('profile')
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const saving = ref(false)
const changingPassword = ref(false)

const profileForm = reactive({
  username: '',
  avatar: '',
  email: '',
  phone: '',
  bio: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function loadProfile() {
  try {
    const res = await UserApi.getProfile()
    const data = res?.data
    if (data) {
      profileForm.username = data.username || ''
      profileForm.avatar = data.avatar || ''
      profileForm.email = data.email || ''
      profileForm.phone = data.phone || ''
      profileForm.bio = data.bio || ''
    }
  } catch (e) {
    console.error('加载个人信息失败:', e)
  }
}

async function saveProfile() {
  try {
    await profileFormRef.value?.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    await UserApi.updateProfile(profileForm)
    ElMessage.success('保存成功')
    // 更新store中的用户信息
    if (userStore.userInfo) {
      userStore.setUserInfo({
        userInfo: {
          ...userStore.userInfo,
          username: profileForm.username,
          avatar: profileForm.avatar
        }
      })
    }
  } catch (e) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  try {
    await passwordFormRef.value?.validate()
  } catch {
    return
  }
  changingPassword.value = true
  try {
    await UserApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (e) {
    ElMessage.error(e?.message || '修改失败')
  } finally {
    changingPassword.value = false
  }
}

onMounted(loadProfile)
</script>

<style lang="scss" scoped>
.user-settings {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.avatar-upload {
  display: flex;
  align-items: center;
}
</style>

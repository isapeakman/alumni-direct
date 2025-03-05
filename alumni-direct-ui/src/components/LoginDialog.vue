<template>
  <el-dialog
      :model-value="visible"
      @update:model-value="emit('update:visible', $event)"
      :title="isLogin ? '登录' : '注册'"
      width="400px"
      :close-on-click-modal="false"
      @close="handleClose"
  >
    <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="login-form"
    >


      <!-- 邮箱登录 -->
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱">
          <template #prefix>
            <el-icon>
              <Message/>
            </el-icon>
          </template>
        </el-input>
        </el-form-item>


      <!-- 密码输入 -->
      <el-form-item label="密码" prop="password">
        <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
        >
          <template #prefix>
            <el-icon>
              <Lock/>
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
    </el-form>

    <el-form-item v-if="!isLogin" label="确认密码" prop="password">
      <el-input
          v-model="form.confirmPassword"
          type="password"
          placeholder="请确认密码"
          show-password
      >
        <template #prefix>
          <el-icon>
            <Lock/>
          </el-icon>
        </template>
      </el-input>
    </el-form-item>

    <el-form-item v-if="!isLogin" label="验证码" prop="smsCode">
      <el-input
          v-model="form.smsCode"
          type="input"
          placeholder="请输入验证码"
          clearable
      >
      </el-input>
    </el-form-item>


    <template #footer>
      <div class="dialog-footer">
        <div class="switch-type">
          <el-link type="primary" @click="switchLoginType">
            {{ isLogin ? '没有账号？立即注册' : '已有账号？立即登录' }}
          </el-link>
        </div>
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">
          {{ isLogin ? '登录' : '注册' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {Iphone, Message, Lock} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import {login, register} from '@/api/user'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['update:visible', 'success'])

const isLogin = ref(true)
const countdown = ref(0)
const formRef = ref(null)

const form = reactive({
  email: '',
  password: '',
  confirmPassword: '',
  smsCode: ''
})

// 表单验证规则
const rules = {
  email: [
    {required: true, message: '请输入邮箱', trigger: 'blur'},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, message: '密码长度不能小于6位', trigger: 'blur'}
  ],
  smsCode: [
    {required: true, message: '请输入验证码', trigger: 'blur'},
    {len: 6, message: '验证码长度应为6位', trigger: 'blur'}
  ]
}

// 发送验证码
const handleSendCode = async () => {
  try {
    await formRef.value.validateField('phone')
    // TODO: 调用发送验证码接口
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
    ElMessage.success('验证码已发送')
  } catch (error) {
    // 验证失败
  }
}

// 切换登录/注册
const switchLoginType = () => {
  isLogin.value = !isLogin.value
  formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    // TODO: 调用登录/注册接口
    const data = {
      email: form.email,
      password: form.password,
    }
    if (!isLogin) {//注册
      if (form.confirmPassword !== form.password) {
        ElMessage.error('两次密码不一致')
        return
      }
      data.confirmPassword = form.confirmPassword
      data.captcha = form.smsCode
    }
    console.log('提交数据:', data)
    isLogin.value ? handleLogin(data) : handleRegister(data)

  } catch (error) {
    // 表单验证失败
  }
}

// 处理登录
const handleLogin = async (data) => {
  const res = await login(data)
  console.log('登录结果:', res)
  const token = res.data.data.token
  console.log('token:', token)
  if (res.data.code === 200) {
    ElMessage.success('登录成功')
    localStorage.setItem('token', token)
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    // emit('success', res.data) // 将用户信息传递给父组件
    handleClose()
    // 刷新页面
    window.location.reload()
  } else {
    ElMessage.error(res.data.message)
  }
}

// 处理注册
const handleRegister = async (data) => {
  const res = await register(data)
  console.log('注册结果:', res)
  if (res.data.code === 200) {
    ElMessage.success('注册成功')
    handleClose()
  } else {
    ElMessage.error(res.data.message)
  }
}


// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
  formRef.value?.resetFields()
  isLogin.value = true
}
</script>

<style lang="scss" scoped>
.login-type-group {
  width: 100%;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;

  :deep(.el-radio-button__inner) {
    width: 120px;
  }
}

.verify-code {
  display: flex;
  gap: 12px;

  .el-input {
    flex: 1;
  }

  .el-button {
    width: 120px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;

  .switch-type {
    flex: 1;
  }
}

:deep(.el-form-item__content) {
  .el-input__wrapper {
    .el-input__prefix {
      margin-left: 5px;
    }
  }
}
</style> 
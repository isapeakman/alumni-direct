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
      <!-- 登录方式切换 -->
      <el-radio-group v-model="loginType" class="login-type-group">
        <el-radio-button label="phone">手机号登录</el-radio-button>
        <el-radio-button label="email">邮箱登录</el-radio-button>
      </el-radio-group>

      <!-- 手机号登录 -->
      <template v-if="loginType === 'phone'">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号">
            <template #prefix>
              <el-icon>
                <Iphone/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="smsCode" v-if="!isLogin">
          <div class="verify-code">
            <el-input v-model="form.smsCode" placeholder="请输入验证码"/>
            <el-button
                type="primary"
                :disabled="countdown > 0"
                @click="handleSendCode"
            >
              {{ countdown > 0 ? `${countdown}s后重试` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </template>

      <!-- 邮箱登录 -->
      <template v-else>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
      </template>

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

const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['update:visible', 'success'])

const isLogin = ref(true)
const loginType = ref('phone')
const countdown = ref(0)
const formRef = ref(null)

const form = reactive({
  phone: '',
  email: '',
  password: '',
  smsCode: ''
})

// 表单验证规则
const rules = {
  phone: [
    {required: true, message: '请输入手机号', trigger: 'blur'},
    {pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur'}
  ],
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
      loginType: loginType.value,
      ...(loginType.value === 'phone'
          ? {phone: form.phone}
          : {email: form.email}),
      password: form.password,
      smsCode: form.smsCode
    }

    console.log('提交数据:', data)
    ElMessage.success(isLogin.value ? '登录成功' : '注册成功')
    emit('success')
    handleClose()
  } catch (error) {
    // 表单验证失败
  }
}

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
  formRef.value?.resetFields()
  isLogin.value = true
  loginType.value = 'phone'
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
<template>

  <el-container>
    <!-- 顶部导航 -->
    <el-header>
      <el-header class="header">
        <div class="header-content">
          <div class="logo">
            <router-link to="/">
              <h1>人才招聘平台</h1>
            </router-link>
          </div>
          <div class="nav-menu">
            <el-menu mode="horizontal" :router="true" :default-active="$route.path">
              <el-menu-item index="/dashboard">首页</el-menu-item>
            </el-menu>
          </div>
          <div class="user-actions">
            <template v-if="!isLoggedIn">
              <el-button type="primary" @click="showLogin">登录</el-button>
              <el-button @click="handleRegister">注册</el-button>
            </template>
            <template v-else>
              <el-button type="primary" @click="showLogin">通知</el-button>
              <el-button type="primary" @click="showLogin">消息</el-button>

              <el-dropdown>
              <span class="user-profile">
                <span class="username">{{ userName }}</span>
                <el-avatar :size="48" :src="avatar"/>
              </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item divided @click="navigateToUser">求职</el-dropdown-item>
                    <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>

                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </div>
        </div>
      </el-header>
    </el-header>
    <!--  侧边导航栏-->
    <el-container>
      <el-aside width="200px">
        <el-row class="tac">
          <el-col :span="3">
            <el-menu
                default-active="/recruitment/manage"
                class="el-menu-vertical-demo"
                router
            >
              <el-menu-item index="/recruitment/manage">
                <el-icon>
                  <icon-menu/>
                </el-icon>
                <span>职位管理</span>
              </el-menu-item>
              <el-menu-item index="/wsTest">
                <el-icon>
                  <document/>
                </el-icon>
                <span>沟通</span>
              </el-menu-item>
              <el-menu-item index="4">
                <el-icon>
                  <setting/>
                </el-icon>
                <span>身份认证</span>
              </el-menu-item>
            </el-menu>
          </el-col>
        </el-row>
      </el-aside>
      <el-main>
        <router-view/>
      </el-main>
    </el-container>
  </el-container>



  <!--  <router-view/>-->

  <!-- 添加登录弹窗组件 -->

  <LoginDialog
      v-model:visible="loginDialogVisible"
  />


</template>

<script setup>
import {
  Document,
  Menu as IconMenu,
  Location,
  Setting,
} from '@element-plus/icons-vue'
import {ref, onMounted, computed} from 'vue'
import LoginDialog from '@/components/LoginDialog.vue'
import router from '@/router/index.js'
import {ElMessage} from 'element-plus'

// 控制登录框显示
const loginDialogVisible = ref(false)
// 用户相关状态
const isLoggedIn = ref(false)
const userName = ref('')
const userInfo = ref(null)
const avatar = ref('')

const navigateToUser = () => {
  router.push('/')
}
// 显示登录框的方法
const showLogin = () => {
  loginDialogVisible.value = true
}
// 监听 localStorage 变化
window.addEventListener('storage', (e) => {
  if (e.key === 'token' || e.key === 'userInfo') {
    checkLoginStatus()
  }
})

// 检查登录状态
const checkLoginStatus = () => {
  const savedUserInfo = localStorage.getItem('userInfo')
  if (savedUserInfo) {
    try {
      userInfo.value = JSON.parse(savedUserInfo)
      isLoggedIn.value = true
      console.log('用户信息:', userInfo.value)
      avatar.value = userInfo.value.data.userAvatar
      console.log('头像:', avatar.value)
      userName.value = userInfo.value.data.nickname || userInfo.value.data.userAccount
      console.log('用户名:', userName.value)
    } catch (e) {
      console.error('解析用户信息失败', e)
    }
  }
}


// 页面加载时检查是否有保存的用户信息
onMounted(() => {
  checkLoginStatus()
})

// 登出处理方法
const handleLogout = () => {
  // 清除用户信息
  userInfo.value = null
  isLoggedIn.value = false
  userName.value = ''

  // 清除本地存储
  localStorage.removeItem('userInfo')
  localStorage.removeItem('token')

  ElMessage.success('退出成功')
}


// 页面加载时检查登录状态
onMounted(() => {
  // 检查 localStorage 中是否有用户信息和 token
  const storedToken = localStorage.getItem('token')
  const storedUserInfo = localStorage.getItem('userInfo')

  if (storedToken && storedUserInfo) {
    const parsedUserInfo = JSON.parse(storedUserInfo)
    userInfo.value = parsedUserInfo
    isLoggedIn.value = true
    userName.value = parsedUserInfo.nickname || parsedUserInfo.userAccount
  } else {
    // 如果 token 或用户信息不完整，清除所有登录状态
    handleLogout()
  }
})
</script>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  position: fixed; // 固定位置
  top: 0; // 顶部对齐
  left: 0; // 左侧对齐
  width: 100%; // 占满宽度
  z-index: 1000; // 确保在其他内容之上
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  background-color: white; // 背景色，防止内容遮挡

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
  }

  .logo {
    h1 {
      margin: 0;
      font-size: 24px;
    }
  }

  .username {
    color: #333;
    font-size: 14px;
    white-space: nowrap;
  }

  .nav-menu {
    flex: 1;
    margin: 0 40px;
  }

  .user-actions {
    display: flex;
    gap: 12px;
    align-items: center;
  }
}
</style>
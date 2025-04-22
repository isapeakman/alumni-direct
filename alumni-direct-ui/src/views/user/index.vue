<template>
  <div class="home-container">
    <div class="common-layout">
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-content">
            <div class="logo">
              <router-link to="/">
                <h1>校友直聘</h1>
              </router-link>
            </div>
            <div class="nav-menu">
              <el-menu mode="horizontal" :router="true" :default-active="$route.path">
                <el-menu-item index="/dashboard">首页</el-menu-item>
                <el-menu-item index="/recommend">推荐职位</el-menu-item>
                <el-menu-item index="/search">搜索职位</el-menu-item>
                <el-menu-item index="/fair">招聘会和宣讲会</el-menu-item>
              </el-menu>
            </div>
            <div class="user-actions">
              <template v-if="!isLoggedIn">
                <el-button type="primary" @click="showLogin">登录</el-button>
                <el-button @click="handleRegister">注册</el-button>
              </template>
              <template v-else>
                <el-button type="primary" @click="showLogin">通知</el-button>
                <el-button type="primary" @click="router.push('/wsTest')">消息</el-button>

                <el-dropdown>
              <span class="user-profile">
                <span class="username">{{ userName }}</span>
                <el-avatar :size="48" :src="avatar"/>
              </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="router.push('/personalCenter')">个人中心</el-dropdown-item>
                      <!--                      <el-dropdown-item>我的简历</el-dropdown-item>-->
                      <el-dropdown-item>投递记录</el-dropdown-item>
                      <!--                      <el-dropdown-item>收藏职位</el-dropdown-item>-->
                      <el-dropdown-item divided @click="navigateToRecruitment">招聘/内推</el-dropdown-item>
                      <el-dropdown-item v-if="role===0" divided @click="navigateToAdmin">管理员登录</el-dropdown-item>
                      <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </div>
          </div>
        </el-header>
        <el-container>
          <el-main>
            <router-view></router-view>
          </el-main>
          <el-footer>power by MR.WU</el-footer>
        </el-container>
      </el-container>
    </div>
  </div>
  <!-- 添加登录弹窗组件 -->
  <LoginDialog
      v-model:visible="loginDialogVisible"
  />


</template>

<script setup>
import {ref, onMounted, computed} from 'vue'
import LoginDialog from '@/components/LoginDialog.vue'
import request from '@/utils/request.js'
import {ElMessage} from 'element-plus'

// 控制登录框显示
const loginDialogVisible = ref(false)
// 用户相关状态
const isLoggedIn = ref(false)
const userName = ref('')
const userInfo = ref(null)
const avatar = ref('')
const role = ref(null)


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
      console.log('用户信息:', userInfo.value)
      isLoggedIn.value = true
      console.log('用户信息:', userInfo.value)
      avatar.value = userInfo.value.data.userAvatar
      console.log('头像:', avatar.value)
      userName.value = userInfo.value.data.nickname || userInfo.value.data.userAccount
      console.log('用户名:', userName.value)
      role.value = userInfo.value.data.role
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


import router from "@/router/index.js";

// 跳转到招聘者页面
const navigateToRecruitment = () => {
  router.push('/recruitment')
}
// 跳转到管理员页面
const navigateToAdmin = () => {
  router.push('/admin')
}
</script>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.el-main {
  margin-top: 10px; /* 假设导航栏高度为60px */
  padding: 50px; /* 可选：添加内边距提升可读性 */
}

.header {
  position: fixed; // 固定位置
  top: 0; // 顶部对齐
  left: 0; // 左侧对齐
  width: 100%; // 占满宽度
  height: 60px;
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
.footer {
  background-color: #2c3e50;
  color: white;
  padding: 40px 0 20px;
  margin-top: auto;


  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    margin-bottom: 40px;
  }

  .footer-section {
    h4 {
      margin-bottom: 20px;
    }

    p {
      margin: 8px 0;
      color: rgba(255, 255, 255, 0.7);
    }

    .phone {
      font-size: 20px;
      color: white;
    }
  }

  .copyright {
    text-align: center;
    color: rgba(255, 255, 255, 0.5);
    padding-top: 20px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }
}
</style>
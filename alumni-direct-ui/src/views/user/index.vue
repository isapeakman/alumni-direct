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
                <el-menu-item index="/dashboard" @click="handleNavClick('/dashboard')">首页</el-menu-item>
                <el-menu-item index="/recommend" @click="handleNavClick('/recommend')">推荐职位</el-menu-item>
                <el-menu-item index="/search" @click="handleNavClick('/search')">搜索职位</el-menu-item>
                <el-menu-item index="/fair" @click="handleNavClick('/fair')">招聘会和宣讲会</el-menu-item>
              </el-menu>
            </div>
            <div class="user-actions">
              <template v-if="!isLoggedIn">
                <el-button type="primary" @click="showLogin(false)">登录</el-button>
                <el-button @click="showLogin(true)">注册</el-button>
              </template>
              <template v-else>
                <el-button type="primary" @click="router.push('/chat')">消息</el-button>

                <el-dropdown>
              <span class="user-profile">
                <span class="username">{{ userName }}</span>
                <el-avatar :size="48" :src="avatar"/>
              </span>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="router.push('/personalCenter')">个人中心</el-dropdown-item>
                      <!--                      <el-dropdown-item>我的简历</el-dropdown-item>-->
                      <el-dropdown-item @click="router.push('/apply')">投递记录</el-dropdown-item>
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
          <el-footer>
            <div class="footer-content">
              <div class="footer-section">
                <h4>校友直聘</h4>
                <ul>
                  <li><a href="/dashboard">首页</a></li>
                  <li><a href="/recommend">推荐职位</a></li>
                  <li><a href="/search">搜索职位</a></li>
                  <li><a href="/fair">招聘会</a></li>
                </ul>
              </div>
              <div class="footer-section">
                <h4>求职者服务</h4>
                <ul>
                  <li><a href="#">简历指导</a></li>
                  <li><a href="#">面试技巧</a></li>
                  <li><a href="/apply">投递记录</a></li>
                  <li><a href="/personalCenter">个人中心</a></li>
                </ul>
              </div>
              <div class="footer-section">
                <h4>企业服务</h4>
                <ul>
                  <li><a href="/recruitment">企业入驻</a></li>
                  <li><a href="#">职位发布</a></li>
                  <li><a href="#">校园招聘</a></li>
                  <li><a href="#">品牌推广</a></li>
                </ul>
              </div>
              <div class="footer-section">
                <h4>联系我们</h4>
                <div class="contact-info">
                  <p class="phone">400-888-8888</p>
                  <p>contact@alumnidirect.com</p>
                  <p>北京市海淀区中关村大街1号</p>
                </div>
              </div>
            </div>
            <div class="copyright">
              © 2024 校友直聘 版权所有 | 京ICP备12345678号
            </div>
          </el-footer>
        </el-container>
      </el-container>
    </div>
  </div>
  <!-- 添加登录弹窗组件 -->
  <LoginDialog
      v-model:visible="loginDialogVisible"
      :is-login="isLogin"
  />
</template>

<script setup>
import {ref, onMounted, computed} from 'vue'
import LoginDialog from '@/components/LoginDialog.vue'
import RegisterDialog from '@/components/RegisterDialog.vue'
import request from '@/utils/request.js'
import {ElMessage} from 'element-plus'
import router from "@/router/index.js";
import {getUserInfo} from "@/api/user.js";

// 控制登录框显示
const loginDialogVisible = ref(false)
const RegisterDialogVisible = ref(false) // 确保初始化为 false

// 用户相关状态
const isLoggedIn = ref(false)
const userName = ref('')
const userInfo = ref(null)
const avatar = ref('')
const role = ref(null)

// 控制登录/注册模式
const isLogin = ref(true)

// 显示登录框的方法
const showLogin = (isRegister = false) => {
  loginDialogVisible.value = true
  isLogin.value = !isRegister
}

// 显示注册框的方法
const showRegister = () => {
  RegisterDialogVisible.value = true // 确保正确设置为 true
  console.log(RegisterDialogVisible.value)
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
onMounted(async () => {
  checkLoginStatus()
  // 保证右上角头像正常刷新
  const response = await getUserInfo()
  avatar.value = response.data.data.userAvatar;
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


// 导航菜单点击处理
const handleNavClick = (path) => {
  console.log('导航点击:', path)
  router.push(path)
}

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
  background: #f8fafc;
}

.el-main {
  flex: 1;
  padding: 0;
  margin-top: 70px;
}

.el-footer {
  background: #1e293b;
  color: #fff;
  padding: 60px 0 30px;
  margin-top: auto;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 70px;
  z-index: 1000;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
    padding: 0 20px;
  }

  .logo {
    a {
      display: flex;
      align-items: center;
      text-decoration: none;
    }

    h1 {
      margin: 0;
      font-size: 24px;
      font-weight: 700;
      background: linear-gradient(135deg, #0ea5e9, #0284c7);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .nav-menu {
    flex: 1;
    margin: 0 60px;

    :deep(.el-menu) {
      border: none;
      background: transparent;
    }

    :deep(.el-menu-item) {
      font-size: 15px;
      color: #475569;
      padding: 0 20px;
      margin: 0 8px;
      border-radius: 8px;
      transition: all 0.3s;

      &:hover {
        color: #0ea5e9;
        background: rgba(14, 165, 233, 0.08);
      }

      &.is-active {
        color: #0ea5e9;
        background: rgba(14, 165, 233, 0.1);
        font-weight: 600;
      }
    }
  }

  .user-actions {
    display: flex;
    gap: 12px;
    align-items: center;

    .username {
      color: #475569;
      font-size: 14px;
      font-weight: 500;
      margin-right: 8px;
      white-space: nowrap;
    }

    .user-profile {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 6px 12px;
      border-radius: 20px;
      transition: background 0.3s;

      &:hover {
        background: #f1f5f9;
      }
    }

    :deep(.el-button) {
      padding: 8px 24px;
      border-radius: 20px;
      font-weight: 500;

      &.el-button--primary {
        background: linear-gradient(135deg, #0ea5e9, #0284c7);
        border: none;

        &:hover {
          background: linear-gradient(135deg, #0284c7, #0369a1);
        }
      }

      &.el-button--default {
        border-color: #e2e8f0;

        &:hover {
          border-color: #0ea5e9;
          color: #0ea5e9;
        }
      }
    }

    :deep(.el-dropdown-menu) {
      border-radius: 12px;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      border: none;
      padding: 8px 0;

      .el-dropdown-item {
        padding: 10px 20px;
        font-size: 14px;
        color: #475569;

        &:hover {
          background: rgba(14, 165, 233, 0.08);
          color: #0ea5e9;
        }

        &.is-divided {
          border-top: 1px solid #f1f5f9;
          margin-top: 4px;
          padding-top: 14px;
        }
      }
    }
  }
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
  margin-bottom: 40px;
  padding: 0 20px;

  .footer-section {
    h4 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 20px;
      color: #fff;
    }

    ul {
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        margin-bottom: 12px;

        a {
          color: #94a3b8;
          text-decoration: none;
          font-size: 14px;
          transition: color 0.3s;

          &:hover {
            color: #0ea5e9;
          }
        }
      }
    }

    .contact-info {
      p {
        margin: 8px 0;
        color: #94a3b8;
        font-size: 14px;
        display: flex;
        align-items: center;
        gap: 8px;
      }

      .phone {
        font-size: 18px;
        font-weight: 600;
        color: #fff;
      }
    }
  }
}

.copyright {
  text-align: center;
  color: #64748b;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 13px;
}
</style>
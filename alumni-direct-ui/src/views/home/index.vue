<template>
  <div class="home-container">
    <!-- 顶部导航 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <h1>人才招聘平台</h1>
          </router-link>
        </div>
        <div class="nav-menu">
          <el-menu mode="horizontal" :router="true">
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/">推荐职位</el-menu-item>
            <el-menu-item index="/">搜索职位</el-menu-item>
            <el-menu-item index="/">招聘会和宣讲会</el-menu-item>
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
                <el-avatar :size="48" :src=avatar/>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>我的简历</el-dropdown-item>
                  <el-dropdown-item>投递记录</el-dropdown-item>
                  <el-dropdown-item>收藏职位</el-dropdown-item>
                  <el-dropdown-item divided>招聘/内推</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>

                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-content">
        <h2>找到理想的工作</h2>
        <div class="search-box">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索职位、公司或地点"
              class="search-input"
          >
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" @click="handleSearch">搜索</el-button>
        </div>
        <div class="hot-tags">
          热门搜索：
          <el-tag
              v-for="tag in hotTags"
              :key="tag"
              class="hot-tag"
              @click="searchKeyword = tag"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 职位推荐部分改为 -->
    <div class="section job-recommend">
      <div class="section-content">
        <h3 class="section-title">最新职位</h3>
        <el-row :gutter="20">
          <el-col :span="6" v-for="job in jobList" :key="job.title">
            <el-card shadow="hover" class="job-card">
              <div class="job-header">
                <h4>{{ job.title }}</h4>
                <div class="salary">{{ formatSalary(job.minSalary, job.maxSalary) }}</div>
              </div>
              <div class="company">{{ job.companyName }}</div>
              <div class="info-row">
              <span class="location">
                <el-icon><Location/></el-icon>
                {{ job.location }}
              </span>
                <span class="job-type">{{ getJobType(job.jobType) }}</span>
              </div>
              <div class="recruiter">
                <el-avatar :size="30" :src=job.recruiterAvatar/>
                <span>{{ job.recruiterName }}·招聘者</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 分页器 -->
        <div class="pagination-container">
          <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[8, 12, 16, 20]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>


    <!-- 页脚 -->
    <el-footer class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h4>关于我们</h4>
          <p>平台介绍</p>
          <p>联系我们</p>
          <p>加入我们</p>
        </div>
        <div class="footer-section">
          <h4>求职者</h4>
          <p>注册简历</p>
          <p>投递记录</p>
          <p>求职攻略</p>
        </div>
        <div class="footer-section">
          <h4>企业服务</h4>
          <p>企业入驻</p>
          <p>企业登录</p>
          <p>招聘服务</p>
        </div>
        <div class="footer-section">
          <h4>客服热线</h4>
          <p class="phone">400-888-8888</p>
          <p>工作日 9:00-18:00</p>
        </div>
      </div>
      <div class="copyright">
        © 2024 人才招聘平台 版权所有
      </div>
    </el-footer>
  </div>
  <!-- 添加登录弹窗组件 -->

  <LoginDialog
      v-model:visible="loginDialogVisible"
  />


</template>

<script setup>
import {ref, onMounted} from 'vue'
import {Search, Location} from '@element-plus/icons-vue'
import LoginDialog from '@/components/LoginDialog.vue'
import request from '@/utils/request'
import {ElMessage} from 'element-plus'
import {getJobCard} from '@/api/job'
import {searchJob} from '@/api/job'
// 控制登录框显示
const loginDialogVisible = ref(false)
// 用户相关状态
const isLoggedIn = ref(false)
const userName = ref('')
const userInfo = ref(null)
const avatar = ref('')
const searchKeyword = ref('')

// 热门标签
const hotTags = ref(['前端开发', 'Java开发', '产品经理', 'UI设计'])

// 企业数据
const companies = ref([
  {id: 1, name: '阿里巴巴', logo: 'https://placeholder.com/100'},
  {id: 2, name: '腾讯', logo: 'https://placeholder.com/100'},
  {id: 3, name: '字节跳动', logo: 'https://placeholder.com/100'},
  // ... 其他企业数据
])

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

const handleSearch = async () => {
  if (searchKeyword.value === '') {
    ElMessage.error('请输入搜索内容')
    return
  }
  // 处理搜索逻辑
  const response = await searchJob(searchKeyword)
  if (response.data.code === 200) {
    jobList.value = response.data.data.records
    total.value = response.data.data.page.total
  } else {
    ElMessage.error(response.data.message || '获取职位列表失败')
  }
}

// 职位列表相关数据
const jobList = ref([])
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

// 获取职位列表
const fetchJobs = async () => {
  try {
    const response = await getJobCard(currentPage, pageSize)

    if (response.data.code === 200) {
      jobList.value = response.data.data.records
      total.value = response.data.data.page.total
    } else {
      ElMessage.error(response.data.message || '获取职位列表失败')
    }
  } catch (error) {
    console.error('获取职位列表出错：', error)
    ElMessage.error('获取职位列表失败')
  }
}

// 工具函数：格式化薪资
const formatSalary = (min, max) => {
  if (!min && !max) return '薪资面议'
  if (!max) return `${min}k以上`
  return `${min}-${max}k`
}

// 工具函数：获取职位类型
const getJobType = (type) => {
  const typeMap = {
    1: '全职',
    2: '兼职',
    3: '实习'
  }
  return typeMap[type] || '未知'
}

// 分页处理函数
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchJobs()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchJobs()
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
  fetchJobs()
})
</script>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

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

.search-section {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  padding: 60px 0;

  .search-content {
    max-width: 1200px;
    margin: 0 auto;
    text-align: center;
    color: white;
  }

  h2 {
    margin-bottom: 30px;
  }

  .search-box {
    display: flex;
    gap: 12px;
    justify-content: center;
    margin-bottom: 20px;
  }

  .search-input {
    width: 500px;
  }

  .hot-tags {
    color: rgba(255, 255, 255, 0.8);

    .hot-tag {
      margin: 0 4px;
      cursor: pointer;
    }
  }
}

.section {
  padding: 40px 0;

  .section-content {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-title {
    margin-bottom: 24px;
    font-size: 24px;
    font-weight: bold;
  }
}

.company-showcase {
  background-color: #f5f7fa;

  .company-card {
    text-align: center;
    padding: 20px;

    .company-logo {
      width: 80px;
      height: 80px;
      object-fit: contain;
      margin-bottom: 12px;
    }

    .company-name {
      font-size: 14px;
    }
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

.job-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .job-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    h4 {
      margin: 0;
      font-size: 16px;
      color: #303133;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .salary {
      color: #f56c6c;
      font-weight: bold;
      font-size: 15px;
      white-space: nowrap;
    }
  }

  .company {
    color: #606266;
    margin-bottom: 12px;
    font-size: 14px;
  }

  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    color: #909399;
    font-size: 13px;

    .location {
      display: flex;
      align-items: center;
      gap: 4px;
    }

    .job-type {
      padding: 2px 8px;
      background-color: #f0f2f5;
      border-radius: 4px;
    }
  }

  .recruiter {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: #909399;

    .el-avatar {
      border: 1px solid #eee;
    }
  }
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
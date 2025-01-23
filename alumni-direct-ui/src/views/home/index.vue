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
            <el-menu-item index="/jobs">职位搜索</el-menu-item>
            <el-menu-item index="/company">名企推荐</el-menu-item>
            <el-menu-item index="/about">关于我们</el-menu-item>
          </el-menu>
        </div>
        <div class="user-actions">
          <template v-if="!isLoggedIn">
            <el-button type="primary" @click="handleLogin">登录</el-button>
            <el-button @click="handleRegister">注册</el-button>
          </template>
          <template v-else>
            <el-dropdown>
              <span class="user-profile">
                <el-avatar :size="32" src="https://placeholder.com/32"/>
                <span>{{ userName }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>我的简历</el-dropdown-item>
                  <el-dropdown-item>投递记录</el-dropdown-item>
                  <el-dropdown-item>收藏职位</el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
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
                <el-avatar :size="24" src="https://placeholder.com/24"/>
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

    <!-- 名企展示 -->
    <div class="section company-showcase">
      <div class="section-content">
        <h3 class="section-title">知名企业</h3>
        <el-row :gutter="20">
          <el-col :span="4" v-for="company in companies" :key="company.id">
            <el-card shadow="hover" class="company-card">
              <img :src="company.logo" :alt="company.name" class="company-logo">
              <div class="company-name">{{ company.name }}</div>
            </el-card>
          </el-col>
        </el-row>
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

      @success="handleLoginSuccess"

  />


</template>

<script setup>
import {ref, onMounted} from 'vue'
import {Search, Location} from '@element-plus/icons-vue'
import LoginDialog from '@/components/LoginDialog.vue'
import request from '@/utils/request'
import {ElMessage} from 'element-plus'

// 用户相关状态
const isLoggedIn = ref(false)
const userName = ref('')
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

// 登录弹窗控制
const loginDialogVisible = ref(false)

// 登录相关方法
const handleLogin = () => {
  // 处理登录逻辑
  loginDialogVisible.value = true
  userName.value = userData.username
  loginDialogVisible.value = false
  ElMessage.success('登录成功')
}

const handleRegister = () => {
  // 处理注册逻辑
}

const handleLogout = () => {
  // 处理登出逻辑
}

const handleSearch = () => {
  // 处理搜索逻辑
}

// 职位列表相关数据
const jobList = ref([])
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

// 获取职位列表
const fetchJobs = async () => {
  try {
    const response = await request.get('/ad/job/cards', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })

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

// 页面加载时获取数据
onMounted(() => {
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
<template>
  <!-- Hero搜索区域 -->
  <div class="hero-section">
    <!-- 背景装饰 -->
    <div class="hero-bg">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
      <div class="bg-wave"></div>
    </div>

    <div class="hero-content">
      <div class="hero-text">
        <h1 class="hero-title">校友直聘</h1>
        <p class="hero-subtitle">连接校友资源，开启职业新篇章</p>
        <p class="hero-desc">汇聚优质企业，精准匹配职位，助力校友职业发展</p>
      </div>

      <div class="search-box-wrapper">
        <div class="search-box">
          <div class="search-input-wrapper">
            <el-icon class="search-icon">
              <Search/>
            </el-icon>
            <el-input
                v-model="searchKeyword"
                placeholder="搜索职位、公司或技能"
                class="search-input"
                @keyup.enter="handleSearch"
            />
          </div>
          <el-button type="primary" size="large" class="search-btn" @click="handleSearch">搜索职位</el-button>
        </div>

        <div class="hot-tags">
          <span class="hot-label">热门搜索：</span>
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

      <!-- 统计数据 -->
      <div class="stats-row">
        <div class="stat-item">
          <div class="stat-number">500+</div>
          <div class="stat-label">优质企业</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-number">10000+</div>
          <div class="stat-label">精选职位</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-number">98%</div>
          <div class="stat-label">匹配成功率</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-number">50万+</div>
          <div class="stat-label">校友用户</div>
        </div>
      </div>
    </div>
  </div>

  <!-- 在搜索区域和职位推荐之间添加分类模块 -->
  <!--  <div class="category-section">-->
  <!--    <div class="section-content">-->
  <!--      <h3 class="section-title">职位分类</h3>-->
  <!--      <div class="category-container">-->
  <!--        &lt;!&ndash; 左侧大分类 &ndash;&gt;-->
  <!--        <div class="main-categories">-->
  <!--          <div-->
  <!--              v-for="category in categories"-->
  <!--              :key="category.id"-->
  <!--              class="main-category-item"-->
  <!--              :class="{ active: currentCategory.id === category.id }"-->
  <!--              @click="handleCategoryClick(category)"-->
  <!--          >-->
  <!--            {{ category.categoryName }}-->
  <!--          </div>-->
  <!--        </div>-->
  <!--        &lt;!&ndash; 右侧小分类 &ndash;&gt;-->
  <!--        <div class="sub-categories">-->
  <!--          <div class="sub-category-group" v-if="currentCategory.children">-->
  <!--            <div-->
  <!--                v-for="(subList, index) in groupedSubCategories"-->
  <!--                :key="main"-->
  <!--                class="sub-category-row"-->
  <!--            >-->
  <!--              <div-->
  <!--                  v-for="sub in subList"-->
  <!--                  :key="sub.id"-->
  <!--                  class="sub-category-item"-->
  <!--                  @click="handleSubCategoryClick(sub)"-->
  <!--              >-->
  <!--                {{ sub.categoryName }}-->
  <!--              </div>-->
  <!--            </div>-->
  <!--          </div>-->
  <!--        </div>-->
  <!--      </div>-->
  <!--    </div>-->
  <!--  </div>-->

  <!-- 合作伙伴展示 -->
  <div class="partners-section">
    <div class="section-content">
      <div class="section-header">
        <h3 class="section-title">合作伙伴企业</h3>
        <p class="section-subtitle">携手知名企业，共创职业未来</p>
      </div>
      <div class="partners-grid">
        <div class="partner-item" v-for="n in 8" :key="n">
          <div class="partner-logo">
            <el-icon size="48" color="#1890ff">
              <Briefcase/>
            </el-icon>
          </div>
          <span class="partner-name">合作伙伴{{ n }}</span>
        </div>
      </div>
    </div>
  </div>

  <!-- 职位推荐部分 -->
  <div class="section job-recommend">
    <div class="section-content">
      <div class="section-header">
        <div class="header-left">
          <h3 class="section-title">热门职位推荐</h3>
          <p class="section-subtitle">精选优质职位，助你快速入职</p>
        </div>
        <el-button type="text" class="view-more" @click="router.push('/search')">查看更多</el-button>
      </div>

      <div class="job-grid">
        <div
            class="job-card"
            v-for="job in jobList"
            :key="job.id"
            @click="handleJobClick(job.id)"
        >
          <div class="job-card-inner">
            <!-- 顶部标签 -->
            <div class="job-tags">
              <el-tag v-if="job.isAlumni === 1" type="success" size="small" class="alumni-tag">校友职位</el-tag>
              <el-tag type="info" size="small" class="type-tag">{{ getJobType(job.jobType) }}</el-tag>
            </div>

            <!-- 职位信息 -->
            <div class="job-header">
              <h4 class="job-title">{{ job.title }}</h4>
              <div class="salary">{{ formatSalary(job.minSalary, job.maxSalary) }}</div>
            </div>

            <div class="company">{{ job.companyName }}</div>

            <div class="job-info">
              <div class="info-row">
                <span class="location">
                  <el-icon><Location/></el-icon>
                  {{ job.location }}
                </span>
              </div>
              <p class="job-desc">{{ truncateText(job.jobDesc, 30) }}</p>
            </div>

            <!-- 招聘者信息 -->
            <div class="recruiter-info">
              <el-avatar :size="36" :src="job.recruiterAvatar" class="recruiter-avatar"/>
              <div class="recruiter-detail">
                <span class="recruiter-name">{{ job.recruiterName }}</span>
                <span class="recruiter-role">招聘者</span>
              </div>
            </div>
          </div>
        </div>
      </div>

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
            class="pagination"
        />
      </div>
    </div>
  </div>

  <!-- 右侧抽屉 -->
  <el-drawer
      v-model="drawerVisible"
      :direction="direction"
      :before-close="handleClose"
      size="50%"
      destroy-on-close
      class="job-detail-drawer"
  >
    <template #header>
      <h3>职位详情</h3>
    </template>
    <JobDetails :selectedJob="selectedJob"/>
  </el-drawer>
</template>

<script setup>
import {getJobCard, getJobDetail} from '@/api/job.js'
import {searchJob} from '@/api/job.js'
import {ref, computed} from 'vue'
import {ElMessage} from 'element-plus'
import {Search, Location, Briefcase} from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import {onMounted} from 'vue'
import JobDetails from '../../components/JobDetail.vue'
import router from "@/router/index.js";
// 分类相关的状态
const categories = ref([])
const currentCategory = ref({})
const searchKeyword = ref('')
// 热门标签
const hotTags = ref(['前端开发', 'Java开发', '产品经理', 'UI设计'])


// 获取分类数据
const fetchCategories = async () => {
  try {
    const response = await request.get('/ad/category/getTree')
    if (response.data.code === 200) {
      console.log('分类数据:', response.data.data)
      categories.value = response.data.data
      console.log('分类数据:', categories.value)
      if (categories.value.length > 0) {
        currentCategory.value = categories.value[0]
      }
    }
  } catch (error) {
    console.error('获取分类失败：', error)
  }
}

// 将子分类分组显示（每行4个）
const groupedSubCategories = computed(() => {
  if (!currentCategory.value.children) return []
  const children = currentCategory.value.children
  const groups = []
  for (let i = 0; i < children.length; i += 4) {
    groups.push(children.slice(i, i + 4))
  }
  return groups
})


onMounted(() => {
  fetchJobs()
  fetchCategories()
})

// 点击大分类
const handleCategoryClick = (category) => {
  currentCategory.value = category
}

// 点击小分类
const handleSubCategoryClick = async (subCategory) => {
  // 可以跳转到对应分类的职位列表页
  console.log('选择的子分类：', subCategory.id)
  console.log('当前页：', currentPage.value)
  console.log('显示个数', pageSize.value)
  try {
    const resp = await searchJob({
      currentPage: currentPage.value,
      pageSize: pageSize.value,
      keyword: '',
      categoryId: subCategory.id
    })
    if (resp.data.code === 200) {
      jobList.value = resp.data.data.records
      total.value = resp.data.data.page.total
    } else {
      ElMessage.error(resp.data.message || '获取职位列表失败')
    }
  } catch (error) {
    console.error('获取职位列表失败：', error)
  }
}
// 搜索
const handleSearch = async () => {
  if (searchKeyword.value === '') {
    ElMessage.error('请输入搜索内容')
    return
  }
  // 跳转到搜索结果页面
  await router.push({name: 'Search', query: {keyword: searchKeyword.value}})
}

// 职位列表相关数据
const jobList = ref([])
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

// 获取职位列表
const fetchJobs = async () => {
  jobList.value = []; // Clear the job list before fetching new data
  try {
    const response = await getJobCard(currentPage.value, pageSize.value)

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
    0: '全职',
    1: '实习',
    2: '兼职'
  }
  return typeMap[type] || '未知'
}
// 抽屉相关状态
const drawerVisible = ref(false);
const direction = ref('rtl'); // 右侧弹出
const selectedJob = ref(null);
// 处理职位卡片点击事件
const handleJobClick = async (jobId) => {
  console.log('选中的职位id', jobId)
  drawerVisible.value = true;
  try {
    const response = await getJobDetail(jobId)
    if (response.data.code === 200) {
      selectedJob.value = response.data.data
      console.log('选中的职位', selectedJob.value)
    } else {
      ElMessage.error(response.data.message || '获取职位详情失败')
    }
  } catch (error) {
    console.error('获取职位详情失败：', error)
    ElMessage.error('获取职位详情失败')
  }
}
// 关闭抽屉时重置数据
const handleClose = () => {
  drawerVisible.value = false;
  selectedJob.value = null;
};
// 分页处理函数
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchJobs()
}
// 分页处理函数
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchJobs()
}
// 截取文本
const truncateText = (text, length) => {
  if (text.length <= length) return text;
  return text.substring(0, length) + '...';
}
</script>
<style lang="scss" scoped>
/* Hero区域样式 */
.hero-section {
  position: relative;
  min-height: 500px;
  overflow: hidden;
  background: linear-gradient(135deg, #0f172a 0%, #1e3a5f 50%, #0ea5e9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 20px 60px;

  .hero-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    overflow: hidden;

    .bg-circle {
      position: absolute;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(14, 165, 233, 0.3) 0%, transparent 70%);
      animation: float 6s ease-in-out infinite;

      &.bg-circle-1 {
        width: 400px;
        height: 400px;
        top: -100px;
        left: -100px;
        animation-delay: 0s;
      }

      &.bg-circle-2 {
        width: 300px;
        height: 300px;
        top: 100px;
        right: 50px;
        animation-delay: 2s;
      }

      &.bg-circle-3 {
        width: 200px;
        height: 200px;
        bottom: 50px;
        left: 200px;
        animation-delay: 4s;
      }
    }

    .bg-wave {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 100px;
      background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 120'%3E%3Cpath fill='%23ffffff' fill-opacity='0.05' d='M0,64L48,69.3C96,75,192,85,288,80C384,75,480,53,576,48C672,43,768,53,864,64C960,75,1056,85,1152,80C1248,75,1344,53,1392,42.7L1440,32L1440,120L1392,120C1344,120,1248,120,1152,120C1056,120,960,120,864,120C768,120,672,120,576,120C480,120,384,120,288,120C192,120,96,120,48,120L0,120Z'%3E%3C/path%3E%3C/svg%3E") no-repeat bottom center;
      background-size: cover;
    }
  }

  .hero-content {
    position: relative;
    z-index: 10;
    max-width: 1200px;
    width: 100%;
    text-align: center;
  }

  .hero-text {
    margin-bottom: 40px;

    .hero-title {
      font-size: 48px;
      font-weight: 700;
      color: #fff;
      margin: 0 0 16px;
      letter-spacing: 2px;
    }

    .hero-subtitle {
      font-size: 24px;
      color: rgba(255, 255, 255, 0.9);
      margin: 0 0 12px;
    }

    .hero-desc {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.7);
      margin: 0;
    }
  }

  .search-box-wrapper {
    margin-bottom: 50px;
  }

  .search-box {
    display: flex;
    max-width: 700px;
    margin: 0 auto;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    overflow: hidden;

    .search-input-wrapper {
      flex: 1;
      display: flex;
      align-items: center;
      padding: 0 20px;

      .search-icon {
        color: #909399;
        margin-right: 12px;
        font-size: 18px;
      }

      .search-input {
        flex: 1;
        border: none;

        &:focus {
          outline: none;
        }
      }
    }

    .search-btn {
      border-radius: 0 12px 12px 0;
      padding: 0 32px;
      font-weight: 600;
    }
  }

  .hot-tags {
    margin-top: 16px;

    .hot-label {
      color: rgba(255, 255, 255, 0.7);
      font-size: 14px;
      margin-right: 12px;
    }

    .hot-tag {
      background: rgba(255, 255, 255, 0.15);
      border: none;
      color: rgba(255, 255, 255, 0.9);
      margin: 0 6px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.25);
      }
    }
  }

  .stats-row {
    display: flex;
    justify-content: center;
    align-items: center;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    padding: 30px 40px;
    max-width: 900px;
    margin: 0 auto;

    .stat-item {
      flex: 1;
      text-align: center;

      .stat-number {
        font-size: 32px;
        font-weight: 700;
        color: #fff;
        margin-bottom: 8px;
        display: block;
      }

      .stat-label {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.7);
      }
    }

    .stat-divider {
      width: 1px;
      height: 60px;
      background: rgba(255, 255, 255, 0.2);
      margin: 0 20px;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
}

/* 合作伙伴区域 */
.partners-section {
  background: #f8fafc;
  padding: 60px 0;

  .section-content {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-header {
    text-align: center;
    margin-bottom: 40px;

    .section-title {
      font-size: 28px;
      font-weight: 600;
      color: #1e293b;
      margin: 0 0 8px;
    }

    .section-subtitle {
      font-size: 14px;
      color: #64748b;
      margin: 0;
    }
  }

  .partners-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 30px;

    .partner-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 24px;
      background: #fff;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
      }

      .partner-logo {
        margin-bottom: 12px;
        opacity: 0.7;
        transition: opacity 0.3s;
      }

      .partner-name {
        font-size: 14px;
        color: #64748b;
      }
    }
  }
}

/* 通用区块样式 */
.section {
  padding: 60px 0;

  .section-content {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 32px;

    .header-left {
      .section-title {
        font-size: 28px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 8px;
      }

      .section-subtitle {
        font-size: 14px;
        color: #64748b;
        margin: 0;
      }
    }

    .view-more {
      color: #0ea5e9;
      font-weight: 500;

      &:hover {
        color: #0284c7;
      }
    }
  }
}

/* 职位卡片样式 */
.job-recommend {
  .job-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
  }

  .job-card {
    background: #fff;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
    }

    .job-card-inner {
      padding: 20px;
    }

    .job-tags {
      display: flex;
      gap: 8px;
      margin-bottom: 12px;

      .alumni-tag {
        background: linear-gradient(135deg, #22c55e, #16a34a);
        border: none;
        color: #fff;
      }

      .type-tag {
        background: #f1f5f9;
        color: #64748b;
        border: none;
      }
    }

    .job-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 8px;

      .job-title {
        font-size: 18px;
        font-weight: 600;
        color: #1e293b;
        margin: 0;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .salary {
        font-size: 18px;
        font-weight: 700;
        color: #ef4444;
        white-space: nowrap;
      }
    }

    .company {
      font-size: 14px;
      color: #64748b;
      margin-bottom: 12px;
    }

    .job-info {
      margin-bottom: 16px;

      .info-row {
        margin-bottom: 8px;

        .location {
          display: inline-flex;
          align-items: center;
          gap: 4px;
          font-size: 13px;
          color: #94a3b8;
        }
      }

      .job-desc {
        font-size: 13px;
        color: #94a3b8;
        line-height: 1.6;
        margin: 0;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
    }

    .recruiter-info {
      display: flex;
      align-items: center;
      padding-top: 12px;
      border-top: 1px solid #f1f5f9;

      .recruiter-avatar {
        margin-right: 10px;
        border: 2px solid #f1f5f9;
      }

      .recruiter-detail {
        display: flex;
        flex-direction: column;

        .recruiter-name {
          font-size: 13px;
          color: #475569;
          font-weight: 500;
        }

        .recruiter-role {
          font-size: 12px;
          color: #94a3b8;
        }
      }
    }
  }

  .pagination-container {
    margin-top: 40px;
    display: flex;
    justify-content: center;

    .pagination {
      :deep(.el-pagination__total) {
        color: #64748b;
      }
    }
  }
}

/* 抽屉样式 */
.job-detail-drawer {
  :deep(.el-drawer__header) {
    padding: 24px 24px 16px;
    border-bottom: 1px solid #f1f5f9;

    h3 {
      font-size: 20px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
    }
  }

  :deep(.el-drawer__body) {
    padding: 24px;
  }
}
</style>
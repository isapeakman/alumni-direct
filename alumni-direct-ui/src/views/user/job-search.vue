<!-- src/views/user/job-search.vue -->
<template>
  <div class="job-search-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">职位搜索</h1>
      <p class="page-subtitle">搜索你感兴趣的职位、公司或技能</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
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
      </div>
    </div>

    <!-- 分类模块 -->
    <div class="category-section">
      <div class="section-content">
        <div class="section-header">
          <h3 class="section-title">职位分类</h3>
        </div>
        <div class="category-container">
          <!-- 左侧大分类 -->
          <div class="main-categories">
            <div
                v-for="category in categories"
                :key="category.id"
                class="main-category-item"
                :class="{ active: currentCategory.id === category.id }"
                @click="handleCategoryClick(category)"
            >
              <el-icon class="category-icon">
                <Folder/>
              </el-icon>
              <span>{{ category.categoryName }}</span>
            </div>
          </div>
          <!-- 右侧小分类 -->
          <div class="sub-categories">
            <div v-if="currentCategory.children" class="sub-category-grid">
              <div
                  v-for="sub in currentCategory.children"
                  :key="sub.id"
                  class="sub-category-item"
                  :class="{ active: selectedSubCategory?.id === sub.id }"
                  @click="handleSubCategoryClick(sub)"
              >
                {{ sub.categoryName }}
              </div>
            </div>
            <div v-else class="empty-sub-categories">
              <p>暂无子分类</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索结果部分 -->
    <div class="section job-results">
      <div class="section-content">
        <div class="section-header">
          <div class="header-left">
            <h3 class="section-title">搜索结果</h3>
            <p class="section-subtitle">共找到 {{ total }} 个职位</p>
          </div>
        </div>

        <div v-if="jobList.length > 0" class="job-grid">
          <div
              class="job-card"
              v-for="job in jobList"
              :key="job.id"
              @click="handleJobClick(job.id)"
          >
            <div class="job-card-inner">
              <div class="job-tags">
                <el-tag v-if="job.isAlumni === 1" type="success" size="small" class="alumni-tag">校友职位</el-tag>
                <el-tag type="info" size="small" class="type-tag">{{ getJobType(job.jobType) }}</el-tag>
              </div>

              <div class="job-header">
                <h4 class="job-title">{{ job.title }}</h4>
                <div class="salary">{{ formatSalary(job.minSalary, job.maxSalary) }}</div>
              </div>

              <div class="company">{{ job.companyName }}</div>

              <div class="job-info">
                <span class="location">
                  <el-icon><Location/></el-icon>
                  {{ job.location }}
                </span>
                <p class="job-desc">{{ truncateText(job.jobDesc, 30) }}</p>
              </div>

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

        <div v-else class="empty-results">
          <el-icon size="64" color="#cbd5e1">
            <Search/>
          </el-icon>
          <p>未找到相关职位</p>
          <p class="empty-hint">试试其他关键词或选择不同的分类</p>
        </div>

        <!-- 分页器 -->
        <div v-if="jobList.length > 0" class="pagination-container">
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
  </div>
</template>

<script setup>
import {getJobCard, getJobDetail} from '@/api/job.js'
import {searchJob} from '@/api/job.js'
import {ref, computed, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {Search, Location, Folder} from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import {useRoute, useRouter} from 'vue-router'
import JobDetails from '../../components/JobDetail.vue'
import main from "@/views/user/main.vue";

const route = useRoute()
const router = useRouter()

// 分类相关的状态
const categories = ref([])
const currentCategory = ref({})
const selectedSubCategory = ref(null)
const searchKeyword = ref(route.query.keyword || '')

// 获取分类数据
const fetchCategories = async () => {
  try {
    const response = await request.get('/ad/category/getTree')
    if (response.data.code === 200) {
      categories.value = response.data.data
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

// 职位列表相关数据
const jobList = ref([])
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

// 获取职位列表
const fetchJobs = async () => {
  jobList.value = [] // Clear the job list before fetching new data
  try {
    const response = await searchJob({
      currentPage: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      categoryId: currentCategory.value.id || ''
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

// 搜索
const handleSearch = async () => {
  if (searchKeyword.value === '') {
    ElMessage.error('请输入搜索内容')
    return
  }
  //关键词
  searchKeyword.value = searchKeyword.value.trim()
  await fetchJobs()
}

// 点击大分类
const handleCategoryClick = async (category) => {
  currentCategory.value = category
  await fetchJobs()
}

// 点击小分类
const handleSubCategoryClick = async (subCategory) => {
  currentCategory.value = subCategory
  await fetchJobs()
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
  drawerVisible.value = true;
  try {
    const response = await getJobDetail(jobId)
    if (response.data.code === 200) {
      selectedJob.value = response.data.data
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

onMounted(() => {
  fetchCategories()
  fetchJobs()
})
</script>

<style lang="scss" scoped>
.job-search-container {
  padding: 30px;
  max-width: 1400px;
  margin: 0 auto;

  .page-header {
    margin-bottom: 30px;

    .page-title {
      font-size: 28px;
      font-weight: 600;
      color: #1e293b;
      margin: 0 0 8px;
    }

    .page-subtitle {
      font-size: 14px;
      color: #64748b;
      margin: 0;
    }
  }
}

/* 搜索区域 */
.search-section {
  background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 50%, #0369a1 100%);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 24px;

  .search-box-wrapper {
    max-width: 800px;
    margin: 0 auto;
  }

  .search-box {
    display: flex;
    gap: 12px;
  }

  .search-input-wrapper {
    flex: 1;
    position: relative;
    background: #fff;
    border-radius: 12px;
    padding: 0 20px;
    display: flex;
    align-items: center;

    .search-icon {
      color: #94a3b8;
      margin-right: 12px;
    }

    .search-input {
      flex: 1;
      border: none;
      box-shadow: none;

      ::v-deep(.el-input__inner) {
        border: none;
        box-shadow: none;
      }
    }
  }

  .search-btn {
    padding: 0 32px;
    border-radius: 12px;
    font-weight: 600;
    background: linear-gradient(135deg, #f97316, #ea580c);
    border: none;

    &:hover {
      background: linear-gradient(135deg, #fb923c, #f97316);
    }
  }
}

/* 分类区域 */
.category-section {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  padding: 24px;
  margin-bottom: 24px;

  .section-content {
    .section-header {
      margin-bottom: 20px;

      .section-title {
        font-size: 18px;
        font-weight: 600;
        color: #1e293b;
        margin: 0;
      }
    }

    .category-container {
      display: flex;
      gap: 24px;
      min-height: 280px;
    }

    .main-categories {
      width: 180px;
      background: #f8fafc;
      border-radius: 12px;
      padding: 8px;

      .main-category-item {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px 16px;
        cursor: pointer;
        border-radius: 8px;
        transition: all 0.3s;
        font-size: 14px;
        color: #475569;

        .category-icon {
          font-size: 16px;
        }

        &:hover {
          background: #e2e8f0;
        }

        &.active {
          background: #0ea5e9;
          color: #fff;
        }
      }
    }

    .sub-categories {
      flex: 1;

      .sub-category-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
        gap: 12px;
      }

      .sub-category-item {
        padding: 10px 16px;
        background: #f8fafc;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        font-size: 13px;
        color: #64748b;
        text-align: center;

        &:hover {
          background: #e2e8f0;
          color: #334155;
        }

        &.active {
          background: rgba(14, 165, 233, 0.1);
          color: #0ea5e9;
        }
      }

      .empty-sub-categories {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 200px;
        color: #94a3b8;

        p {
          margin: 0;
        }
      }
    }
  }
}

/* 搜索结果区域 */
.job-results {
  .section-content {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      .header-left {
        .section-title {
          font-size: 18px;
          font-weight: 600;
          color: #1e293b;
          margin: 0 0 4px;
        }

        .section-subtitle {
          font-size: 13px;
          color: #94a3b8;
          margin: 0;
        }
      }
    }
  }
}

.job-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.job-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f1f5f9;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    border-color: #0ea5e9;
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
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .salary {
      font-size: 16px;
      font-weight: 700;
      color: #ef4444;
      white-space: nowrap;
    }
  }

  .company {
    font-size: 13px;
    color: #64748b;
    margin-bottom: 12px;
  }

  .job-info {
    margin-bottom: 14px;

    .location {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #94a3b8;
      margin-bottom: 8px;
    }

    .job-desc {
      font-size: 13px;
      color: #94a3b8;
      line-height: 1.5;
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
    padding-top: 14px;
    border-top: 1px solid #f1f5f9;

    .recruiter-avatar {
      margin-right: 12px;
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
        font-size: 11px;
        color: #94a3b8;
      }
    }
  }
}

.empty-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  color: #94a3b8;

  p {
    margin: 12px 0 0;
    font-size: 14px;
  }

  .empty-hint {
    font-size: 12px;
    color: #cbd5e1;
    margin-top: 6px;
  }
}

.pagination-container {
  margin-top: 32px;
  display: flex;
  justify-content: center;

  .pagination {
    ::v-deep(.el-pagination) {
      .el-pager li {
        border-radius: 8px;
        margin: 0 4px;
      }

      .el-pager li.active {
        background: #0ea5e9;
      }

      .el-pagination__sizes {
        margin-right: 12px;
      }
    }
  }
}

/* 抽屉样式 */
.job-detail-drawer {
  ::v-deep(.el-drawer__body) {
    padding: 24px;
  }

  ::v-deep(.el-drawer__header) {
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;

    h3 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
    }
  }
}
</style>

<!-- src/views/user/job-search.vue -->
<template>
  <div class="job-search-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-content">
        <div class="search-box">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索职位"
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
      </div>
    </div>

    <!-- 分类模块 -->
    <div class="category-section">
      <div class="section-content">
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
              {{ category.categoryName }}
            </div>
          </div>
          <!-- 右侧小分类 -->
          <div class="sub-categories">
            <div class="sub-category-group" v-if="currentCategory.children">
              <div
                  v-for="(subList, index) in groupedSubCategories"
                  :key="main"
                  class="sub-category-row"
              >
                <div
                    v-for="sub in subList"
                    :key="sub.id"
                    class="sub-category-item"
                    @click="handleSubCategoryClick(sub)"
                >
                  {{ sub.categoryName }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索结果部分 -->
    <div class="section job-recommend">
      <div class="section-content">
        <h3 class="section-title">搜索结果</h3>
        <el-row :gutter="20">
          <el-col :span="6" v-for="job in jobList" :key="job.title">
            <el-card shadow="hover" class="job-card" @click="handleJobClick(job.id)">
              <div class="job-header">
                <h4>{{ job.title }}</h4>
                <div class="salary">{{ formatSalary(job.minSalary, job.maxSalary) }}</div>
              </div>
              <div class="company">{{ job.companyName }}</div>
              <div class="info-row">
                <p>{{ truncateText(job.jobDesc, 20) }}</p>
                <span class="location">
                  <el-icon><Location/></el-icon>
                  {{ job.location }}
                </span>
              </div>
              <div class="recruiter">
                <el-avatar :size="30" :src="job.recruiterAvatar"/>
                <span>{{ job.recruiterName }}·招聘者</span>
              </div>
              <!-- 新增 job-footer 容器 -->
              <div class="job-footer">
                <el-tag v-if="job.isAlumni === 1" type="success" size="small" style="margin-left: 10px;">校友职位
                </el-tag>
                <el-tag type="info" size="small">{{ getJobType(job.jobType) }}</el-tag>
              </div>
            </el-card>

          </el-col>
          <!-- 右侧抽屉 -->
          <el-col :span="16" class="drawer-container">
            <el-drawer
                v-model="drawerVisible"
                :direction="direction"
                :before-close="handleClose"
                size="50%"
                destroy-on-close
            >
              <template #header>
                <h3>职位详情</h3>
              </template>
              <JobDetails :selectedJob="selectedJob"/>
            </el-drawer>
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
  </div>
</template>

<script setup>
import {getJobCard, getJobDetail} from '@/api/job.js'
import {searchJob} from '@/api/job.js'
import {ref, computed, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {Search, Location} from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import {useRoute, useRouter} from 'vue-router'
import JobDetails from '../../components/JobDetail.vue'
import main from "@/views/user/main.vue";

const route = useRoute()
const router = useRouter()

// 分类相关的状态
const categories = ref([])
const currentCategory = ref({})
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
  padding: 20px;
}

.search-section {


  .search-content {
    max-width: 1200px;
    margin: 0 auto;
    text-align: center;
    color: white;
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

.job-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s; /* 调整过渡时间为 0.3s */
  width: 250px; /* 固定宽度 */
  height: 200px; /* 固定高度 */
  position: relative; /* 为子元素的绝对定位提供参考 */
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  overflow: hidden; /* 确保内容不会超出卡片范围 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

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
      position: absolute;
      bottom: 10px;
      right: 10px;
      padding: 2px 8px;
      background-color: #f0f2f5;
      border-radius: 4px;
      max-width: 50%; /* 限制宽度，避免超出卡片 */
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
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

  .job-footer {
    position: absolute;
    bottom: 10px;
    right: 10px;
    display: flex;
    gap: 10px;
  }
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

.category-section {
  background-color: #fff;

  .category-container {
    display: flex;
    gap: 20px;
    min-height: 300px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
  }

  .main-categories {
    width: 200px;
    border-right: 1px solid #ebeef5;

    .main-category-item {
      padding: 15px 20px;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        color: #409eff;
        background-color: #f5f7fa;
      }

      &.active {
        color: #409eff;
        background-color: #ecf5ff;
      }
    }
  }

  .sub-categories {
    flex: 1;
    padding: 20px;

    .sub-category-row {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .sub-category-item {
      padding: 8px 15px;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s;
      color: #606266;

      &:hover {
        color: #409eff;
        background-color: #ecf5ff;
      }
    }
  }
}

/* 覆盖抽屉默认样式 */
:deep(.el-drawer__body) {
  padding: 20px;
}

/* 抽屉标题样式 */
:deep(.el-drawer__header) {
  margin-bottom: 20px;

  h3 {
    margin: 0;
    font-size: 20px;
  }
}
</style>

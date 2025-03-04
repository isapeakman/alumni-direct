<template>
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

  <!-- 在搜索区域和职位推荐之间添加分类模块 -->
  <div class="category-section">
    <div class="section-content">
      <h3 class="section-title">职位分类</h3>
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
                :key="index"
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
              <el-avatar :size="30" :src="job.recruiterAvatar"/>
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
</template>

<script setup>
import {getJobCard} from '@/api/job'
import {searchJob} from '@/api/job'
import {ref, computed} from 'vue'
import {ElMessage} from 'element-plus'
import {Search, Location} from '@element-plus/icons-vue'
import request from '@/utils/request'
import {onMounted} from 'vue'
// 分类相关的状态
const categories = ref([])
const currentCategory = ref({})
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
</script>
<style lang="scss" scoped>
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

.category-section {
  padding: 40px 0;
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
</style>
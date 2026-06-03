<template>
  <div class="recommend-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">智能职位推荐</h1>
      <p class="page-subtitle">基于您的求职意向，为您推荐最合适的职位</p>
    </div>

    <div class="main-content">
      <!-- 左侧职位列表 -->
      <div class="job-list-panel">
        <div class="panel-header">
          <h3>推荐职位</h3>
          <span class="count">{{ jobList.length }} 个职位</span>
        </div>

        <div class="job-list-container">
          <div class="job-list" v-infinite-scroll="loadMore" :infinite-scroll-disabled="loading">
            <div class="job-card-wrapper" v-for="job in jobList" :key="job.id">
              <div
                  class="job-card"
                  @click="handleJobClick(job.id)"
                  :class="{ 'active-card': selectedCardId.value === Number(job.id) }"
              >
                <!-- 标签区域 -->
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
                  <p class="job-desc">{{ truncateText(job.jobDesc, 30) }}</p>
                  <span class="location">
                    <el-icon><Location/></el-icon>
                    {{ job.location }}
                  </span>
                </div>

                <div class="recruiter-info">
                  <el-avatar :size="30" :src="job.recruiterAvatar"/>
                  <span class="recruiter-name">{{ job.recruiterName }}·招聘者</span>
                </div>
              </div>
            </div>

            <!-- 加载状态 -->
            <div class="loading-status" v-if="loading">
              <el-icon class="loading-icon">
                <Loading/>
              </el-icon>
              加载中...
            </div>
            <div class="no-more" v-if="noMore">没有更多职位了</div>
          </div>
        </div>
      </div>

      <!-- 右侧职位详情 -->
      <div class="job-detail-panel">
        <div class="panel-header">
          <h3>职位详情</h3>
        </div>

        <div class="job-details-container">
          <div v-if="!selectedJob" class="empty-state">
            <el-icon size="64" color="#cbd5e1">
              <Briefcase/>
            </el-icon>
            <p>请选择一个职位查看详情</p>
          </div>
          <JobDetails v-else :selectedJob="selectedJob"/>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {Location, Loading, Briefcase} from '@element-plus/icons-vue'
import {getRecommendJobCard} from '@/api/job.js'
import {ElMessage} from 'element-plus'
import {getJobDetail} from '@/api/job.js'
import JobDetails from '@/components/JobDetail.vue'
// 状态管理
const jobList = ref([])
const currentPage = ref(1)
const pageSize = ref(8)
const loading = ref(false)
const noMore = ref(false)
const total = ref(0)
const selectedJob = ref(null)
let isFirstLoad = true // 新增变量用于判断是否是第一次加载
const selectedCardId = ref(null); // 记录选中的职位卡片id
const isAuthenticated = ref(false); // 用户是否登录状态
// 监听登录状态变化


// 加载更多数据
const loadMore = async () => {
  if (loading.value || noMore.value) return;

  loading.value = true;
  try {
    const response = await getRecommendJobCard(currentPage.value, pageSize.value);
    if (response.data.code === 200) {
      const newJobs = response.data.data.records;
      total.value = response.data.data.page.total;

      // 如果用户未登录，限制最多显示八条记录
      if (!isAuthenticated.value) {
        newJobs.splice(8); // 截取前八条记录
      }

      // 追加新数据而不是替换
      jobList.value = [...jobList.value, ...newJobs];

      // 更新页码和状态
      currentPage.value++;
      // 计算是否无更多数据
      noMore.value = jobList.value.length >= total.value ||
          currentPage.value > Math.ceil(total.value / pageSize.value);

      // 如果是第一次加载，请求第一个职位的详情
      if (isFirstLoad && jobList.value.length > 0) {
        await handleJobClick(jobList.value[0].id);
        isFirstLoad = false;
      }
    }
  } catch (error) {
    console.error('获取职位列表失败：', error);
  } finally {
    loading.value = false;
  }
}

// 截取文本
const truncateText = (text, length) => {
  if (text.length <= length) return text;
  return text.substring(0, length) + '...';
}
// 获取职位类型
const getJobType = (type) => {
  const typeMap = {
    0: '全职',
    1: '实习',
    2: '兼职'
  }
  return typeMap[type] || '未知'
}
// 处理职位卡片点击事件
const handleJobClick = async (jobId) => {
  selectedCardId.value = jobId;
  console.log('选中的职位id', jobId)
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
// 工具函数
const formatSalary = (min, max) => {
  if (!min && !max) return '薪资面议'
  if (!max) return `${min}k以上`
  return `${min}-${max}k`
}
onMounted(() => {
  loadMore()
})
</script>

<style lang="scss" scoped>
.recommend-container {
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

.main-content {
  display: grid;
  grid-template-columns: 450px 1fr;
  gap: 24px;
}

/* 左侧职位列表面板 */
.job-list-panel {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
    }

    .count {
      font-size: 13px;
      color: #94a3b8;
      background: #f1f5f9;
      padding: 4px 12px;
      border-radius: 20px;
    }
  }
}

.job-list-container {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  padding: 16px;
}

.job-card-wrapper {
  margin-bottom: 12px;
}

.job-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  &:hover {
    transform: translateX(4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }

  &.active-card {
    border-color: #0ea5e9;
    background: rgba(14, 165, 233, 0.02);

    .job-title {
      color: #0ea5e9;
    }
  }

  .job-tags {
    display: flex;
    gap: 8px;
    margin-bottom: 10px;

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
    margin-bottom: 10px;
  }

  .job-info {
    margin-bottom: 12px;

    .job-desc {
      font-size: 12px;
      color: #94a3b8;
      line-height: 1.5;
      margin: 0 0 8px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .location {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #94a3b8;
    }
  }

  .recruiter-info {
    display: flex;
    align-items: center;
    padding-top: 10px;
    border-top: 1px solid #f1f5f9;

    .el-avatar {
      margin-right: 8px;
      border: 1px solid #f1f5f9;
    }

    .recruiter-name {
      font-size: 12px;
      color: #64748b;
    }
  }
}

.loading-status, .no-more {
  text-align: center;
  padding: 20px 0;
  color: #94a3b8;
  font-size: 14px;

  .loading-icon {
    margin-right: 8px;
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 右侧职位详情面板 */
.job-detail-panel {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: fit-content;
  max-height: calc(100vh - 180px);

  .panel-header {
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;

    h3 {
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
    }
  }
}

.job-details-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  color: #94a3b8;

  p {
    margin-top: 16px;
    font-size: 14px;
  }
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .job-detail-panel {
    max-height: 600px;
  }
}
</style>



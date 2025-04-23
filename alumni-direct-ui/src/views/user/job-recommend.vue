<template>
  <el-row :gutter="20">
    <div class="recommend-container">
      <el-col :span="8">
        <div class="grid-content ep-bg-purple"/>

        <!--  左侧职位列表-->
        <div class="job-list" v-infinite-scroll="loadMore" :infinite-scroll-disabled="loading">
          <div style="width: 100%" v-for="job in jobList" :key="job.id">
            <el-card shadow="hover" class="job-card" @click="handleJobClick(job.id)" :class="{ 'active-card':selectedCardId.value === Number(job.id)
 }">
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
                <span class="job-type">{{ getJobType(job.jobType) }}</span>
              </div>
              <div class="recruiter">
                <el-avatar :size="30" :src="job.recruiterAvatar"/>
                <span>{{ job.recruiterName }}·招聘者</span>
              </div>
            </el-card>
          </div>
          <!-- 加载状态 -->
          <div class="loading-status" v-if="loading">加载中...</div>
          <div class="no-more" v-if="noMore">没有更多职位了</div>
        </div>
      </el-col>
      <el-col :span="15">
        <div class="grid-content ep-bg-purple"/>
        <!--    职位详情页-->
        <JobDetails :selectedJob="selectedJob"/>
      </el-col>
    </div>
  </el-row>


</template>

<script setup>
import {ref, onMounted} from 'vue'
import {Location} from '@element-plus/icons-vue'
import {getJobCard} from '@/api/job.js'
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
    const response = await getJobCard(currentPage.value, pageSize.value);
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
  padding: 10px;
  display: flex;
  background-color: #e7f0fa;

  .job-list {
    //max-width: 600px;
    min-width: 400px;
    min-height: 200px;
    margin-right: 10px;
  }

}

.list-header {
  color: #409eff;
}

.job-card {
  margin-bottom: 5px;
  cursor: pointer;
  transition: all 0.3s; /* 调整过渡时间为 0.3s */
  width: 97%; /* 固定宽度 */
  height: 100%; /* 固定高度 */
  position: relative; /* 为子元素的绝对定位提供参考 */
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 10px;
  overflow: hidden; /* 确保内容不会超出卡片范围 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  /* 覆盖内部 el-card__body 的 padding */
  ::v-deep(.el-card__body) {
    padding: 0;
  }

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &.active-card {
    border-color: #409EFF !important; /* 使用!important确保覆盖 */
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);

    .job-header h4 {
      color: #409EFF !important;
    }
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
      //flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .salary {
      color: #f56c6c;
      font-weight: bold;
      font-size: 15px;
      white-space: nowrap;
      margin-left: auto;
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
}

.loading-status, .no-more {
  text-align: center;
  padding: 20px 0;
  color: #909399;
}
</style>

<script>
export default {
  name: 'Recommend',
  mounted() {
    console.log('推荐职位组件已加载')
  }
}
</script> 
<template>

  <div class="recommend-container">
    <!--  左侧职位列表-->
    <div class="job-list" v-infinite-scroll="loadMore" :infinite-scroll-disabled="loading">
      <!--      <el-row :gutter="20">-->
      <!--        -->
      <!--      </el-row>-->
      <div style="width: 100%" v-for="job in jobList" :key="job.id">
        <el-card shadow="hover" class="job-card" @click="handleJobClick(job.id)">
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
      </div>

      <!-- 加载状态 -->
      <div class="loading-status" v-if="loading">加载中...</div>
      <div class="no-more" v-if="noMore">没有更多数据了</div>
    </div>
    <!--    职位详情页-->
    <JobDetails :selectedJob="selectedJob"/>

  </div>
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

// 加载更多数据
const loadMore = async () => {
  if (loading.value || noMore.value) return

  loading.value = true
  try {
    const response = await getJobCard(currentPage.value, pageSize.value)
    if (response.data.code === 200) {
      const newJobs = response.data.data.records
      total.value = response.data.data.total

      // 追加新数据而不是替换
      jobList.value = [...jobList.value, ...newJobs]

      // 更新页码和状态
      currentPage.value++
      noMore.value = jobList.value.length >= total.value
      // 如果是第一次加载，请求第一个职位的详情
      if (isFirstLoad && jobList.value.length > 0) {
        await handleJobClick(jobList.value[0].id)
        isFirstLoad = false
      }
    }
  } catch (error) {
    console.error('获取职位列表失败：', error)
  } finally {
    loading.value = false
  }
}


// 获取职位类型
const getJobType = (type) => {
  const typeMap = {
    1: '全职',
    2: '实习',
    3: '兼职'
  }
  return typeMap[type] || '未知'
}
// 处理职位卡片点击事件
const handleJobClick = async (jobId) => {
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

  .job-list {
    //max-width: 600px;
    min-width: 400px;
    margin-right: 10px;
  }

}

.job-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #428ef4;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
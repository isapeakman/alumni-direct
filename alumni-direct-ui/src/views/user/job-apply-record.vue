<template>
  <div class="job-apply-timeline" ref="scrollContainer" @scroll="handleScroll">
    <h1>投递记录时间轴</h1>
    <div class="timeline-container" ref="timelineContainer">
      <div
          v-for="(record, index) in jobRecords"
          :key="record.jobId + '-' + index"
          class="timeline-item"
          :class="{'left-item': index % 2 === 0, 'right-item': index % 2 !== 0}"
      >
        <div class="timeline-content">
          <div class="timestamp">{{ formatDate(record.applyTime) }}</div>
          <div class="job-card" @click="showJobDetail(record)">
            <h3>{{ record.title }}</h3>
            <p>{{ record.companyName }} | {{ record.location }}</p>
            <p>薪资范围：{{ formatSalary(record.minSalary, record.maxSalary) }}</p>
            <el-tag :type="getStatusTagType(record.status)">
              {{ getJobStatusText(record.status) }}
            </el-tag>
          </div>
        </div>
      </div>
      <div v-if="loading" class="loading-more">
        <el-icon class="is-loading">
          <Loading/>
        </el-icon>
        加载中...
      </div>
      <div v-if="noMore" class="no-more">没有更多记录了</div>
    </div>

    <!-- 修改后的弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="selectedJob ? selectedJob.title : ''"
        width="50%"
        custom-class="job-details-dialog"
    >
      <div v-if="selectedJob" class="job-details-container">
        <div class="job-details">
          <!-- 标题与薪资 -->
          <div class="job-title-row">
            <div class="job-title">{{ selectedJob.title }}</div>
            <div class="job-salary">{{ formatSalary(selectedJob.minSalary, selectedJob.maxSalary) }}</div>
            <el-button
                type="primary"
                plain
                @click="goChat(selectedJob.createId, selectedJob.title)"
                class="apply-button"
                :disabled="!isJobActive(selectedJob.status)"
            >
              发起沟通
              <el-tooltip
                  v-if="!isJobActive(selectedJob.status)"
                  content="该职位已关闭，无法沟通"
                  placement="top"
              >
                <el-icon>
                  <Warning/>
                </el-icon>
              </el-tooltip>
            </el-button>
          </div>

          <!-- 职位类型标签 -->
          <el-tag type="info" size="small" class="job-type-tag">
            {{ getJobType(selectedJob.jobType) }}
          </el-tag>

          <!-- 发布时间 -->
          <div class="publish-time">
            发布时间：{{ formatDateTime(selectedJob.publishTime) }}
          </div>

          <!-- 职位详情 -->
          <h3 class="section-title">职位详情</h3>
          <div class="job-desc">
            <p v-for="(line, index) in jobDescLines" :key="index" class="desc-line">
              {{ line }}
            </p>
          </div>

          <!-- 工作地点 -->
          <p class="location">工作地点：
            <el-icon>
              <Location/>
            </el-icon>
            {{ selectedJob.location }}
          </p>

          <!-- 招聘者信息 -->
          <div class="divider-section">
            <el-divider></el-divider>
            <el-avatar :size="30" :src="selectedJob.recruiterAvatar || defaultAvatar"/>
            招聘者：{{ selectedJob.recruiterName }}
          </div>

          <!-- 公司名称 -->
          <p class="company-name">公司名称：{{ selectedJob.companyName }}</p>

          <!-- 职位状态 -->
          <div class="status-section">
            <el-tag
                :type="getStatusTagType(selectedJob.status)"
                size="small"
                class="status-tag"
            >
              {{ getJobStatusText(selectedJob.status) }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed} from 'vue'
import {Location, Warning} from "@element-plus/icons-vue"
import {getJobApplyRecord} from '@/api/job.js'
import dayjs from 'dayjs'
import router from '@/router/index.js'
import {ElMessage} from "element-plus";

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const jobRecords = ref([])
const dialogVisible = ref(false)
const selectedJob = ref(null)
// 分页相关状态
const currentPage = ref(1)
const pageSize = 10
const loading = ref(false)
const noMore = ref(false)
const timelineContainer = ref(null)
// 添加滚动容器引用
const scrollContainer = ref(null)

// 按时间降序排序的记录
const sortedRecords = computed(() => {
  return [...jobRecords.value].sort((a, b) => {
    return new Date(b.applyTime) - new Date(a.applyTime)
  })
})

const jobDescLines = computed(() => {
  if (!selectedJob.value?.jobDesc) return []
  return selectedJob.value.jobDesc
      .split('\n')
      .filter(line => line.trim() !== '')
      .map(line => line.trim())
})

// 获取投递记录
const fetchJobRecords = async () => {
  if (loading.value || noMore.value) return

  loading.value = true
  try {
    const res = await getJobApplyRecord(currentPage.value, pageSize)
    if (res.data.code === 200) {
      const newRecords = res.data.data.records.map(record => ({
        ...record,
        recruiterAvatar: record.recruiterAvatar || defaultAvatar
      }))

      if (newRecords.length === 0) {
        noMore.value = true
      } else {
        jobRecords.value = [...jobRecords.value, ...newRecords]
        currentPage.value++
      }
    }
  } catch (error) {
    console.error('获取投递记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 修改后的 handleScroll 方法
const handleScroll = () => {
  const container = scrollContainer.value
  if (!container || loading.value || noMore.value) return

  // 计算是否滚动到底部
  const {scrollTop, scrollHeight, clientHeight} = container
  const isBottom = scrollHeight - (scrollTop + clientHeight) < 50

  if (isBottom) {
    fetchJobRecords()
  }
}

const showJobDetail = (job) => {
  selectedJob.value = job
  dialogVisible.value = true
}


const formatSalary = (min, max) => {
  if (!min && !max) return '薪资面议'
  if (!max) return `${min}k以上`
  return `${min}-${max}k`
}

const getJobType = (type) => {
  const typeMap = {
    0: '全职',
    1: '实习',
    2: '兼职'
  }
  return typeMap[type] || '未知'
}
const getStatusClass = (status) => {
  return status === 2 ? 'success' : 'info'
}


const isJobActive = (status) => {
  return status === 2 // 只有状态为2表示发布中
}
const getJobStatusText = (status) => {
  const statusMap = {
    '0': '已关闭',
    '1': '已关闭',
    '2': '发布中',
    '3': '已关闭',
    '4': '已关闭',
    '5': '已关闭'
  }
  return statusMap[status] || '未知状态'
}

// 状态标签类型
const getStatusTagType = (status) => {
  return status === 2 ? 'success' : 'info'
}

// 修改后的沟通方法
const goChat = (createId, title) => {
  if (!isJobActive(selectedJob.value.status)) {
    ElMessage.warning('该职位已关闭，无法沟通')
    return
  }
  router.push({
    path: "/chat",
    query: {
      createId: createId,
      title: title
    }
  })
  dialogVisible.value = false
}

const formatDate = (date) => {
  return dayjs(date).format('MM-DD HH:mm')
}

const formatDateTime = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

fetchJobRecords()
</script>

<style scoped lang="scss">
.job-apply-timeline {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  height: calc(100vh - 60px); /* 设置固定高度以启用滚动 */
  overflow-y: auto; /* 启用垂直滚动 */
}

.timeline-container {
  position: relative;
  padding: 20px 0;
  min-height: 100%;
}

/* 加载更多样式 */
.loading-more, .no-more {
  text-align: center;
  padding: 20px 0;
  color: #909399;
  font-size: 14px;

  .el-icon {
    margin-right: 8px;
    animation: rotating 2s linear infinite;
  }
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}


.timeline-container::before {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 2px;
  background-color: #e0e0e0;
  transform: translateX(-50%);
}

.timeline-item {
  position: relative;
  margin-bottom: 40px;
  width: 100%;
}

.timeline-item::after {
  content: '';
  position: absolute;
  top: 20px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background-color: #409eff;
  z-index: 1;
}

.left-item::after {
  left: 50%;
  transform: translateX(-8px);
}

.right-item::after {
  left: 50%;
  transform: translateX(-8px);
}

.timeline-content {
  position: relative;
  width: calc(50% - 40px);
  padding: 15px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  background-color: #fff;
}

.left-item .timeline-content {
  margin-right: auto;
  margin-left: 0;
}

.right-item .timeline-content {
  margin-left: auto;
  margin-right: 0;
}

.timestamp {
  position: absolute;
  top: 10px;
  font-size: 12px;
  color: #999;
}

.left-item .timestamp {
  right: -120px;
  text-align: left;
}

.right-item .timestamp {
  left: -120px;
  text-align: right;
}

.job-card {
  cursor: pointer;
  transition: all 0.3s;

  h3 {
    margin: 0 0 8px 0;
    color: #333;
    font-size: 16px;
  }

  p {
    margin: 4px 0;
    color: #666;
    font-size: 14px;
  }

  &:hover {
    transform: translateY(-3px);
  }
}

.status {
  font-weight: bold;
  margin-top: 8px !important;
}

.status-pending {
  color: #e6a23c;
}

.status-success {
  color: #67c23a; /* Green */
}

.status-closed {
  color: #909399; /* Gray */
}

.status-error {
  color: #909399; /* Gray */
}

/* 详情弹窗样式 - 完全匹配你提供的设计 */
.job-details-container {
  padding: 10px;
  max-height: 70vh;
  overflow-y: auto;
}

.job-details {
  .job-title-row {
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .job-title {
      font-size: 20px;
      color: #303133;
      margin-right: 16px;
    }

    .job-salary {
      font-size: 20px;
      color: #409EFF;
      font-weight: bold;
      margin-right: auto;
    }

    .apply-button {
      margin-left: 20px;
    }
  }

  .job-type-tag {
    margin-bottom: 12px;
  }

  .publish-time {
    color: #909399;
    font-size: 12px;
    margin-bottom: 16px;
  }

  .section-title {
    color: #303133;
    font-size: 16px;
    margin: 20px 0 10px 0;
    padding-bottom: 5px;
    border-bottom: 1px solid #f0f0f0;
  }

  .job-desc {
    .desc-line {
      margin: 8px 0;
      line-height: 1.6;
      color: #606266;
    }
  }

  .location {
    display: flex;
    align-items: center;
    margin: 15px 0;
    color: #606266;

    .el-icon {
      margin-right: 5px;
    }
  }

  .divider-section {
    display: flex;
    align-items: center;
    margin: 20px 0;

    .el-divider {
      flex: 1;
      margin-right: 15px;
    }

    .el-avatar {
      margin-right: 10px;
    }
  }

  .company-name {
    color: #606266;
    margin: 10px 0;
  }

  .status-section {
    margin-top: 20px;
    text-align: center;

    .el-tag {
      font-size: 14px;
      padding: 8px 15px;
    }
  }
}

@media (max-width: 768px) {
  .timeline-container::before {
    left: 20px;
  }

  .timeline-item::after {
    left: 20px;
  }

  .timeline-content {
    width: calc(100% - 60px);
    margin-left: 60px !important;
  }

  .left-item .timestamp,
  .right-item .timestamp {
    left: -40px;
    right: auto;
    text-align: left;
  }

  .job-details-dialog {
    width: 90% !important;
  }

  .job-title-row {
    flex-wrap: wrap;

    .job-title {
      width: 100%;
      margin-bottom: 8px;
    }

    .job-salary {
      margin-right: 0 !important;
    }
  }
}
</style>

<style lang="scss">
/* 全局覆盖对话框样式 */
.job-details-dialog {
  .el-dialog__header {
    border-bottom: 1px solid #f0f0f0;
    margin-right: 0;
  }

  .el-dialog__body {
    padding: 20px;
  }
}

.status-tag {
  margin-left: 10px;
}

/* 禁用按钮样式 */
.apply-button[disabled] {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
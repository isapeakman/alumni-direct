<template>
  <div class="job-apply-container" ref="scrollContainer" @scroll="handleScroll">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">投递记录</h1>
      <p class="page-subtitle">追踪你的职位申请进度</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-card">
      <div class="stat-item">
        <el-icon class="stat-icon">
          <component :is="Icons.Briefcase"/>
        </el-icon>
        <div class="stat-content">
          <span class="stat-value">{{ jobRecords.length }}</span>
          <span class="stat-label">投递总数</span>
        </div>
      </div>
      <div class="stat-item">
        <el-icon class="stat-icon text-success">
          <component :is="Icons.CheckCircle"/>
        </el-icon>
        <div class="stat-content">
          <span class="stat-value">{{ activeCount }}</span>
          <span class="stat-label">进行中</span>
        </div>
      </div>
      <div class="stat-item">
        <el-icon class="stat-icon text-warning">
          <component :is="Icons.Clock"/>
        </el-icon>
        <div class="stat-content">
          <span class="stat-value">{{ closedCount }}</span>
          <span class="stat-label">已结束</span>
        </div>
      </div>
    </div>

    <!-- 时间轴 -->
    <div class="timeline-section">
      <div v-if="jobRecords.length > 0" class="timeline-container">
        <div
            v-for="(record, index) in sortedRecords"
            :key="record.jobId + '-' + index"
            class="timeline-item"
        >
          <div class="timeline-node">
            <div class="node-dot"></div>
            <div v-if="index < sortedRecords.length - 1" class="node-line"></div>
          </div>
          <div class="timeline-card" @click="showJobDetail(record)">
            <div class="card-header">
              <div class="job-info">
                <h3 class="job-title">{{ record.title }}</h3>
                <p class="job-meta">{{ record.companyName }} · {{ record.location }}</p>
              </div>
              <div class="card-badge">
                <el-tag :type="getStatusTagType(record.status)" size="small">
                  {{ getJobStatusText(record.status) }}
                </el-tag>
              </div>
            </div>
            <div class="card-body">
              <div class="salary-info">
                <el-icon>
                  <Wallet/>
                </el-icon>
                <span>{{ formatSalary(record.minSalary, record.maxSalary) }}</span>
              </div>
              <div class="apply-time">
                <el-icon>
                  <component :is="Icons.Clock"/>
                </el-icon>
                <span>{{ formatDate(record.applyTime) }}</span>
              </div>
            </div>
            <div class="card-footer">
              <span class="recruiter">
                <el-avatar :size="24" :src="record.recruiterAvatar || defaultAvatar"/>
                {{ record.recruiterName }}
              </span>
              <span class="view-detail">查看详情 <el-icon><component :is="Icons.ChevronRight"/></el-icon></span>
            </div>
          </div>
        </div>

        <div v-if="loading" class="loading-more">
          <el-icon class="loading-icon">
            <Loading/>
          </el-icon>
          加载中...
        </div>
        <div v-if="noMore" class="no-more">没有更多记录了</div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-icon size="64" color="#cbd5e1">
          <File/>
        </el-icon>
        <p>暂无投递记录</p>
        <p class="empty-hint">快去搜索并投递心仪的职位吧</p>
      </div>
    </div>

    <!-- 职位详情弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="selectedJob ? selectedJob.title : ''"
        width="50%"
        custom-class="job-details-dialog"
        class="detail-dialog"
    >
      <div v-if="selectedJob" class="job-details-container">
        <div class="details-header">
          <div class="header-left">
            <div class="details-salary">{{ formatSalary(selectedJob.minSalary, selectedJob.maxSalary) }}</div>
            <el-tag type="info" size="small" class="details-type">{{ getJobType(selectedJob.jobType) }}</el-tag>
          </div>
          <el-button
              type="primary"
              plain
              @click="goChat(selectedJob.createId, selectedJob.title)"
              :disabled="!isJobActive(selectedJob.status)"
              class="chat-btn"
          >
            <el-icon>
              <component :is="Icons.MessageCircle"/>
            </el-icon>
            发起沟通
          </el-button>
        </div>

        <div class="details-body">
          <div class="detail-section">
            <h4 class="section-title">职位详情</h4>
            <div class="job-desc">
              <p v-for="(line, index) in jobDescLines" :key="index" class="desc-line">
                {{ line }}
              </p>
            </div>
          </div>

          <div class="detail-section">
            <h4 class="section-title">基本信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <el-icon class="info-icon">
                  <MapPin/>
                </el-icon>
                <span class="info-label">工作地点</span>
                <span class="info-value">{{ selectedJob.location }}</span>
              </div>
              <div class="info-item">
                <el-icon class="info-icon">
                  <component :is="Icons.Calendar"/>
                </el-icon>
                <span class="info-label">发布时间</span>
                <span class="info-value">{{ formatDateTime(selectedJob.publishTime) }}</span>
              </div>
              <div class="info-item">
                <el-icon class="info-icon">
                  <component :is="Icons.Building"/>
                </el-icon>
                <span class="info-label">公司名称</span>
                <span class="info-value">{{ selectedJob.companyName }}</span>
              </div>
              <div class="info-item">
                <el-icon class="info-icon">
                  <component :is="Icons.User"/>
                </el-icon>
                <span class="info-label">招聘者</span>
                <div class="recruiter-info">
                  <el-avatar :size="28" :src="selectedJob.recruiterAvatar || defaultAvatar"/>
                  <span>{{ selectedJob.recruiterName }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4 class="section-title">职位状态</h4>
            <div class="status-display">
              <el-tag :type="getStatusTagType(selectedJob.status)" class="status-tag">
                {{ getJobStatusText(selectedJob.status) }}
              </el-tag>
              <span v-if="!isJobActive(selectedJob.status)" class="status-hint">
                该职位已关闭，无法发起沟通
              </span>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed} from 'vue'
import * as Icons from "@element-plus/icons-vue"
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

// 统计进行中的投递数量
const activeCount = computed(() => {
  return jobRecords.value.filter(record => record.status === 2).length
})

// 统计已结束的投递数量
const closedCount = computed(() => {
  return jobRecords.value.filter(record => record.status !== 2).length
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

<style lang="scss" scoped>
.job-apply-container {
  padding: 30px;
  max-width: 900px;
  margin: 0 auto;
  min-height: calc(100vh - 60px);

  .page-header {
    margin-bottom: 24px;

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

/* 统计卡片 */
.stats-card {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%);
  border-radius: 16px;

  .stat-item {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 14px;

    .stat-icon {
      width: 48px;
      height: 48px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      color: #fff;

      &.text-success {
        background: rgba(34, 197, 94, 0.3);
      }

      &.text-warning {
        background: rgba(251, 146, 60, 0.3);
      }
    }

    .stat-content {
      display: flex;
      flex-direction: column;

      .stat-value {
        font-size: 24px;
        font-weight: 700;
        color: #fff;
      }

      .stat-label {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

/* 时间轴区域 */
.timeline-section {
  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 80px 40px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

    p {
      margin-top: 16px;
      font-size: 14px;
      color: #94a3b8;
    }

    .empty-hint {
      font-size: 12px;
      color: #cbd5e1;
      margin-top: 6px;
    }
  }
}

.timeline-container {
  position: relative;
}

.timeline-item {
  display: flex;
  gap: 20px;
  margin-bottom: 0;

  &:last-child .timeline-node .node-line {
    display: none;
  }
}

.timeline-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 24px;

  .node-dot {
    width: 14px;
    height: 14px;
    border-radius: 50%;
    background: linear-gradient(135deg, #0ea5e9, #0284c7);
    flex-shrink: 0;
    box-shadow: 0 0 0 4px rgba(14, 165, 233, 0.1);
  }

  .node-line {
    width: 2px;
    flex: 1;
    background: linear-gradient(180deg, #e2e8f0, transparent);
    margin-top: 12px;
  }
}

.timeline-card {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateX(8px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;

    .job-info {
      .job-title {
        font-size: 16px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 6px;
      }

      .job-meta {
        font-size: 13px;
        color: #64748b;
        margin: 0;
      }
    }

    .card-badge {
      :deep(.el-tag) {
        border: none;
        font-size: 11px;
        padding: 3px 10px;
        border-radius: 12px;

        &.el-tag--success {
          background: rgba(34, 197, 94, 0.1);
          color: #22c55e;
        }

        &.el-tag--info {
          background: rgba(148, 163, 184, 0.1);
          color: #64748b;
        }
      }
    }
  }

  .card-body {
    display: flex;
    gap: 24px;
    margin-bottom: 16px;
    padding: 12px 0;
    border-top: 1px solid #f1f5f9;
    border-bottom: 1px solid #f1f5f9;

    .salary-info, .apply-time {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      color: #64748b;

      :deep(.el-icon) {
        font-size: 14px;
      }
    }

    .salary-info {
      color: #ef4444;
      font-weight: 600;
    }
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .recruiter {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 12px;
      color: #94a3b8;

      :deep(.el-avatar) {
        border: 1px solid #f1f5f9;
      }
    }

    .view-detail {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #0ea5e9;
    }
  }
}

.loading-more, .no-more {
  text-align: center;
  padding: 24px 0;
  color: #94a3b8;
  font-size: 13px;

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

/* 详情弹窗样式 */
.detail-dialog {
  ::v-deep(.el-dialog__header) {
    padding: 20px 24px;
    border-bottom: 1px solid #f1f5f9;

    span {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
    }
  }

  ::v-deep(.el-dialog__body) {
    padding: 24px;
  }
}

.job-details-container {
  .details-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 20px;
    border-bottom: 1px solid #f1f5f9;
    margin-bottom: 20px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 12px;

      .details-salary {
        font-size: 20px;
        font-weight: 700;
        color: #ef4444;
      }

      .details-type {
        background: #f1f5f9;
        color: #64748b;
        border: none;
      }
    }

    .chat-btn {
      background: rgba(14, 165, 233, 0.1);
      color: #0ea5e9;
      border-color: transparent;
      padding: 10px 20px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      gap: 6px;

      &:hover:not(:disabled) {
        background: rgba(14, 165, 233, 0.2);
        border-color: transparent;
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
  }

  .details-body {
    .detail-section {
      margin-bottom: 24px;

      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: #1e293b;
        margin: 0 0 16px;
        padding-bottom: 10px;
        border-bottom: 1px solid #f1f5f9;
      }
    }

    .job-desc {
      .desc-line {
        margin: 8px 0;
        line-height: 1.7;
        color: #64748b;
        font-size: 14px;
      }
    }

    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;

      .info-item {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px;
        background: #f8fafc;
        border-radius: 8px;

        .info-icon {
          width: 20px;
          height: 20px;
          color: #0ea5e9;
          flex-shrink: 0;
        }

        .info-label {
          font-size: 12px;
          color: #94a3b8;
          width: 60px;
          flex-shrink: 0;
        }

        .info-value {
          font-size: 13px;
          color: #334155;
        }

        .recruiter-info {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 13px;
          color: #334155;

          :deep(.el-avatar) {
            border: 1px solid #e2e8f0;
          }
        }
      }
    }

    .status-display {
      display: flex;
      align-items: center;
      gap: 12px;

      .status-tag {
        font-size: 14px;
        padding: 8px 20px;
        border-radius: 20px;
        border: none;

        &.el-tag--success {
          background: rgba(34, 197, 94, 0.1);
          color: #22c55e;
        }

        &.el-tag--info {
          background: rgba(148, 163, 184, 0.1);
          color: #64748b;
        }
      }

      .status-hint {
        font-size: 13px;
        color: #94a3b8;
      }
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .job-apply-container {
    padding: 20px;
  }

  .stats-card {
    flex-direction: column;
    gap: 16px;
    padding: 20px;

    .stat-item {
      padding-bottom: 16px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);

      &:last-child {
        border-bottom: none;
        padding-bottom: 0;
      }
    }
  }

  .timeline-item {
    gap: 16px;
  }

  .timeline-card {
    padding: 16px;
  }

  .info-grid {
    grid-template-columns: 1fr !important;
  }

  ::v-deep(.el-dialog) {
    width: 90% !important;
  }
}
</style>
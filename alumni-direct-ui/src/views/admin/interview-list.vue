<template>
  <div class="interview-list-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>面试会话列表</h2>
      <p>查看所有AI模拟面试记录</p>
    </div>

    <!-- 会话列表 -->
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <el-icon>
            <component :is="Icons.VideoCamera"/>
          </el-icon>
          <span>面试记录</span>
          <span class="record-count">共 {{ sessions.length }} 条记录</span>
        </div>
      </template>

      <div v-if="sessions.length === 0" class="empty-state">
        <el-icon class="empty-icon">
          <component :is="Icons.VideoCamera"/>
        </el-icon>
        <p>暂无面试记录</p>
        <el-button type="primary" @click="goToResumeParse">去解析简历开始面试</el-button>
      </div>

      <el-table v-else :data="sessions" border class="interview-table">
        <el-table-column prop="id" label="会话ID" width="200">
          <template #default="scope">
            <span class="session-id">{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="candidateName" label="候选人" width="120">
          <template #default="scope">
            <span>{{ scope.row.candidateName || '未填写' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="desiredPosition" label="目标岗位" width="180">
          <template #default="scope">
            <span>{{ scope.row.desiredPosition || '未填写' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'ACTIVE' ? 'warning' : 'success'">
              {{ scope.row.status === 'ACTIVE' ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="120">
          <template #default="scope">
            <span>{{ formatDuration(scope.row.duration) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="endedAt" label="结束时间" width="180">
          <template #default="scope">
            <span>{{ scope.row.endedAt ? formatDateTime(scope.row.endedAt) : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
                size="small"
                type="primary"
                @click="viewDetail(scope.row)"
                :disabled="!scope.row.id"
            >
              <el-icon>
                <component :is="Icons.Document"/>
              </el-icon>
              查看详情
            </el-button>
            <el-button
                v-if="scope.row.status === 'ENDED' && scope.row.evaluationReport"
                size="small"
                type="success"
                @click="viewReport(scope.row)"
            >
              <el-icon>
                <component :is="Icons.FileText"/>
              </el-icon>
              评估报告
            </el-button>
            <el-button
                v-if="scope.row.status === 'ACTIVE'"
                size="small"
                type="warning"
                @click="continueInterview(scope.row)"
            >
              <el-icon>
                <component :is="Icons.VideoCamera"/>
              </el-icon>
              继续面试
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="showDetailDialog" title="面试会话详情" width="80%" destroy-on-close>
      <div v-if="selectedSession" class="detail-content">
        <div class="detail-section">
          <h3>基本信息</h3>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="detail-item">
                <span class="label">会话ID</span>
                <span class="value">{{ selectedSession.id }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="detail-item">
                <span class="label">候选人</span>
                <span class="value">{{ selectedSession.candidateName || '未填写' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="detail-item">
                <span class="label">目标岗位</span>
                <span class="value">{{ selectedSession.desiredPosition || '未填写' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="detail-item">
                <span class="label">状态</span>
                <el-tag :type="selectedSession.status === 'ACTIVE' ? 'warning' : 'success'">
                  {{ selectedSession.status === 'ACTIVE' ? '进行中' : '已结束' }}
                </el-tag>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="detail-item">
                <span class="label">时长</span>
                <span class="value">{{ formatDuration(selectedSession.duration) }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="detail-item">
                <span class="label">创建时间</span>
                <span class="value">{{ formatDateTime(selectedSession.createdAt) }}</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="detail-section" v-if="selectedSession.messages && selectedSession.messages.length > 0">
          <h3>对话记录</h3>
          <div class="message-list">
            <div
                v-for="(msg, index) in selectedSession.messages"
                :key="index"
                class="message-item"
                :class="msg.role === 'assistant' ? 'ai-message' : 'user-message'"
            >
              <div class="message-role">{{ msg.role === 'assistant' ? 'AI面试官' : '我' }}</div>
              <div class="message-content">{{ msg.content }}</div>
              <div class="message-time">{{ formatDateTime(msg.createdAt) }}</div>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="selectedSession.evaluationReport">
          <h3>评估报告</h3>
          <div class="report-content">
            <p v-for="(line, index) in formatReport(selectedSession.evaluationReport)" :key="index">{{ line }}</p>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button
            v-if="selectedSession?.status === 'ACTIVE'"
            type="primary"
            @click="continueInterview(selectedSession)"
        >
          继续面试
        </el-button>
      </template>
    </el-dialog>

    <!-- 评估报告弹窗 -->
    <el-dialog v-model="showReportDialog" title="面试评估报告" width="70%" destroy-on-close>
      <div v-if="selectedSession?.evaluationReport" class="report-container">
        <div class="report-header">
          <div class="report-info">
            <p><strong>候选人：</strong>{{ selectedSession.candidateName || '未填写' }}</p>
            <p><strong>目标岗位：</strong>{{ selectedSession.desiredPosition || '未填写' }}</p>
            <p><strong>面试时间：</strong>{{ formatDateTime(selectedSession.createdAt) }}</p>
            <p><strong>时长：</strong>{{ formatDuration(selectedSession.duration) }}</p>
          </div>
        </div>
        <div class="report-body">
          <p v-for="(line, index) in formatReport(selectedSession.evaluationReport)" :key="index">{{ line }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="showReportDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import * as Icons from '@element-plus/icons-vue'
import {listSessions, getInterviewSession} from '@/api/interview'

const router = useRouter()
const sessions = ref([])
const showDetailDialog = ref(false)
const showReportDialog = ref(false)
const selectedSession = ref(null)

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds || seconds < 0) return '-'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}分${secs}秒`
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  try {
    const date = new Date(dateStr)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (e) {
    return dateStr
  }
}

// 格式化报告内容
const formatReport = (content) => {
  if (!content) return []
  return content.split('\n').filter(line => line.trim() !== '')
}

// 获取会话列表
const loadSessions = async () => {
  try {
    const response = await listSessions()
    if (response.data.code === 200) {
      sessions.value = response.data.data
    } else {
      ElMessage.error(response.data.message || '获取会话列表失败')
    }
  } catch (error) {
    console.error('获取会话列表失败:', error)
    ElMessage.error('获取会话列表失败: ' + (error.message || 'Unknown error'))
  }
}

// 查看详情
const viewDetail = async (session) => {
  try {
    const response = await getInterviewSession(session.id)
    if (response.data.code === 200) {
      selectedSession.value = response.data.data
      showDetailDialog.value = true
    } else {
      ElMessage.error(response.data.message || '获取会话详情失败')
    }
  } catch (error) {
    console.error('获取会话详情失败:', error)
    ElMessage.error('获取会话详情失败: ' + (error.message || 'Unknown error'))
  }
}

// 查看评估报告
const viewReport = (session) => {
  selectedSession.value = session
  showReportDialog.value = true
}

// 继续面试
const continueInterview = (session) => {
  showDetailDialog.value = false
  router.push(`/admin/interview/${session.id}`)
}

// 跳转到简历解析页面
const goToResumeParse = () => {
  router.push('/admin/resume-async')
}

// 页面加载时获取会话列表
onMounted(() => {
  loadSessions()
})
</script>

<style scoped lang="scss">
.interview-list-container {
  padding: 20px;
  min-height: calc(100vh - 140px);
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);

  .page-header {
    margin-bottom: 20px;

    h2 {
      margin: 0 0 5px 0;
      font-size: 24px;
      color: #1a1a2e;
    }

    p {
      margin: 0;
      color: #999;
      font-size: 14px;
    }
  }

  .list-card {
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

    .card-header {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 16px;
      font-weight: 600;
      color: #1a1a2e;

      .record-count {
        margin-left: auto;
        font-size: 13px;
        font-weight: normal;
        color: #999;
      }
    }

    .empty-state {
      text-align: center;
      padding: 60px 0;

      .empty-icon {
        font-size: 64px;
        color: #ccc;
        margin-bottom: 20px;
      }

      p {
        color: #999;
        margin-bottom: 20px;
      }
    }

    .interview-table {
      :deep(.el-table__header) {
        background: #f8f9fa;
      }

      :deep(.el-table__body tr:hover) {
        background: #f8f9fa;
      }

      .session-id {
        font-family: monospace;
        font-size: 12px;
        color: #667eea;
      }
    }
  }

  .detail-content {
    .detail-section {
      margin-bottom: 25px;

      h3 {
        margin: 0 0 15px 0;
        font-size: 16px;
        font-weight: 600;
        color: #1a1a2e;
        border-bottom: 2px solid #667eea;
        padding-bottom: 8px;
      }

      .detail-item {
        margin-bottom: 10px;

        .label {
          display: block;
          font-size: 12px;
          color: #999;
          margin-bottom: 4px;
        }

        .value {
          font-size: 14px;
          color: #333;
        }
      }
    }

    .message-list {
      max-height: 300px;
      overflow-y: auto;
      padding: 10px;
      background: #f8f9fa;
      border-radius: 10px;

      .message-item {
        margin-bottom: 15px;
        padding: 12px;
        border-radius: 8px;

        &.ai-message {
          background: #f0fdf4;
          border: 1px solid #bbf7d0;
        }

        &.user-message {
          background: #e0e7ff;
          border: 1px solid #c7d2fe;
        }

        .message-role {
          font-size: 12px;
          color: #999;
          margin-bottom: 5px;
        }

        .message-content {
          font-size: 14px;
          line-height: 1.6;
          color: #333;
        }

        .message-time {
          font-size: 11px;
          color: #bbb;
          margin-top: 5px;
        }
      }
    }

    .report-content {
      padding: 15px;
      background: #f8f9fa;
      border-radius: 10px;

      p {
        margin: 0 0 10px 0;
        line-height: 1.8;
        color: #333;
        font-size: 14px;
      }
    }
  }

  .report-container {
    .report-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
      border-radius: 10px;
      margin-bottom: 20px;

      .report-info {
        p {
          margin: 5px 0;
          color: #fff;
          font-size: 14px;
        }
      }
    }

    .report-body {
      p {
        margin: 0 0 12px 0;
        line-height: 1.8;
        color: #333;
        font-size: 14px;
      }
    }
  }
}
</style>

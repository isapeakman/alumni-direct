<template>
  <div class="resume-parse-container">
    <el-card class="parse-card">
      <template #header>
        <div class="card-header">
          <span>简历智能解析</span>
        </div>
      </template>

      <!-- 文件上传区域 -->
      <el-upload
          ref="uploadRef"
          class="upload-area"
          drag
          :auto-upload="false"
          :on-change="handleFileChange"
          :before-upload="beforeUpload"
          accept=".pdf,.jpg,.jpeg,.png,.gif,.bmp"
          :limit="1"
      >
        <el-icon class="el-icon--upload">
          <upload-filled/>
        </el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 PDF、JPG、PNG 等格式，文件大小不超过 10MB
          </div>
        </template>
      </el-upload>

      <!-- 任务处理状态 -->
      <div v-if="taskStatus" class="task-status">
        <el-progress
            :percentage="taskStatus.progress || 0"
            :status="getProgressStatus(taskStatus.status)"
            :stroke-width="20"
        />

        <div class="status-info">
          <el-tag :type="getStatusType(taskStatus.status)" size="large">
            {{ getStatusText(taskStatus.status) }}
          </el-tag>
          <p class="stage-text">{{ taskStatus.stage }}</p>

          <!-- 错误信息 -->
          <el-alert
              v-if="taskStatus.status === 'FAILED' && taskStatus.errorMessage"
              :title="taskStatus.errorMessage"
              type="error"
              :closable="false"
              show-icon
              class="error-alert"
          />
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button
            type="primary"
            size="large"
            :loading="isProcessing"
            :disabled="!selectedFile || isProcessing"
            @click="handleSubmitTask"
        >
          {{ isProcessing ? '解析中...' : '开始解析' }}
        </el-button>

        <el-button
            v-if="taskStatus?.status === 'COMPLETED'"
            type="success"
            size="large"
            @click="handleViewResult"
        >
          查看结果
        </el-button>

        <el-button
            v-if="isProcessing"
            size="large"
            @click="handleCancel"
        >
          取消
        </el-button>
      </div>

      <!-- 解析结果展示 -->
      <el-collapse v-if="taskStatus?.status === 'COMPLETED' && taskStatus.result" class="result-section">
        <el-collapse-item title="解析结果" name="1">
          <pre class="result-json">{{ JSON.stringify(taskStatus.result, null, 2) }}</pre>
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script setup>
import {ref, onUnmounted} from 'vue'
import {ElMessage} from 'element-plus'
import {UploadFilled} from '@element-plus/icons-vue'
import {submitResumeParseTask, getResumeParseTaskStatus} from '@/api/resume'

// 响应式数据
const uploadRef = ref(null)
const selectedFile = ref(null)
const taskStatus = ref(null)
const isProcessing = ref(false)
let pollingTimer = null

// 文件选择处理
const handleFileChange = (file) => {
  selectedFile.value = file.raw
  // 重置任务状态
  taskStatus.value = null
}

// 上传前验证
const beforeUpload = (file) => {
  const isValidType = [
    'application/pdf',
    'image/jpeg',
    'image/png',
    'image/gif',
    'image/bmp'
  ].includes(file.type)

  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('只支持 PDF 和图片格式！')
    return false
  }

  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB！')
    return false
  }

  return true
}

// 提交解析任务
const handleSubmitTask = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }

  try {
    isProcessing.value = true

    // 提交异步任务
    const response = await submitResumeParseTask(selectedFile.value)
    if (response.data.code === 200 && response.data.data.taskId) {
      const taskId = response.data.data.taskId
      ElMessage.success('任务已提交，正在解析...')

      // 开始轮询任务状态
      startPolling(taskId)
    } else {
      ElMessage.error(response.message || '提交任务失败')
      isProcessing.value = false
    }
  } catch (error) {
    console.error('提交任务失败:', error)
    ElMessage.error('提交任务失败: ' + (error.message || '未知错误'))
    isProcessing.value = false
  }
}

// 开始轮询任务状态
const startPolling = (taskId) => {
  // 立即查询一次
  pollTaskStatus(taskId)

  // 设置定时器，每20秒查询一次
  pollingTimer = setInterval(() => {
    pollTaskStatus(taskId)
  }, 20000)
}

// 查询任务状态
const pollTaskStatus = async (taskId) => {
  try {
    const response = await getResumeParseTaskStatus(taskId)

    // 注意：request.js 直接返回 response，所以需要 response.data 获取后端返回的 BaseResponse
    if (response.data.code === 200) {
      taskStatus.value = response.data.data  // 获取 BaseResponse.data 字段

      // 如果任务完成或失败，停止轮询
      if (response.data.data.status === 'COMPLETED' || response.data.data.status === 'FAILED') {
        stopPolling()
        isProcessing.value = false

        if (response.data.data.status === 'COMPLETED') {
          ElMessage.success('简历解析完成！')
        } else {
          ElMessage.error('简历解析失败: ' + (response.data.data.errorMessage || '未知错误'))
        }
      }
    }
  } catch (error) {
    console.error('查询任务状态失败:', error)
    // 查询失败不停止轮询，继续尝试
  }
}

// 停止轮询
const stopPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

// 获取进度条状态
const getProgressStatus = (status) => {
  switch (status) {
    case 'COMPLETED':
      return 'success'
    case 'FAILED':
      return 'exception'
    default:
      return undefined
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  switch (status) {
    case 'PENDING':
      return 'info'
    case 'PROCESSING':
      return 'warning'
    case 'COMPLETED':
      return 'success'
    case 'FAILED':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'PENDING':
      return '等待中'
    case 'PROCESSING':
      return '处理中'
    case 'COMPLETED':
      return '已完成'
    case 'FAILED':
      return '失败'
    default:
      return '未知'
  }
}

// 查看结果
const handleViewResult = () => {
  ElMessage.info('结果已在下方显示')
}

// 取消任务
const handleCancel = () => {
  stopPolling()
  isProcessing.value = false
  taskStatus.value = null
  ElMessage.info('已取消解析')
}

// 组件卸载时清理定时器
onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped lang="scss">
.resume-parse-container {
  max-width: 800px;
  margin: 20px auto;

  .parse-card {
    .card-header {
      font-size: 18px;
      font-weight: bold;
    }

    .upload-area {
      margin: 20px 0;
    }

    .task-status {
      margin: 20px 0;

      .status-info {
        margin-top: 15px;
        text-align: center;

        .stage-text {
          margin-top: 10px;
          font-size: 14px;
          color: #606266;
        }

        .error-alert {
          margin-top: 15px;
        }
      }
    }

    .action-buttons {
      margin: 20px 0;
      text-align: center;

      .el-button {
        margin: 0 10px;
      }
    }

    .result-section {
      margin-top: 20px;

      .result-json {
        background-color: #f5f7fa;
        padding: 15px;
        border-radius: 4px;
        overflow-x: auto;
        font-size: 13px;
        line-height: 1.6;
      }
    }
  }
}
</style>

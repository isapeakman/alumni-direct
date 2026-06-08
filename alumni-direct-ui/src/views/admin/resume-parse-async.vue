<template>
  <div class="resume-parse-container">
    <div class="parse-layout">
      <!-- 左侧：上传区域 -->
      <div class="left-panel">
        <el-card class="upload-card">
          <template #header>
            <div class="card-header">
              <span>Resume Upload</span>
            </div>
          </template>

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
            <div class="upload-icon">
              <el-icon class="el-icon--upload">
                <Document/>
              </el-icon>
            </div>
            <div class="el-upload__text">
              <span>Drag & Drop or</span>
              <em>Browse Files</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                PDF, JPG, PNG supported. Max 10MB
              </div>
            </template>
          </el-upload>

          <!-- 文件名显示 -->
          <div v-if="selectedFile" class="file-info">
            <el-icon class="file-icon">
              <Document/>
            </el-icon>
            <span class="file-name">{{ selectedFile.name }}</span>
            <span class="file-size">{{ formatFileSize(selectedFile.size) }}</span>
            <el-button type="text" class="remove-btn" @click="clearFile">
              <el-icon>
                <Document/>
              </el-icon>
            </el-button>
          </div>

          <!-- 解析计时器 -->
          <div class="timer-section" v-if="isProcessing || parseTime > 0">
            <div class="timer-label">
              <el-icon>
                <Clock/>
              </el-icon>
              <span>{{ isProcessing ? 'Parsing...' : 'Done in' }}</span>
            </div>
            <div class="timer-value">{{ formatTime(parseTime) }}</div>
          </div>

          <!-- 任务处理状态 -->
          <div v-if="taskStatus" class="task-status">
            <el-progress
                :percentage="taskStatus.progress || 0"
                :status="getProgressStatus(taskStatus.status)"
                :stroke-width="12"
                :show-text="false"
            />

            <div class="status-info">
              <el-tag :type="getStatusType(taskStatus.status)" size="large">
                {{ getStatusText(taskStatus.status) }}
              </el-tag>
              <p class="stage-text">{{ taskStatus.stage }}</p>

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
                class="parse-btn"
            >
              {{
                isProcessing ? 'Parsing...' : (taskStatus?.status === 'COMPLETED' ? 'Re-parse Resume' : 'Parse Resume')
              }}
            </el-button>

            <el-button
                v-if="isProcessing"
                size="large"
                @click="handleCancel"
                class="cancel-btn"
            >
              Cancel
            </el-button>

            <!-- 进入面试按钮 -->
            <el-button
                v-if="taskStatus?.status === 'COMPLETED' && resumeData"
                size="large"
                @click="showInterviewDialog = true"
                class="interview-btn"
            >
              <el-icon>
                <VideoCamera/>
              </el-icon>
              Start AI Interview
            </el-button>
          </div>
        </el-card>

        <!-- Resume Preview 小预览 -->
        <el-card class="mini-preview-card" v-if="resumeData">
          <template #header>
            <div class="card-header">
              <span>Resume Preview</span>
            </div>
          </template>
          <div class="mini-preview">
            <div class="preview-header">
              <div class="avatar-placeholder">
                <el-icon>
                  <User/>
                </el-icon>
              </div>
              <div class="preview-name">{{ resumeData.name || 'XXX' }}</div>
            </div>
            <div class="preview-content">
              <p class="preview-line">{{ resumeData.desiredPosition || 'Desired Position' }}</p>
              <p class="preview-line">{{ resumeData.location || 'Location' }}</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧：预览区域 -->
      <div class="right-panel">
        <!-- Tab切换 -->
        <el-card class="result-card">
          <template #header>
            <div class="card-header">
              <el-tabs v-model="activeTab" type="card" class="result-tabs">
                <el-tab-pane label="Structured Result" name="structured">
                </el-tab-pane>
                <el-tab-pane label="Raw JSON" name="json">
                </el-tab-pane>
              </el-tabs>
            </div>
          </template>

          <!-- 结构化结果 -->
          <div v-show="activeTab === 'structured'" class="structured-content">
            <div v-if="resumeData" class="resume-content">
              <!-- 编辑按钮 -->
              <div class="edit-button-container">
                <el-button type="primary" @click="handleEdit">
                  <el-icon>
                    <Document/>
                  </el-icon>
                  Edit Resume
                </el-button>
              </div>

              <!-- Basic Info -->
              <div class="section-card basic-card">
                <div class="section-header">
                  <div class="section-icon basic-icon">
                    <el-icon>
                      <User/>
                    </el-icon>
                  </div>
                  <h2 class="section-title">Basic Info</h2>
                </div>
                <div class="basic-grid">
                  <div class="basic-item">
                    <label>NAME</label>
                    <span>{{ resumeData.name || '-' }}</span>
                  </div>
                  <div class="basic-item">
                    <label>EMAIL</label>
                    <span>{{ resumeData.email || '-' }}</span>
                  </div>
                  <div class="basic-item">
                    <label>PHONE</label>
                    <span>{{ resumeData.phone || '-' }}</span>
                  </div>
                  <div class="basic-item">
                    <label>LOCATION</label>
                    <span>{{ resumeData.location || '-' }}</span>
                  </div>
                  <div class="basic-item">
                    <label>EDUCATION</label>
                    <span>{{ resumeData.education || '-' }}</span>
                  </div>
                  <div class="basic-item">
                    <label>MAJOR</label>
                    <span>{{ resumeData.major || '-' }}</span>
                  </div>
                  <div class="basic-item">
                    <label>EXPERIENCE</label>
                    <span>{{ resumeData.experienceYears || '-' }}</span>
                  </div>
                  <div class="basic-item">
                    <label>DESIRED POSITION</label>
                    <span>{{ resumeData.desiredPosition || '-' }}</span>
                  </div>
                </div>
              </div>

              <!-- Self Evaluation -->
              <div class="section-card eval-card" v-if="resumeData.selfEvaluation">
                <div class="section-header">
                  <div class="section-icon eval-icon">
                    <el-icon>
                      <Message/>
                    </el-icon>
                  </div>
                  <h2 class="section-title">Self Evaluation</h2>
                </div>
                <p class="eval-content">{{ resumeData.selfEvaluation }}</p>
              </div>

              <!-- Education Experience -->
              <div class="section-card education-card" v-if="resumeData.educationExperience?.length">
                <div class="section-header">
                  <div class="section-icon education-icon">
                    <el-icon>
                      <Briefcase/>
                    </el-icon>
                  </div>
                  <h2 class="section-title">Education</h2>
                </div>
                <div class="timeline">
                  <div class="timeline-item" v-for="(edu, index) in resumeData.educationExperience"
                       :key="'edu-' + index">
                    <div class="timeline-dot"></div>
                    <div class="timeline-content">
                      <div class="timeline-header">
                        <span class="timeline-title">{{ edu.school }}</span>
                        <span class="timeline-date">{{ edu.startDate }} - {{ edu.endDate }}</span>
                      </div>
                      <div class="timeline-body">
                        <span class="timeline-sub">{{ edu.degree }} | {{ edu.major }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Work Experience -->
              <div class="section-card work-card" v-if="resumeData.workExperience?.length">
                <div class="section-header">
                  <div class="section-icon work-icon">
                    <el-icon>
                      <Briefcase/>
                    </el-icon>
                  </div>
                  <h2 class="section-title">Work Experience</h2>
                </div>
                <div class="experience-list">
                  <div class="experience-card" v-for="(work, index) in resumeData.workExperience"
                       :key="'work-' + index">
                    <div class="experience-header">
                      <span class="experience-company">{{ work.company }}</span>
                      <span class="experience-date">{{ work.startDate }} - {{ work.endDate }}</span>
                    </div>
                    <div class="experience-position">{{ work.position }}</div>
                    <div class="experience-desc" v-if="work.description">{{ work.description }}</div>
                  </div>
                </div>
              </div>

              <!-- Project Experience -->
              <div class="section-card project-card" v-if="resumeData.projectExperience?.length">
                <div class="section-header">
                  <div class="section-icon project-icon">
                    <el-icon>
                      <Folder/>
                    </el-icon>
                  </div>
                  <h2 class="section-title">Projects</h2>
                </div>
                <div class="project-list">
                  <div class="project-card" v-for="(project, index) in resumeData.projectExperience"
                       :key="'project-' + index">
                    <div class="project-header">
                      <span class="project-name">{{ project.projectName }}</span>
                      <span class="project-role">{{ project.role }}</span>
                    </div>
                    <div class="project-date">{{ project.startDate }} - {{ project.endDate }}</div>
                    <div class="project-tags" v-if="project.technologies">
                      <el-tag v-for="tech in project.technologies.split(',')" :key="tech" class="tech-tag">
                        {{ tech.trim() }}
                      </el-tag>
                    </div>
                    <div class="project-desc" v-if="project.description">{{ project.description }}</div>
                  </div>
                </div>
              </div>

              <!-- Skills & Certificates -->
              <div class="section-card skills-card" v-if="resumeData.skills?.length || resumeData.certificates?.length">
                <div class="section-header">
                  <div class="section-icon skills-icon">
                    <el-icon>
                      <Collection/>
                    </el-icon>
                  </div>
                  <h2 class="section-title">Skills & Certificates</h2>
                </div>
                <div class="skills-grid">
                  <div class="skills-column">
                    <h3>Professional Skills</h3>
                    <div class="skill-tags">
                      <el-tag v-for="skill in resumeData.skills" :key="skill" class="skill-tag">
                        {{ skill }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="certificates-column">
                    <h3>Certificates</h3>
                    <div class="skill-tags">
                      <el-tag v-for="cert in resumeData.certificates" :key="cert" class="cert-tag">
                        {{ cert }}
                      </el-tag>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Empty State -->
            <div v-else class="empty-state">
              <div class="empty-icon">
                <el-icon>
                  <Document/>
                </el-icon>
              </div>
              <p>Upload a resume to see structured result</p>
            </div>
          </div>

          <!-- JSON结果 -->
          <div v-show="activeTab === 'json'" class="json-content">
            <div v-if="taskStatus?.result" class="json-code">
              <pre>{{ formattedJson }}</pre>
            </div>
            <div v-else class="empty-state">
              <div class="empty-icon">
                <el-icon>
                  <Document/>
                </el-icon>
              </div>
              <p>Upload and parse a resume to see JSON data</p>
            </div>
            <div v-if="taskStatus?.result" class="json-actions">
              <el-button type="primary" size="small" @click="copyJson">
                <el-icon>
                  <Document/>
                </el-icon>
                Copy JSON
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="showEditDialog" title="Edit Resume" width="80%" destroy-on-close>
      <resume-edit ref="resumeEditRef" :resume-data="resumeData"/>
      <template #footer>
        <el-button @click="showEditDialog = false">Cancel</el-button>
      </template>
    </el-dialog>

    <!-- 进入面试确认对话框 -->
    <el-dialog v-model="showInterviewDialog" title="Start AI Interview" width="450px" destroy-on-close>
      <div class="interview-dialog-content">
        <div class="interview-icon">
          <el-icon>
            <VideoCamera/>
          </el-icon>
        </div>
        <h3>准备开始AI模拟面试</h3>
        <p>系统将基于您的简历内容进行针对性提问，帮助您提前准备面试。</p>

        <div class="resume-summary">
          <h4>简历信息摘要</h4>
          <div class="summary-item">
            <span class="label">姓名</span>
            <span class="value">{{ resumeData?.name || '未填写' }}</span>
          </div>
          <div class="summary-item">
            <span class="label">目标岗位</span>
            <span class="value">{{ resumeData?.desiredPosition || '未填写' }}</span>
          </div>
          <div class="summary-item">
            <span class="label">工作年限</span>
            <span class="value">{{ resumeData?.experienceYears || '未填写' }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showInterviewDialog = false">Cancel</el-button>
        <el-button type="primary" :loading="isStartingInterview" @click="startInterview">
          <el-icon>
            <ArrowRight/>
          </el-icon>
          Start Interview
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed, onUnmounted, onMounted} from 'vue'
import {ElMessage, ElDialog} from 'element-plus'
import {
  Document,
  Clock,
  User,
  Message,
  Briefcase,
  Folder,
  Collection,
  VideoCamera,
  ArrowRight
} from '@element-plus/icons-vue'
import {submitResumeParseTask, getResumeParseTaskStatus} from '@/api/resume'
import {startInterview as startInterviewApi} from '@/api/interview'
import ResumeEdit from './resume-edit.vue'

// 响应式数据
const uploadRef = ref(null)
const selectedFile = ref(null)
const taskStatus = ref(null)
const isProcessing = ref(false)
const resumeData = ref(null)
const showEditDialog = ref(false)
const resumeEditRef = ref(null)
const activeTab = ref('structured')
const parseTime = ref(0)

// 面试相关
const showInterviewDialog = ref(false)
const isStartingInterview = ref(false)
const interviewSessionId = ref(null)

let pollingTimer = null
let parseTimer = null

// 格式化JSON
const formattedJson = computed(() => {
  if (taskStatus.value?.result) {
    return JSON.stringify(taskStatus.value.result, null, 2)
  }
  return '{}'
})

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

// 格式化时间
const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = (seconds % 60).toFixed(2)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(5, '0')}`
}

// 文件选择处理
const handleFileChange = (file) => {
  selectedFile.value = file.raw
  taskStatus.value = null
  resumeData.value = null
  parseTime.value = 0
  isProcessing.value = false
}

// 清除文件
const clearFile = () => {
  selectedFile.value = null
  taskStatus.value = null
  resumeData.value = null
  parseTime.value = 0
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
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
    ElMessage.error('Only PDF and image formats are supported!')
    return false
  }

  if (!isLt10M) {
    ElMessage.error('File size cannot exceed 10MB!')
    return false
  }

  return true
}

// 提交解析任务
const handleSubmitTask = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('Please select a file first')
    return
  }

  try {
    isProcessing.value = true
    parseTime.value = 0

    // 启动计时器
    parseTimer = setInterval(() => {
      parseTime.value += 0.01
    }, 10)

    const response = await submitResumeParseTask(selectedFile.value)
    if (response.data.code === 200 && response.data.data.taskId) {
      const taskId = response.data.data.taskId
      ElMessage.success('Task submitted, parsing...')

      startPolling(taskId)
    } else {
      ElMessage.error(response.data.message || 'Failed to submit task')
      stopParseTimer()
      isProcessing.value = false
    }
  } catch (error) {
    console.error('Failed to submit task:', error)
    ElMessage.error('Failed to submit task: ' + (error.message || 'Unknown error'))
    stopParseTimer()
    isProcessing.value = false
  }
}

// 停止解析计时器
const stopParseTimer = () => {
  if (parseTimer) {
    clearInterval(parseTimer)
    parseTimer = null
  }
}

// 开始轮询任务状态
const startPolling = (taskId) => {
  pollTaskStatus(taskId)

  pollingTimer = setInterval(() => {
    pollTaskStatus(taskId)
  }, 20000)
}

// 查询任务状态
const pollTaskStatus = async (taskId) => {
  try {
    const response = await getResumeParseTaskStatus(taskId)

    if (response.data.code === 200) {
      taskStatus.value = response.data.data

      if (response.data.data.status === 'COMPLETED' || response.data.data.status === 'FAILED') {
        stopPolling()
        stopParseTimer()
        isProcessing.value = false

        if (response.data.data.status === 'COMPLETED') {
          ElMessage.success('Resume parsed successfully!')
          resumeData.value = response.data.data.result
        } else {
          ElMessage.error('Failed to parse resume: ' + (response.data.data.errorMessage || 'Unknown error'))
        }
      }
    }
  } catch (error) {
    console.error('Failed to query task status:', error)
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
  const textMap = {
    'PENDING': 'Pending',
    'PROCESSING': 'Processing',
    'COMPLETED': 'Completed',
    'FAILED': 'Failed'
  }
  return textMap[status] || 'Unknown'
}

// 编辑简历
const handleEdit = () => {
  showEditDialog.value = true
  if (resumeEditRef.value && resumeData.value) {
    resumeEditRef.value.initForm(resumeData.value)
  }
}

// 复制JSON
const copyJson = () => {
  navigator.clipboard.writeText(formattedJson.value)
  ElMessage.success('Copied to clipboard')
}

// 开始AI面试
const startInterview = async () => {
  if (!resumeData.value) {
    ElMessage.error('请先解析简历')
    return
  }

  isStartingInterview.value = true

  try {
    const resumeContent = JSON.stringify(resumeData.value)

    // 使用封装的API
    const response = await startInterviewApi(resumeContent)

    if (response.data.code === 200) {
      interviewSessionId.value = response.data.data.id
      showInterviewDialog.value = false

      // 跳转到面试页面（需要创建面试页面或路由）
      ElMessage.success('面试会话已创建，即将进入面试环节...')

      // 这里可以跳转到面试页面或显示面试组件
      // 暂时用消息提示
      setTimeout(() => {
        ElMessage.info(`面试会话ID: ${interviewSessionId.value}`)
      }, 1000)
    } else {
      ElMessage.error(response.data.message || '创建面试会话失败')
    }
  } catch (error) {
    console.error('创建面试会话失败:', error)
    ElMessage.error('创建面试会话失败: ' + (error.message || 'Unknown error'))
  } finally {
    isStartingInterview.value = false
  }
}

// 取消任务
const handleCancel = () => {
  stopPolling()
  stopParseTimer()
  isProcessing.value = false
  taskStatus.value = null
  resumeData.value = null
  parseTime.value = 0
  ElMessage.info('Parsing cancelled')
}

// 组件卸载时清理
onUnmounted(() => {
  stopPolling()
  stopParseTimer()
})
</script>

<style scoped lang="scss">
.resume-parse-container {
  padding: 20px;
  height: calc(100vh - 140px);
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);

  .parse-layout {
    display: flex;
    gap: 20px;
    height: 100%;
  }

  .left-panel {
    width: 320px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    gap: 20px;
    overflow-y: auto;

    .upload-card {
      flex-shrink: 0;
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

      .card-header {
        font-weight: 600;
        font-size: 16px;
        color: #1a1a2e;
      }

      .upload-area {
        margin: 20px 0;
        border: 2px dashed #ddd;
        border-radius: 12px;
        background: #fff;
        transition: all 0.3s ease;

        &:hover {
          border-color: #667eea;
          background: #f8f9ff;
        }

        .upload-icon {
          margin-bottom: 10px;
          color: #667eea;
          font-size: 48px;
        }

        .el-upload__text {
          display: flex;
          flex-direction: column;
          gap: 5px;

          em {
            color: #667eea;
            font-weight: 600;
          }
        }
      }

      .file-info {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px;
        background: #f8f9fa;
        border-radius: 8px;
        margin-bottom: 15px;

        .file-icon {
          color: #667eea;
        }

        .file-name {
          flex: 1;
          font-size: 13px;
          color: #333;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .file-size {
          font-size: 12px;
          color: #999;
        }

        .remove-btn {
          color: #999;
          padding: 0;
        }
      }

      .timer-section {
        text-align: center;
        margin-bottom: 15px;
        padding: 15px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 10px;
        color: #fff;

        .timer-label {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;
          font-size: 14px;
          margin-bottom: 5px;
          opacity: 0.9;
        }

        .timer-value {
          font-size: 28px;
          font-weight: bold;
          font-family: 'Courier New', monospace;
        }
      }

      .task-status {
        margin-bottom: 15px;

        :deep(.el-progress) {
          width: 100%;
        }

        .status-info {
          margin-top: 15px;
          text-align: center;

          :deep(.el-tag) {
            min-width: 120px;
            padding: 10px 24px;
            font-size: 15px;
            font-weight: 600;
            display: inline-block;
          }

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
        .parse-btn {
          width: 100%;
          height: 48px;
          border-radius: 12px;
          font-weight: 600;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border: none;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
          }
        }

        .cancel-btn {
          width: 100%;
          height: 48px;
          border-radius: 12px;
          font-weight: 600;
        }

        .interview-btn {
          width: 100%;
          height: 48px;
          border-radius: 12px;
          font-weight: 600;
          margin-top: 10px;
          background: linear-gradient(135deg, #10b981 0%, #059669 100%);
          border: none;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
          }
        }
      }
    }

    .mini-preview-card {
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

      .card-header {
        font-weight: 600;
        font-size: 14px;
        color: #1a1a2e;
      }

      .mini-preview {
        padding: 15px;

        .preview-header {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 12px;

          .avatar-placeholder {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            font-size: 20px;
          }

          .preview-name {
            font-size: 16px;
            font-weight: 600;
            color: #1a1a2e;
          }
        }

        .preview-content {
          .preview-line {
            font-size: 13px;
            color: #666;
            margin: 4px 0;
          }
        }
      }
    }
  }

  .right-panel {
    flex: 1;
    overflow-y: auto;

    .result-card {
      border-radius: 16px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

      .card-header {
        padding: 0;
        border-bottom: none;
      }

      .result-tabs {
        .el-tabs__nav {
          margin: 0;
          padding: 0;

          .el-tabs__item {
            border-radius: 10px 10px 0 0;
            padding: 12px 24px;
            font-weight: 600;
            color: #666;
            background: #f5f5f5;
            border: none;
            margin-right: 8px;

            &.is-active {
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: #fff;
            }
          }
        }
      }

      .structured-content {
        padding: 20px;
      }

      .resume-content {
        .edit-button-container {
          display: flex;
          justify-content: flex-end;
          margin-bottom: 20px;

          .el-button {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            font-weight: 600;

            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            }
          }
        }

        .section-card {
          border-radius: 12px;
          padding: 20px;
          margin-bottom: 20px;
          position: relative;
          overflow: hidden;

          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 4px;
            height: 100%;
          }

          &.basic-card {
            background: linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%);

            &::before {
              background: #0ea5e9;
            }
          }

          &.eval-card {
            background: linear-gradient(135deg, #fce7f3 0%, #fbcfe8 100%);

            &::before {
              background: #ec4899;
            }
          }

          &.education-card {
            background: linear-gradient(135deg, #dcfce7 0%, #bbf7d0 100%);

            &::before {
              background: #22c55e;
            }
          }

          &.work-card {
            background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);

            &::before {
              background: #f59e0b;
            }
          }

          &.project-card {
            background: linear-gradient(135deg, #e0e7ff 0%, #c7d2fe 100%);

            &::before {
              background: #6366f1;
            }
          }

          &.skills-card {
            background: linear-gradient(135deg, #fce7f3 0%, #fbcfe8 100%);

            &::before {
              background: #ec4899;
            }
          }

          .section-header {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 16px;

            .section-icon {
              width: 36px;
              height: 36px;
              border-radius: 10px;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 18px;
              color: #fff;
            }

            .basic-icon {
              background: #0ea5e9;
            }

            .eval-icon {
              background: #ec4899;
            }

            .education-icon {
              background: #22c55e;
            }

            .work-icon {
              background: #f59e0b;
            }

            .project-icon {
              background: #6366f1;
            }

            .skills-icon {
              background: #ec4899;
            }

            .section-title {
              margin: 0;
              font-size: 16px;
              font-weight: 600;
              color: #1a1a2e;
            }
          }
        }

        .basic-grid {
          display: grid;
          grid-template-columns: repeat(4, 1fr);
          gap: 12px;

          .basic-item {
            background: rgba(255, 255, 255, 0.6);
            padding: 12px;
            border-radius: 8px;

            label {
              display: block;
              font-size: 11px;
              font-weight: 600;
              color: #666;
              text-transform: uppercase;
              margin-bottom: 4px;
            }

            span {
              font-size: 13px;
              color: #1a1a2e;
              font-weight: 500;
            }
          }
        }

        .eval-content {
          font-size: 14px;
          line-height: 1.8;
          color: #333;
          margin: 0;
          background: rgba(255, 255, 255, 0.6);
          padding: 15px;
          border-radius: 8px;
        }

        .timeline {
          .timeline-item {
            display: flex;
            gap: 15px;
            position: relative;
            padding-bottom: 20px;

            &:last-child {
              padding-bottom: 0;
            }

            .timeline-dot {
              width: 12px;
              height: 12px;
              border-radius: 50%;
              background: #22c55e;
              flex-shrink: 0;
              margin-top: 4px;
            }

            .timeline-content {
              flex: 1;
              background: rgba(255, 255, 255, 0.6);
              padding: 15px;
              border-radius: 10px;

              .timeline-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 8px;

                .timeline-title {
                  font-weight: 600;
                  color: #1a1a2e;
                }

                .timeline-date {
                  font-size: 12px;
                  color: #666;
                }
              }

              .timeline-body {
                .timeline-sub {
                  font-size: 13px;
                  color: #555;
                }
              }
            }
          }
        }

        .experience-list {
          .experience-card {
            background: rgba(255, 255, 255, 0.6);
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 12px;

            &:last-child {
              margin-bottom: 0;
            }

            .experience-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              margin-bottom: 5px;

              .experience-company {
                font-weight: 600;
                color: #1a1a2e;
              }

              .experience-date {
                font-size: 12px;
                color: #666;
              }
            }

            .experience-position {
              font-size: 14px;
              color: #f59e0b;
              font-weight: 500;
              margin-bottom: 8px;
            }

            .experience-desc {
              font-size: 13px;
              line-height: 1.6;
              color: #555;
            }
          }
        }

        .project-list {
          .project-card {
            background: rgba(255, 255, 255, 0.6);
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 12px;

            &:last-child {
              margin-bottom: 0;
            }

            .project-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              margin-bottom: 5px;

              .project-name {
                font-weight: 600;
                color: #1a1a2e;
              }

              .project-role {
                font-size: 12px;
                color: #6366f1;
                background: rgba(99, 102, 241, 0.1);
                padding: 3px 8px;
                border-radius: 4px;
              }
            }

            .project-date {
              font-size: 12px;
              color: #666;
              margin-bottom: 10px;
            }

            .project-tags {
              display: flex;
              flex-wrap: wrap;
              gap: 6px;
              margin-bottom: 10px;

              .tech-tag {
                background: rgba(99, 102, 241, 0.15);
                color: #6366f1;
                border: none;
                font-size: 12px;
              }
            }

            .project-desc {
              font-size: 13px;
              line-height: 1.6;
              color: #555;
            }
          }
        }

        .skills-grid {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 20px;

          .skills-column,
          .certificates-column {
            h3 {
              font-size: 14px;
              font-weight: 600;
              color: #1a1a2e;
              margin: 0 0 12px 0;
            }

            .skill-tags {
              display: flex;
              flex-wrap: wrap;
              gap: 8px;

              .skill-tag {
                background: rgba(236, 72, 153, 0.15);
                color: #ec4899;
                border: none;
                font-size: 12px;
              }

              .cert-tag {
                background: rgba(168, 85, 247, 0.15);
                color: #a855f7;
                border: none;
                font-size: 12px;
              }
            }
          }
        }
      }

      .empty-state {
        text-align: center;
        padding: 60px 0;

        .empty-icon {
          font-size: 48px;
          color: #ccc;
          margin-bottom: 15px;
        }

        p {
          color: #999;
          font-size: 14px;
        }
      }

      .json-content {
        padding: 20px;

        .json-code {
          background: #1e1e1e;
          border-radius: 10px;
          padding: 20px;
          max-height: 500px;
          overflow: auto;

          pre {
            margin: 0;
            color: #d4d4d4;
            font-family: 'Monaco', 'Menlo', monospace;
            font-size: 13px;
            line-height: 1.6;
            white-space: pre-wrap;
            word-wrap: break-word;
          }
        }

        .json-actions {
          margin-top: 15px;
          text-align: right;
        }
      }
    }
  }

  /* 面试确认对话框样式 */
  .interview-dialog-content {
    text-align: center;
    padding: 20px 0;

    .interview-icon {
      width: 80px;
      height: 80px;
      margin: 0 auto 20px;
      border-radius: 50%;
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 36px;
    }

    h3 {
      margin: 0 0 10px 0;
      font-size: 18px;
      font-weight: 600;
      color: #1a1a2e;
    }

    p {
      margin: 0 0 20px 0;
      color: #666;
      font-size: 14px;
    }

    .resume-summary {
      background: #f8f9fa;
      border-radius: 12px;
      padding: 15px;
      text-align: left;

      h4 {
        margin: 0 0 12px 0;
        font-size: 14px;
        font-weight: 600;
        color: #333;
      }

      .summary-item {
        display: flex;
        justify-content: space-between;
        padding: 8px 0;
        border-bottom: 1px solid #e9ecef;

        &:last-child {
          border-bottom: none;
        }

        .label {
          font-size: 13px;
          color: #999;
        }

        .value {
          font-size: 13px;
          font-weight: 500;
          color: #333;
        }
      }
    }
  }
}
</style>

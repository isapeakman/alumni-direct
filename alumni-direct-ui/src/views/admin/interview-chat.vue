<template>
  <div class="interview-chat-container">
    <!-- 顶部信息栏 -->
    <div class="interview-header">
      <div class="header-left">
        <el-icon class="header-icon">
          <VideoCamera/>
        </el-icon>
        <div class="header-info">
          <h2>AI模拟面试</h2>
          <p>会话ID: {{ sessionId }}</p>
        </div>
      </div>
      <div class="header-right">
        <el-tag v-if="sessionStatus === 'ACTIVE'" type="success" size="large">
          进行中
        </el-tag>
        <el-tag v-else-if="sessionStatus === 'ENDED'" type="info" size="large">
          已结束
        </el-tag>
        <el-button
            type="danger"
            size="large"
            @click="handleEndInterview"
            :loading="isEnding"
            :disabled="sessionStatus === 'ENDED'"
        >
          结束面试
        </el-button>
      </div>
    </div>

    <!-- 消息区域 -->
    <div class="chat-messages" ref="chatMessagesRef">
      <div v-for="(msg, index) in messages" :key="index" class="message-wrapper"
           :class="msg.role === 'assistant' ? 'message-ai' : 'message-user'">
        <div class="message-avatar" :class="msg.role === 'assistant' ? 'avatar-ai' : 'avatar-user'">
          <el-icon v-if="msg.role === 'assistant'">
            <VideoCamera/>
          </el-icon>
          <el-icon v-else>
            <User/>
          </el-icon>
        </div>
        <div class="message-content">
          <div class="message-role">
            {{ msg.role === 'assistant' ? 'AI面试官' : '我' }}
          </div>
          <div class="message-bubble">
            <p v-for="(line, lineIndex) in formatContent(msg.content)" :key="lineIndex">{{ line }}</p>
          </div>
          <div class="message-time">
            {{ formatTime(msg.createdAt) }}
          </div>
        </div>
      </div>

      <!-- AI思考中 -->
      <div v-if="isAiThinking" class="message-wrapper message-ai">
        <div class="message-avatar avatar-ai">
          <el-icon>
            <VideoCamera/>
          </el-icon>
        </div>
        <div class="message-content">
          <div class="message-role">AI面试官</div>
          <div class="message-bubble thinking-bubble">
            <span class="thinking-dots">正在思考</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-area" v-if="sessionStatus === 'ACTIVE'">
      <div class="input-wrapper">
        <el-input
            v-model="userInput"
            type="textarea"
            :rows="3"
            placeholder="请输入您的回答..."
            :disabled="isAiThinking || isSending"
            resize="none"
            @keydown.enter.ctrl="handleSend"
        />
      </div>
      <div class="input-actions">
        <span class="input-tip">按 Ctrl + Enter 发送</span>
        <el-button
            type="primary"
            size="large"
            :loading="isSending"
            :disabled="!userInput.trim() || isAiThinking"
            @click="handleSend"
        >
          <el-icon>
            <ArrowRight/>
          </el-icon>
          发送回答
        </el-button>
      </div>
    </div>

    <!-- 面试结束后的总结展示 -->
    <div v-if="interviewSummary" class="summary-panel">
      <el-card>
        <template #header>
          <div class="summary-header">
            <el-icon>
              <Document/>
            </el-icon>
            <span>面试总结</span>
          </div>
        </template>
        <div class="summary-content">
          <p v-for="(line, index) in formatContent(interviewSummary)" :key="index">{{ line }}</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, nextTick} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {VideoCamera, User, ArrowRight, Document} from '@element-plus/icons-vue'
import {
  getInterviewSession,
  sendInterviewMessage,
  endInterview
} from '@/api/interview'

const route = useRoute()
const router = useRouter()

// 从路由参数获取会话ID
const sessionId = ref(route.params.sessionId || localStorage.getItem('interviewSessionId'))
const sessionStatus = ref('ACTIVE')
const messages = ref([])
const userInput = ref('')
const isAiThinking = ref(false)
const isSending = ref(false)
const isEnding = ref(false)
const interviewSummary = ref(null)
const chatMessagesRef = ref(null)

// 格式化消息内容
const formatContent = (content) => {
  if (!content) return ['']
  return content.split('\n').filter(line => line.trim() !== '')
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  try {
    const date = new Date(timeStr)
    return date.toLocaleString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
      month: '2-digit',
      day: '2-digit'
    })
  } catch (e) {
    return timeStr
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
    }
  })
}

// 加载会话详情
const loadSession = async () => {
  if (!sessionId.value) {
    ElMessage.error('未找到面试会话')
    router.push('/admin/resume-async')
    return
  }

  try {
    const response = await getInterviewSession(sessionId.value)
    if (response.data.code === 200) {
      const session = response.data.data
      sessionStatus.value = session.status

      // 加载历史消息
      if (session.messages && session.messages.length > 0) {
        messages.value = session.messages.map(msg => ({
          role: msg.role,
          content: msg.content,
          createdAt: msg.createdAt
        }))
      } else if (session.currentQuestion) {
        // 如果没有历史消息但有当前问题，显示第一个问题
        messages.value = [{
          role: 'assistant',
          content: session.currentQuestion,
          createdAt: session.createdAt || new Date().toISOString()
        }]
      }

      scrollToBottom()
    } else {
      ElMessage.error(response.data.message || '加载会话失败')
    }
  } catch (error) {
    console.error('加载会话失败:', error)
    ElMessage.error('加载会话失败: ' + (error.message || 'Unknown error'))
  }
}

// 发送用户回答
const handleSend = async () => {
  if (!userInput.value.trim() || isAiThinking.value || isSending.value) {
    return
  }

  const answer = userInput.value.trim()
  userInput.value = ''
  isSending.value = true

  // 先显示用户的回答
  const userMsg = {
    role: 'user',
    content: answer,
    createdAt: new Date().toISOString()
  }
  messages.value.push(userMsg)
  scrollToBottom()

  // 显示AI思考状态
  isAiThinking.value = true

  try {
    const response = await sendInterviewMessage(sessionId.value, answer)

    if (response.data.code === 200) {
      const aiMsg = response.data.data
      messages.value.push({
        role: aiMsg.role,
        content: aiMsg.content,
        createdAt: aiMsg.createdAt || new Date().toISOString()
      })
      scrollToBottom()
    } else {
      ElMessage.error(response.data.message || '发送回答失败')
    }
  } catch (error) {
    console.error('发送回答失败:', error)
    ElMessage.error('发送回答失败: ' + (error.message || 'Unknown error'))
  } finally {
    isAiThinking.value = false
    isSending.value = false
  }
}

// 结束面试
const handleEndInterview = async () => {
  try {
    await ElMessageBox.confirm(
        '确定要结束本次面试吗？结束后系统将为您生成面试总结。',
        '确认结束面试',
        {
          confirmButtonText: '确定结束',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )
  } catch (e) {
    return // 用户取消
  }

  isEnding.value = true
  isAiThinking.value = true

  try {
    const response = await endInterview(sessionId.value)

    if (response.data.code === 200) {
      sessionStatus.value = 'ENDED'
      interviewSummary.value = response.data.data

      // 清空本地存储的会话ID
      localStorage.removeItem('interviewSessionId')

      ElMessage.success('面试已结束')
      scrollToBottom()
    } else {
      ElMessage.error(response.data.message || '结束面试失败')
    }
  } catch (error) {
    console.error('结束面试失败:', error)
    ElMessage.error('结束面试失败: ' + (error.message || 'Unknown error'))
  } finally {
    isEnding.value = false
    isAiThinking.value = false
  }
}

// 组件挂载时加载会话
onMounted(() => {
  // 检查是否有从创建面试时传来的初始数据
  const initialMsg = localStorage.getItem('interviewFirstQuestion')
  if (initialMsg && messages.value.length === 0) {
    try {
      const firstQuestion = JSON.parse(initialMsg)
      messages.value = [{
        role: 'assistant',
        content: firstQuestion,
        createdAt: new Date().toISOString()
      }]
      localStorage.removeItem('interviewFirstQuestion')
    } catch (e) {
      loadSession()
    }
  } else {
    loadSession()
  }
})
</script>

<style scoped lang="scss">
.interview-chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
  padding: 20px;

  .interview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
    padding: 20px 30px;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    margin-bottom: 20px;

    .header-left {
      display: flex;
      align-items: center;
      gap: 15px;

      .header-icon {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        color: #fff;
        font-size: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 10px;
      }

      .header-info {
        h2 {
          margin: 0;
          font-size: 20px;
          color: #1a1a2e;
        }

        p {
          margin: 4px 0 0 0;
          font-size: 13px;
          color: #999;
        }
      }
    }

    .header-right {
      display: flex;
      align-items: center;
      gap: 15px;
    }
  }

  .chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    margin-bottom: 20px;

    .message-wrapper {
      display: flex;
      gap: 12px;
      margin-bottom: 24px;

      &.message-ai {
        flex-direction: row;

        .message-avatar {
          background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        }

        .message-bubble {
          background: #f0fdf4;
          border: 1px solid #bbf7d0;
        }
      }

      &.message-user {
        flex-direction: row-reverse;

        .message-avatar {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        .message-content {
          text-align: right;
        }

        .message-bubble {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: #fff;

          p {
            color: #fff;
          }
        }

        .message-time {
          text-align: right;
        }
      }

      .message-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 18px;
        flex-shrink: 0;
      }

      .message-content {
        max-width: 70%;

        .message-role {
          font-size: 12px;
          color: #999;
          margin-bottom: 6px;
        }

        .message-bubble {
          padding: 14px 18px;
          border-radius: 12px;
          line-height: 1.8;

          p {
            margin: 0;
            color: #333;
            font-size: 14px;
          }

          &.thinking-bubble {
            background: #f0fdf4;
            border: 1px solid #bbf7d0;

            .thinking-dots {
              color: #10b981;
              font-weight: 500;
            }
          }
        }

        .message-time {
          font-size: 11px;
          color: #bbb;
          margin-top: 6px;
        }
      }
    }
  }

  .chat-input-area {
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    padding: 20px;

    .input-wrapper {
      :deep(.el-textarea__inner) {
        border-radius: 10px;
        border: 1px solid #ddd;
        font-size: 14px;
        resize: none;
      }
    }

    .input-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 12px;

      .input-tip {
        font-size: 12px;
        color: #999;
      }

      .el-button {
        height: 40px;
        border-radius: 8px;
        font-weight: 600;
      }
    }
  }

  .summary-panel {
    margin-top: 20px;

    .summary-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: #1a1a2e;
    }

    .summary-content {
      p {
        margin: 0 0 10px 0;
        line-height: 1.8;
        color: #333;
        font-size: 14px;
      }
    }

    :deep(.el-card) {
      border-radius: 12px;
      border: 1px solid #e5e7eb;
    }
  }
}
</style>

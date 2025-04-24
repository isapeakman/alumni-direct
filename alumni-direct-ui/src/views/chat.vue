<template>
  <div class="chat-container">
    <div class="sidebar">
      <div class="sidebar-header">
        <h3>会话列表</h3>
      </div>
      <ul class="conversation-list">
        <li
            v-for="conversation in conversations"
            :key="conversation.sessionId"
            :class="{ active: selectedConversation === conversation.sessionId }"
            @click="selectConversation(conversation.sessionId)"
        >
          <div>
            <span>{{ conversation.title }}</span>
            <div class="last-message" :title="conversation.lastMessage">
              {{ truncateMessage(conversation.lastMessage, 30) }}
            </div>
            <div class="last-time">
              {{ conversation.lastReceiveTime }}
            </div>
          </div>
        </li>
      </ul>
    </div>
    <div class="chat-window" v-if="selectedConversation">
      <div class="chat-header">
        <h3>{{ selectedConversationTitle }}</h3>
      </div>
      <div class="chat-messages" @scroll="onScroll">
        <div
            v-for="message in chatMessages"
            :key="message.id"
            :class="{ 'sent': message.sendUserId === userInfo.userId, 'received': message.sendUserId !== userInfo.userId }"
        >
          <div v-if="message.sendUserId===userInfo.userId" class="message-header">
            <img :src="avatar" alt="Avatar" class="message-avatar"/>
            <span class="message-username">我</span>
          </div>
          <div v-else class="message-header">
            <img :src="selectedContactAvatar" alt="Avatar" class="message-avatar"/>
            <span class="message-username">{{ selectedContactName }}</span>
          </div>

          <!-- 文本消息 -->
          <div
              v-if="message.messageType === MESSAGE_TYPE.TEXT || !message.messageType"
              class="message-content text-message"
          >
            {{ message.messageContent }}
          </div>

          <!-- 文件消息 -->
          <div
              v-else-if="message.messageType === MESSAGE_TYPE.FILE"
              class="file-card-message"
              :class="{ 'sent': message.sendUserId === userInfo.userId }"
          >
            <div class="file-card" @click="downloadFile(message)">
              <div class="file-icon">
                <el-icon :size="28">
                  <component :is="Document"/>
                </el-icon>
              </div>
              <div class="file-details">
                <div class="file-name">{{ message.fileName }}</div>
                <div class="file-meta">
                  <span class="file-size">{{ formatFileSize(message.fileSize) }}</span>
                  <span class="file-download">
          <el-icon><Download/></el-icon>
        </span>
                </div>
              </div>
            </div>
            <div class="message-time">{{ (message.sendTime) }}</div>
          </div>
        </div>
      </div>
      <!-- 新增投递简历按钮 -->
      <div class="resume-button">
        <el-button v-if="isJobSeekerChat" type="success" @click="submitResume">投递简历</el-button>
      </div>
      <div class="chat-input">
        <el-input
            v-model="sendMessage.messageContent"
            placeholder="输入消息..."
            @keyup.enter="submit"
        ></el-input>
        <el-button type="primary" @click="submit">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref, watch, nextTick, computed} from 'vue';
import {useRoute} from 'vue-router';
import {ElMessage} from 'element-plus';
import {getSessionHistory, getSessionList} from "@/api/session.js";
import {getUserInfo} from "@/api/user.js";
import {uploadFile, uploadResume} from "@/api/file.js";
import {Document, Download, Picture, Folder} from '@element-plus/icons-vue';

const route = useRoute();

const webSocket = ref();
const isOnline = ref(true);
const userId = ref(route.query.userId || '');
const toId = ref('');
const sendMessage = ref({
  toId: '',
  messageContent: ''
});
const chatMessages = ref([]);
const conversations = ref([]);
const selectedConversation = ref('');
const selectedConversationTitle = ref('');
const selectedContactId = ref(null);
const selectedContactName = ref('');
const selectedContactAvatar = ref('');
const userInfoMap = ref({}); // 存储用户信息
const userInfo = ref(null);
const avatar = ref('')
// 消息类型定义
const MESSAGE_TYPE = {
  TEXT: 0,    // 文本消息
  FILE: 1     // 文件消息
}
const getFileIcon = (filename) => {
  const extension = filename.split('.').pop().toLowerCase();
  console.log('文件扩展名:', extension);
  switch (extension) {
    case 'pdf':
      return 'Document';
    case 'doc':
    case 'docx':
      return 'Document';
    case 'xls':
    case 'xlsx':
      return 'Document';
    case 'ppt':
    case 'pptx':
      return 'Document';
    case 'zip':
    case 'rar':
      return 'Folder';
    case 'jpg':
    case 'png':
    case 'gif':
      return 'Picture';
    default:
      return 'Document';
  }
};
// 定义一个计算属性来判断是否显示投递简历按钮
const isJobSeekerChat = computed(() => route.path === '/chat');

// 获取路由参数
const createId = route.query.createId;
const title = route.query.title;
const recruiterName = route.query.recruiterName;
const jobId = route.query.jobId; // 添加 jobId 参数
const currentJobId = ref(jobId); // 使用 ref 来存储 jobId
console.log('createId:', createId);
console.log('title:', title);

onMounted(() => {
  init();
  fetchConversations();
});
// 简历投递方法
const submitResume = async () => {
  try {
    // 1. 创建文件输入元素
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.pdf,.doc,.docx';

    input.onchange = async (e) => {
      const file = e.target.files[0];
      if (!file) return;

      // 2. 上传文件获取路径
      const formData = new FormData();
      formData.append('file', file);

      const uploadRes = await uploadFile(formData);
      const filePath = uploadRes.data.data;
      const fileName = file.name;

      // 3. 构造文件消息
      const fileMessage = {
        toId: selectedContactId.value,
        messageType: MESSAGE_TYPE.FILE,
        messageContent: filePath,
        fileName: fileName,
        fileSize: file.size,
      };

      // 4. 通过WS发送文件消息
      webSocket.value.send(JSON.stringify(fileMessage));

      // 5. 本地立即显示（乐观更新）
      chatMessages.value.push({
        ...fileMessage,
        sendUserId: userInfo.value.userId,
        id: `temp_${Date.now()}`
      });

      scrollToBottom();
    };

    input.click();
  } catch (error) {
    ElMessage.error('简历上传失败: ' + error.message);
  }
};
// 文件大小格式化
const formatFileSize = (bytes) => {
  if (!bytes) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 文件下载方法
const downloadFile = async (message) => {
  try {
    // 1. 获取文件Blob
    console.log('下载文件路径:', message.messageContent);
    const response = await fetch(message.messageContent);
    const blob = await response.blob();

    // 2. 创建下载链接
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;

    // 3. 强制设置正确的文件名和扩展名
    link.download = message.fileName || `resume_${Date.now()}.pdf`;

    // 4. 触发下载
    document.body.appendChild(link);
    link.click();

    // 5. 清理
    setTimeout(() => {
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    }, 100);
  } catch (error) {
    console.error('下载失败:', error);
    ElMessage.error('文件下载失败');
  }
};
const init = () => {
  const token = localStorage.getItem('token');
  webSocket.value = new WebSocket(`ws://localhost:8080/ad/chat?token=${token}`);

  const timeoutId = setTimeout(() => {
    if (webSocket.value.readyState === WebSocket.CONNECTING) {
      webSocket.value.close();
      console.error('WebSocket 连接超时');
      retryConnection();
    }
  }, 10000);

  webSocket.value.onopen = () => {
    clearTimeout(timeoutId);
    isOnline.value = true;
  };

  webSocket.value.onmessage = onMessage;
  webSocket.value.onclose = onClose;

  // 获取当前用户信息
  fetchUserInfo(userId.value);
};

const onOpen = () => {
  isOnline.value = true;
};

const onClose = () => {
  isOnline.value = false;
};

const onMessage = (event) => {
  const response = JSON.parse(event.data);
  console.log('服务端推送过来的消息：', response);

  if (response.system) {
    // 系统消息处理
    friendsList.value = response.message.filter(name => name !== username.value);
    systemMessages.value = [...friendsList.value];
  } else {
    console.log('来自用户ID:', response.fromId);
    console.log('当前会话的联系人ID:', selectedContactId.value);

    // 获取发送者信息
    fetchUserInfo(response.fromId);

    // 添加头像和用户名
    const senderInfo = userInfoMap.value[response.fromId] || {};
    response.avatar = senderInfo.avatar || '';
    response.username = senderInfo.username || '';

    if (response.fromId === selectedContactId.value) {
      chatMessages.value.push(response);
    } else {
      const conversation = conversations.value.find(conv => conv.sessionId === response.sessionId);
      if (conversation) {
        conversation.unreadCount++;
      }
    }
  }
};

// 获取会话列表
const fetchConversations = async () => {
  try {
    const response = await getSessionList();
    conversations.value = response.data.data.map(conv => ({
      sessionId: conv.sessionId,
      title: conv.contactName,
      unreadCount: 0,
      contactId: conv.contactId,
      avatar: conv.contactAvatar,
      lastMessage: conv.lastMessage,
      lastReceiveTime: conv.lastReceiveTime,
    }));

    // 检查是否需要添加临时会话
    if (createId && title && recruiterName) {
      const exists = conversations.value.some(conv => conv.contactId == createId);
      if (!exists) { // 历史会话不存在则添加
        const tempConversation = {
          sessionId: `temp_${createId}`,
          title: recruiterName,
          contactId: createId,
          isTemp: true,
          avatar: '', // 可以添加默认头像
          unreadCount: 0
        };
        conversations.value.unshift(tempConversation);
        // 选中该临时会话
        selectConversation(tempConversation.sessionId);
      } else {
        // 如果会话已存在，找到该会话并置顶
        const existingConversation = conversations.value.find(conv => conv.contactId == createId);
        if (existingConversation) {
          conversations.value = conversations.value.filter(conv => conv.sessionId !== existingConversation.sessionId);
          conversations.value.unshift(existingConversation);
          // 选中该会话
          selectConversation(existingConversation.sessionId);
        }
      }
    }

    // 自动选择第一个会话（优先选择临时会话）
    if (conversations.value.length > 0 && !selectedConversation.value) {
      selectConversation(conversations.value[0].sessionId);
    }
  } catch (error) {
    ElMessage.error('获取会话列表失败:', error);
  }
};

const lastMessageTime = ref(null);
const isFetching = ref(false);
const fetchMessages = async (sessionId, historyTime = null) => {
  if (isFetching.value) return;
  isFetching.value = true;

  try {
    const response = await getSessionHistory(sessionId, historyTime);
    const messages = response.data.data;
    console.log('历史消息列表:', messages);
    if (messages.length > 0) {
      lastMessageTime.value = messages[messages.length - 1].sendTime;

      // 如果是加载历史消息（滚动到顶部时）
      if (historyTime) {
        const previousScrollHeight = document.querySelector('.chat-messages')?.scrollHeight || 0;
        chatMessages.value = [...messages, ...chatMessages.value];

        await nextTick();
        const chatMessagesEl = document.querySelector('.chat-messages');
        if (chatMessagesEl) {
          chatMessagesEl.scrollTop = chatMessagesEl.scrollHeight - previousScrollHeight;
        }
      }
      // 如果是首次加载或切换会话
      else {
        chatMessages.value = messages;

        await nextTick();
        scrollToBottom();
      }
    }
  } catch (error) {
    console.error('获取消息失败:', error);
  } finally {
    isFetching.value = false;
  }
};

const onScroll = async (event) => {
  const {scrollTop} = event.target;
  if (scrollTop === 0 && !isFetching.value) {
    await fetchMessages(selectedConversation.value, lastMessageTime.value);
  }
};
const truncateMessage = (message, maxLength) => {
  if (!message) return '';
  if (message.length <= maxLength) return message;
  return message.substring(0, maxLength) + '...';
};

// 获取会话历史消息
// const fetchMessages = async (sessionId) => {
//   try {
//     const response = await getSessionHistory(sessionId);
//     chatMessages.value = response.data.data;
//     console.log("历史会话消息列表", chatMessages.value);
//   } catch (error) {
//     console.error('获取消息失败:', error);
//   }
// };
const count = ref(0)
// 选择会话
const selectConversation = (sessionId) => {
  // 1. 清空当前聊天消息
  chatMessages.value = [];

  console.log('选择的会话ID:', sessionId);
  selectedConversation.value = sessionId;
  const conversation = conversations.value.find(conv => conv.sessionId === sessionId);
  console.log('当前会话的联系人ID:', conversation.contactId);
  console.log('当前会话的联系人头像:', conversation.avatar);
  selectedContactId.value = conversation.contactId;
  selectedContactAvatar.value = conversation.avatar;
  if (conversation) {
    selectedConversationTitle.value = conversation.title;
    conversation.unreadCount = 0;
  }
  fetchMessages(sessionId);
  sendMessage.value.toId = conversation.contactId;
  // 重置 jobId
  if (count.value !== 0) {
    currentJobId.value = null;
  }
  console.log(count.value)
  count.value++;
};

// 发送消息
const submit = () => {
  const content = sendMessage.value.messageContent.trim();
  if (!content) {
    ElMessage.error('请输入聊天内容');
    return;
  }
  const message = {
    toId: selectedContactId.value,
    messageContent: content,
    sendUserId: userInfo.value.userId,
    // jobId: jobId, // 添加 jobId 参数
    sendTime: new Date().toLocaleTimeString(),
    avatar: userInfoMap.value[userId.value]?.avatar || '',
    username: userInfoMap.value[userId.value]?.username || ''
  };
  if (currentJobId.value) {
    message.jobId = currentJobId.value; // 只有在 currentJobId 不为空时才添加 jobId
  }


  chatMessages.value.push(message);
  webSocket.value.send(JSON.stringify(message));
  sendMessage.value.messageContent = '';
};

// 获取用户信息
const fetchUserInfo = async (userId) => {
  if (userInfoMap.value[userId]) return;

  try {
    const response = await getUserInfo(userId);
    if (response.data.code === 200) {
      userInfo.value = response.data.data;
      avatar.value = userInfo.value.userAvatar;
      const userData = response.data.data;
      userInfoMap.value[userId] = {
        avatar: userData.userAvatar || '',
        username: userData.nickname || ''
      };
      console.log('存储的用户信息:', userInfoMap.value[userId]);
    } else {
      console.error('获取用户信息失败，响应码:', response.code);
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};

// 重试连接
const retryConnection = () => {
  console.warn('尝试重新连接 WebSocket...');
  setTimeout(init, 5000); // 5秒后重试
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    const chatMessagesEl = document.querySelector('.chat-messages');
    if (chatMessagesEl) {
      chatMessagesEl.scrollTop = chatMessagesEl.scrollHeight;
    }
  });
};

watch(chatMessages, scrollToBottom, {deep: true});
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 90vh; /* Adjust the height as needed */
  width: 80vw; /* Adjust the width as needed */
  margin: auto; /* Center the chat container */
  font-family: 'Arial', sans-serif; /* Modern font */
  background-color: #f0f2f5; /* Light background color */
}

.sidebar {
  width: 250px;
  background-color: #ffffff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Subtle shadow */
  border-radius: 8px; /* Rounded corners */
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  background-color: #f9fafb; /* Slightly different background for header */
}

.conversation-list {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
  overflow-y: auto;
}

.conversation-list li {
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #e4e7ed;
  transition: background-color 0.3s; /* Smooth transition */
}

.conversation-list li:hover {
  background-color: #f0f2f5; /* Light hover background */
}

.conversation-list li.active {
  background-color: #ecf5ff;
  border-left: 4px solid #409EFF; /* Active session indicator */
  padding-left: 12px; /* Adjust padding to account for border */
}

.conversation-list li .last-message {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 4px;
}

.conversation-list li .last-time {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  margin-left: 16px; /* Space between sidebar and chat window */
  border-radius: 8px; /* Rounded corners */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* Subtle shadow */
}

.chat-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
  background-color: #f9fafb; /* Slightly different background for header */
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #ffffff; /* White background for messages */
}

.chat-messages .message-header {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.chat-messages .message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 8px;
}

.chat-messages .message-username {
  font-weight: bold;
  color: #303133;
}

.chat-messages .message-content {
  max-width: 70%;
  padding: 8px 12px;
  margin-bottom: 8px;
  border-radius: 16px;
  background-color: #e4e7ed;
  word-wrap: break-word; /* Ensure long words break */
}

.chat-messages .sent {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.chat-messages .sent .message-content {
  background-color: #409EFF;
  color: #ffffff;
  text-align: right;
}

.chat-messages .received .message-content {
  align-self: flex-start;
}

.chat-messages .message-time {
  font-size: 12px;
  color: #909399;
  text-align: right;
}

.chat-input {
  display: flex;
  padding: 16px;
  border-top: 1px solid #e4e7ed;
  background-color: #ffffff; /* White background for input area */
}

.chat-input .el-input {
  flex: 1;
  margin-right: 8px;
}

.resume-button {
  padding: 16px;
  background-color: #ffffff; /* White background for resume button */
}

.resume-button .el-button {
  width: 100%;
}

/* 文件消息样式 */
.chat-messages .file-message {
  display: flex;
  align-items: center;
  background-color: #f0f9eb; /* Light green background */
  border: 1px solid #c2e7b0; /* Green border */
  border-radius: 8px; /* Rounded corners */
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s; /* Smooth transition */
}

/* 文件卡片消息容器 */
.file-card-message {
  max-width: 280px;
  margin: 8px 0;

  &.sent {
    margin-left: auto;

    .file-card {
      background: #409EFF;
      color: white;

      .file-meta {
        color: rgba(255, 255, 255, 0.8);
      }
    }
  }
}

/* 文件卡片 */
.file-card {
  display: flex;
  border-radius: 8px;
  background: #f5f7fa;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e4e7ed;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .file-download {
      opacity: 1;
    }
  }
}

.file-icon {
  margin-right: 12px;
  color: #409EFF;
}

.file-details {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.file-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.file-download {
  opacity: 0.5;
  transition: opacity 0.2s;
}

.message-time {
  font-size: 12px;
  color: #909399;
  text-align: right;
  margin-top: 4px;
}

/* 发送方样式 */
.sent .file-card {
  background: #409EFF;
  color: white;

  .file-icon {
    color: white;
  }

  .file-meta {
    color: rgba(255, 255, 255, 0.8);
  }
}
</style>

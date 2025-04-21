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
          {{ conversation.title }}
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
          <div class="message-content">
            {{ message.messageContent }}
          </div>
          <div class="message-time">
            {{ message.sendTime }}
          </div>
        </div>
      </div>
      <!-- 新增投递简历按钮 -->
      <div class="resume-button">
        <el-button type="success" @click="submitResume">投递简历</el-button>
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
import {onMounted, ref, watch, nextTick} from 'vue';
import {useRoute} from 'vue-router';
import {ElMessage} from 'element-plus';
import {getSessionHistory, getSessionList} from "@/api/session.js";
import {getUserInfo} from "@/api/user.js"; // 假设有一个 API 来获取用户信息

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

// 获取路由参数
const createId = route.query.createId;
const title = route.query.title;

console.log('createId:', createId);
console.log('title:', title);

onMounted(() => {
  init();
  fetchConversations();
});

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
      avatar: conv.contactAvatar
    }));
    console.log("该用户拥有的会话列表", conversations.value);
    // 选择第一个会话
    if (conversations.value.length > 0) {
      selectedConversation.value = conversations.value[0].sessionId;
      selectedConversationTitle.value = conversations.value[0].title;
      selectedContactId.value = conversations.value[0].contactId;
      selectedContactName.value = conversations.value[0].title;
      selectedContactAvatar.value = conversations.value[0].avatar;
      fetchMessages(conversations.value[0].sessionId);
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
    if (messages.length > 0) {
      lastMessageTime.value = messages[messages.length - 1].sendTime;
      const previousScrollHeight = document.querySelector('.chat-messages').scrollHeight;
      chatMessages.value = [...messages, ...chatMessages.value];
      nextTick(() => {
        const chatMessagesEl = document.querySelector('.chat-messages');
        chatMessagesEl.scrollTop = chatMessagesEl.scrollHeight - previousScrollHeight;
      });
    }
  } catch (error) {
    console.error('Failed to fetch messages:', error);
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

// 选择会话
const selectConversation = (sessionId) => {
  console.log('选择的会话ID:', sessionId);
  selectedConversation.value = sessionId;
  const conversation = conversations.value.find(conv => conv.sessionId === sessionId);
  console.log('当前会话的联系人ID:', conversation.contactId);
  selectedContactId.value = conversation.contactId;
  if (conversation) {
    selectedConversationTitle.value = conversation.title;
    conversation.unreadCount = 0;
  }
  fetchMessages(sessionId);
  sendMessage.value.toId = conversation.contactId;
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
    sendTime: new Date().toLocaleTimeString(),
    avatar: userInfoMap.value[userId.value]?.avatar || '',
    username: userInfoMap.value[userId.value]?.username || ''
  };

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
}

.sidebar {
  width: 250px;
  background-color: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
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
}

.conversation-list li.active {
  background-color: #ecf5ff;
}

.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
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
}

.chat-input .el-input {
  flex: 1;
  margin-right: 8px;
}
</style>

<template>

</template>

<script setup>
import {onMounted, ref} from 'vue'
import {ElMessage} from "element-plus";

const webSocket = ref()

const isShowChat = ref(false)
const chatMes = ref(false)
const isOnline = ref(true)
const username = ref('')
const toName = ref('')
const sendMessage = ref({
  toName: '',
  message: ''
})
const historyMessage = ref([])
const friendsList = ref([])
const systemMessages = ref([])

onMounted(() => {
  init()
})

const init = async () => {
  // 创建 WebSocket 对象
  webSocket.value = new WebSocket('ws://localhost:8080/ad/chat')

  webSocket.value.onopen = onOpen

  // 接收到服务端推送的消息后触发
  webSocket.value.onmessage = onMessage

  webSocket.value.onclose = onClose
}

const onOpen = () => {
  isOnline.value = true
}

const onClose = () => {
  isOnline.value = false
}

const onMessage = (event) => {
  // 获取服务端推送过来的消息
  let dataString = event.data
  // 将 dataString 转换为 json 对象
  let response = JSON.parse(dataString)

  console.log('服务端推送过来的消息：', response)

  // 判断是否是系统消息
  if (response.system) {
    // 系统消息 好友列表展示
    let names = response.message
    friendsList.value = []
    systemMessages.value = []
    for (let i = 0; i < names.length; i++) {
      if (names[i] !== username.value) {
        friendsList.value.push(names[i])
        systemMessages.value.push(names[i])
      }
    }
  } else {
    // 非系统消息
    let history = sessionStorage.getItem(response.fromName);
    if (!history) {
      historyMessage.value = []
    }
    historyMessage.value.push(response)
    sessionStorage.setItem(response.fromName, JSON.stringify(historyMessage.value))
  }
}

const showChat = (name) => {
  toName.value = name

  // 清除聊天区的数据
  let history = sessionStorage.getItem(toName.value)
  if (!history) {
    historyMessage.value = []
  } else {
    historyMessage.value = JSON.parse(history)
  }

  // 展示聊天对话框
  isShowChat.value = true

  // 显示正在和谁聊天
  chatMes.value = true
}

const submit = () => {
  sendMessage.value.message = sendMessage.value.message.trim()

  if (!sendMessage.value.message) {
    ElMessage.error('请输入聊天内容')
    return
  }

  sendMessage.value.toName = toName.value

  historyMessage.value.push(JSON.parse(JSON.stringify(sendMessage.value)))
  sessionStorage.setItem(toName.value, JSON.stringify(historyMessage.value))

  webSocket.value.send(JSON.stringify(sendMessage.value));
  sendMessage.value.message = ''
}
</script>

<style scoped>

.wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea, #764ba2) repeat;
}

.guest-name {
  margin-left: 1%;
  font-size: 12px;
}

.hostname {
  font-size: 12px;
  margin-bottom: -2%;
  text-align: right;
}
</style>

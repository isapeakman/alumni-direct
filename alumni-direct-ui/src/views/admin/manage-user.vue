<template>
  <div class="user-management">
    <div class="header">
      <!--      <el-button type="primary" @click="addUser">添加用户</el-button>-->
      <el-input
          v-model="searchQuery"
          placeholder="搜索用户"
          class="search-input"
          @input="filterUsers"
      />
    </div>

    <el-table :data="paginatedUsers" style="width: 100%">
      <el-table-column prop="userAvatar" label="头像" width="100">
        <template #default="{ row }">
          <img :src="row.userAvatar" alt="头像" class="user-avatar"/>
        </template>
      </el-table-column>
      <el-table-column prop="userId" label="用户ID" width="180"></el-table-column>
      <el-table-column prop="userAccount" label="用户账号" width="180"></el-table-column>
      <el-table-column prop="nickname" label="昵称" width="180"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="row.status===0" type="danger" @click="banUser(row,1)">禁用</el-button>
          <el-button v-else type="success" @click="banUser(row,0)">启用</el-button>
          <el-button type="primary" @click="viewUserInfo(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalUsers"
        @current-change="handlePageChange"
        layout="total, prev, pager, next, jumper"
    />
    <el-dialog v-model="dialogVisible" title="用户信息">
      <div v-if="selectedUser">
        <p>用户ID: {{ selectedUser.userId }}</p>
        <p>用户账号: {{ selectedUser.userAccount }}</p>
        <p>昵称: {{ selectedUser.nickname }}</p>
        <p>头像: <img :src="selectedUser.userAvatar" alt="头像" class="user-avatar"/></p>
        <p>角色: {{ selectedUser.role === 0 ? '管理员' : '用户' }}</p>
        <p>性别: {{ selectedUser.gender }}</p>
        <p>出生日期: {{ selectedUser.birth }}</p>
        <p>简介: {{ selectedUser.introduction }}</p>
        <p>创建时间: {{ selectedUser.createTime }}</p>
        <p>是否删除: {{ selectedUser.isDelete === 0 ? '未删除' : '已删除' }}</p>
        <p>最后登录时间: {{ selectedUser.lastLoginTime }}</p>
        <p>是否校友: {{ selectedUser.isAlumni === 0 ? '否' : '是' }}</p>
        <p>状态: {{ selectedUser.status === 0 ? '未禁用' : '已禁用' }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="system-user">
import {ref, reactive, computed, onMounted} from 'vue';
import {ElMessage} from 'element-plus';

import {disableUser, fetchUserData} from '../../api/user.js';

const users = ref([])
const searchQuery = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const totalUsers = ref(0)
const selectedUser = ref(null)
const dialogVisible = ref(false)
const fetchUsers = async () => {
  try {
    const response = await fetchUserData(currentPage.value, pageSize.value, searchQuery.value)
    if (response.data.code === 200) {
      users.value = response.data.data.records
      totalUsers.value = response.data.data.page.total
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    console.error('获取用户列表失败：', error)
    ElMessage.error('获取用户列表失败')
  }
}

const paginatedUsers = computed(() => {
  return users.value
})

const filterUsers = () => {
  currentPage.value = 1 // 重置到第一页
  fetchUsers()
}
const viewUserInfo = (user) => {
  selectedUser.value = user
  dialogVisible.value = true
}
const handlePageChange = (page) => {
  currentPage.value = page
  fetchUsers()
}

const addUser = () => {
  // 添加用户逻辑
}

const editUser = (user) => {
  // 编辑用户逻辑
}

const banUser = async (user, status) => {
  console.log('禁用用户:', user)
  // 禁用用户逻辑
  const response = await disableUser(user.userId, status);
  if (response.data.code === 200) {
    ElMessage.success('禁用成功')
    fetchUsers() // 刷新用户列表
  } else {
    ElMessage.error(response.data.message)
  }
}


onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
</style>
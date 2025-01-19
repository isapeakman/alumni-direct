<template>
  <div class="navbar">
    <div class="left-menu">
      <el-button type="text" :icon="Fold" @click="toggleSidebar"/>
    </div>

    <div class="right-menu">
      <el-dropdown trigger="click">
        <div class="avatar-wrapper">
          <el-avatar :size="30" src="https://placeholder.com/30"/>
          <span class="user-name">管理员</span>
          <el-icon>
            <CaretBottom/>
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>个人信息</el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import {Fold, CaretBottom} from '@element-plus/icons-vue'

const toggleSidebar = () => {
  // 控制侧边栏折叠的方法，后续实现
}

const handleLogout = () => {
  // 退出登录的方法，后续实现
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;

  .left-menu {
    display: flex;
    align-items: center;
  }

  .right-menu {
    .avatar-wrapper {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 0 8px;

      .user-name {
        margin: 0 8px;
        font-size: 14px;
        color: #333;
      }
    }
  }
}
</style>

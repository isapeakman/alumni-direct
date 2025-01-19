<template>
  <div class="sidebar">
    <div class="logo-container">
      <router-link to="/">
        <h1 class="logo-title">招聘管理系统</h1>
      </router-link>
    </div>

    <el-scrollbar>
      <el-menu
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          :unique-opened="true"
          :collapse-transition="false"
          mode="vertical"
      >
        <el-menu-item index="/dashboard">
          <el-icon>
            <Monitor/>
          </el-icon>
          <template #title>控制台</template>
        </el-menu-item>

        <el-sub-menu index="1">
          <template #title>
            <el-icon>
              <Document/>
            </el-icon>
            <span>职位管理</span>
          </template>
          <el-menu-item index="/jobs/list">职位列表</el-menu-item>
          <el-menu-item index="/jobs/create">发布职位</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="2">
          <template #title>
            <el-icon>
              <User/>
            </el-icon>
            <span>简历管理</span>
          </template>
          <el-menu-item index="/resumes/new">新简历</el-menu-item>
          <el-menu-item index="/resumes/processing">处理中</el-menu-item>
          <el-menu-item index="/resumes/archived">已归档</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/statistics">
          <el-icon>
            <PieChart/>
          </el-icon>
          <template #title>数据统计</template>
        </el-menu-item>

        <el-menu-item index="/settings">
          <el-icon>
            <Setting/>
          </el-icon>
          <template #title>系统设置</template>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {useRoute} from 'vue-router'
import {
  Monitor,
  Document,
  User,
  PieChart,
  Setting
} from '@element-plus/icons-vue'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  background-color: #304156;

  .logo-container {
    height: 50px;
    padding: 10px 0;
    text-align: center;

    .logo-title {
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      margin: 0;
      white-space: nowrap;
    }
  }

  :deep(.el-menu) {
    border-right: none;
  }

  :deep(.el-sub-menu__title) {
    &:hover {
      background-color: #263445 !important;
    }
  }

  :deep(.el-menu-item) {
    &:hover {
      background-color: #263445 !important;
    }

    &.is-active {
      background-color: #263445 !important;
    }
  }
}
</style>

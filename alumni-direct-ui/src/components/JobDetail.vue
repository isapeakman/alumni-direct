<template>
  <div class="job-details-container" v-if="selectedJob">
    <div class="job-details">
      <!-- 标题与薪资 -->
      <div class="job-title-row">
        <div class="job-title">{{ selectedJob.title }}</div>
        <div class="job-salary">{{ formatSalary(selectedJob.minSalary, selectedJob.maxSalary) }}</div>
        <el-button type="primary" plain @click="goChat(selectedJob.createId,selectedJob.title)" class="apply-button">
          发起聊天
        </el-button>
      </div>

      <!-- 职位类型标签 -->
      <el-tag type="info" size="small" class="job-type-tag">
        {{ getJobType(selectedJob.jobType) }}
      </el-tag>

      <!-- 发布时间 -->
      <div class="publish-time">
        发布时间：{{ selectedJob.publishTime }}
      </div>

      <!-- 职位详情 -->
      <h3 class="section-title">职位详情</h3>
      <div class="job-desc">
        <!-- 根据\n分段显示 -->
        <p v-for="(line, index) in jobDescLines" :key="index" class="desc-line">
          {{ line }}
        </p>
      </div>

      <!-- 其他信息 -->
      <p class="location">工作地点：
        <el-icon>
          <Location/>
        </el-icon>
        {{ selectedJob.location }}
      </p>
      <div class="divider-section">
        <el-divider></el-divider>
        <el-avatar :size="30" :src="selectedJob.recruiterAvatar"/>
        招聘者：{{ selectedJob.recruiterName }}
      </div>
      <p class="company-name">公司名称：{{ selectedJob.companyName }}</p>
    </div>
  </div>
</template>

<script setup>
// 定义props 使值传进组件
import {computed} from "vue";
import {Location} from "@element-plus/icons-vue";
import router from "@/router/index.js";

const props = defineProps({
  selectedJob: {
    type: Object,
    required: true
  }
})
const goChat = (createId, title) => {
  console.log("创建者是", createId)
  router.push({
    path: "/wsTest",
    query: {
      createId: createId,
      title: title
    }
  })
}
// 工具函数
const formatSalary = (min, max) => {
  if (!min && !max) return '薪资面议'
  if (!max) return `${min}k以上`
  return `${min}-${max}k`
}
// 处理职位详情换行
const jobDescLines = computed(() => {
  return props.selectedJob.jobDesc
      .split('\n')
      .filter(line => line.trim() !== '') // 过滤空行
      .map(line => line.trim()); // 去除多余空格
});
const getJobType = (type) => {
  const typeMap = {
    1: '全职',
    2: '实习',
    3: '兼职'
  }
  return typeMap[type] || '未知'
}
</script>

<style scoped lang="scss">
.job-details {
  padding: 20px;
  border: 1px #c4c8d4;
  background-color: #ffffff;

  /* 标题与薪资 */
  .job-title-row {
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .job-title {
      font-size: 20px;
      color: #303133;
      margin-right: 16px;
    }

    .job-salary {
      font-size: 20px;
      color: #409EFF;
      font-weight: bold;
      margin-right: 100px;
    }
  }

  /* 职位类型标签 */
  .job-type-tag {
    margin-bottom: 12px;
  }

  /* 发布时间 */
  .publish-time {
    color: #909399;
    font-size: 12px;
    margin-bottom: 16px;
  }

  /* 职位详情段落 */
  .job-desc {
    .desc-line {
      margin: 8px 0;
      line-height: 1.5;
    }
  }
}


.job-details-container {
  display: flex;
  flex-direction: column;
  //flex: 1;
  width: 100%; // 设置容器宽度
  height: 100%; // 设置容器高度
  overflow-y: auto; // 启用垂直滚动
  //position: fixed;
  right: 10%;
  top: 10%;
}
</style>
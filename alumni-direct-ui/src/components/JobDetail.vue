<template>
  <!-- 职位详情 -->
  <div class="job-details-container" v-if="selectedJob">
    <div class="job-details">
      <h2>{{ selectedJob.title }} {{ formatSalary(selectedJob.minSalary, selectedJob.maxSalary) }}</h2>
      <p>职位类型：{{ getJobType(selectedJob.jobType) }}</p>
      <span>发布时间：{{ selectedJob.publishTime }}</span>
      <h3>职位详情</h3>
      <p>{{ selectedJob.jobDesc }}</p>

      <p>工作地点：{{ selectedJob.location }}</p>
      <div>
        <el-divider></el-divider>
        <el-avatar :size="30" :src="selectedJob.recruiterAvatar"/>
        招聘者：{{ selectedJob.recruiterName }}
      </div>
      <p>公司名称：{{ selectedJob.companyName }}</p>
    </div>
  </div>
</template>

<script setup>
// 定义props 使值传进组件
const props = defineProps({
  selectedJob: {
    type: Object,
    required: true
  }
})
// 工具函数
const formatSalary = (min, max) => {
  if (!min && !max) return '薪资面议'
  if (!max) return `${min}k以上`
  return `${min}-${max}k`
}

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
.job-details-container {
  display: flex;
  width: 58%;
  flex-direction: column;
  //flex: 1;
  height: 90%; // 设置容器高度
  overflow-y: auto; // 启用垂直滚动
  position: fixed;
  right: 10%;
  top: 10%;
}

.job-details {
  flex: 2;
  padding: 20px;
  border: 1px solid #428ef4;
  background-color: #f9f9f9; // 添加背景色
}
</style>
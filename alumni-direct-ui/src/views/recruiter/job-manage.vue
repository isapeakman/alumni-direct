<template>
  <div class="job-management">
    <div class="header">
      <!-- 发布职位按钮 -->
      <el-button type="primary" @click="toggleForm">
        {{ showForm ? '取消发布' : '发布职位' }}
      </el-button>
    </div>

    <div v-if="!showForm">
      <!-- 职位状态过滤器 -->
      <div class="status-filters">
        <el-button v-for="status in statuses" :key="status.value" @click="filterJobs(status.value)">
          {{ status.label }}
        </el-button>
      </div>

      <!-- 职位分页 -->
      <div class="section job-recommend">
        <div class="section-content">
          <el-row :gutter="20">
            <el-col :span="7" v-for="job in jobList" :key="job.title">
              <el-card shadow="hover" class="job-card" @click="handleShowDetail(job)">
                <div class="job-header">
                  <h4 class="job-status" :style="{ color: getStatusColor(job.status) }">
                    {{ getStatusText(job.status) }}
                  </h4>
                  <h4>{{ job.title }}</h4>
                  <div class="salary">{{ formatSalary(job.minSalary, job.maxSalary) }}</div>
                </div>
                <div class="info-row">
                  <p>{{ truncateText(job.jobDesc, 50) }}</p>
                  <span class="location">
                <el-icon><Location/></el-icon>
                  {{ job.location }}
                </span>
                  <span class="job-type">{{ getJobType(job.jobType) }}</span>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 分页器 -->
          <div class="pagination-container">
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :total="total"
                :page-sizes="[9, 12, 15, 18]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </div>

    <AddJobForm
        v-else
        @submit="handleJobSubmit"
        @cancel="toggleForm"
    />
    <!-- 职位详情弹窗 -->
    <JobDetailDialog
        ref="jobDetailDialog"
        :job="selectedJob"
        :jobCategories="jobCategories"
        @save="handleSaveJob"
        @refresh="fetchJobs(currentStatus)"
    />

  </div>

</template>

<script setup>
import {Location} from "@element-plus/icons-vue";
import {ref, onMounted} from 'vue'
import {addJob, getJobById} from '@/api/job.js'
import {ElMessage} from "element-plus";
import AddJobForm from "@/components/AddJobForm.vue";
import JobDetailDialog from "@/components/JobDetailDialog.vue";

const total = ref(0)
const jobList = ref([])
const currentPage = ref(1)
const pageSize = ref(9)
// 职位状态
const statuses = ref([
  {label: '全部', value: null, count: null},
  {label: '待审核', value: 0, count: 0},
  {label: '待发布', value: 1, count: 1},
  {label: '正在招', value: 2, count: 2},
  {label: '已关闭', value: 3, count: 3},
  {label: '未通过', value: 4, count: 4},
])

const showForm = ref(false); // 控制表单显示
const jobDetailDialog = ref(null);
const selectedJob = ref({});
const currentStatus = ref(null);
// 显示职位详情
const handleShowDetail = async (job) => {
  selectedJob.value = job;
  console.log('选中的职位:', job);
  jobDetailDialog.value.open();
};

// 保存职位修改
const handleSaveJob = (updatedJob) => {
  const index = jobList.value.findIndex(job => job.id === updatedJob.id);
  if (index !== -1) {
    jobList.value[index] = updatedJob;
  }
};
// 切换表单显示状态
const toggleForm = () => {
  showForm.value = !showForm.value;
};

// 处理表单提交
const handleJobSubmit = async (formData) => {
  console.log('提交的职位数据：', formData);

  // 将数组转换为逗号分隔字符串
  const payload = {
    ...formData,
    categoryIds: formData.categoryIds.join(",")
  };

  const response = await addJob(payload);
  if (response.data.code === 200) {
    ElMessage.success('发布职位成功');
    fetchJobs();
  } else {
    ElMessage.error(response.data.message || '发布职位失败');
  }
  toggleForm();
};

// 获取状态对应的文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待审批',
    1: '待发布',
    2: '已发布',
    3: '关闭',
    4: '审核未通过'
  }
  return statusMap[status] || '未知'
}
// 获取状态对应的颜色
const getStatusColor = (status) => {
  const colorMap = {
    0: 'black',
    1: '#09e0fb',
    2: '#68d754',
    3: '#cbcbcb',
    4: 'red'
  }
  return colorMap[status] || 'black'
}
// 截取文本
const truncateText = (text, length) => {
  if (text.length <= length) return text;
  return text.substring(0, length) + '...';
}
// 格式化薪资
const formatSalary = (min, max) => {
  if (!min && !max) return '薪资面议'
  if (!max) return `${min}k以上`
  return `${min}-${max}k`
}
// 获取职位列表
const fetchJobs = async (status) => {
  jobList.value = []; // 清空职位列表
  try {
    const response = await getJobById(status, currentPage.value, pageSize.value)
    if (response.data.code === 200) {
      jobList.value = response.data.data.records
      total.value = response.data.data.page.total
    } else {
      ElMessage.error(response.data.message || '获取职位列表失败')
    }
  } catch (error) {
    console.error('获取职位列表出错：', error)
    ElMessage.error('获取职位列表失败')
  }
}
// 过滤职位
const filterJobs = (status) => {
  console.log('当前过滤的职位状态:', status)
  currentStatus.value = status
  fetchJobs(status)
}

onMounted(() => {
  fetchJobs()
})
const getJobType = (type) => {
  const typeMap = {
    0: '全职',
    1: '实习',
    2: '兼职'
  }
  return typeMap[type] || '未知'
}
// 分页处理函数
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchJobs()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchJobs()
}
</script>

<style scoped>
.job-management {
  padding: 0;
}

.header {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 5px;
}

.status-filters {
  display: flex;
  gap: 5px;
  margin-bottom: 5px;
}

.section {
  padding: 10px 0;

  .section-content {
    max-width: 1200px;
    margin: 0 auto;
  }
}

.job-card {
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s; /* 调整过渡时间为 0.3s */
  width: 280px; /* 固定宽度 */
  height: 150px; /* 固定高度 */
  position: relative; /* 为子元素的绝对定位提供参考 */
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  overflow: hidden; /* 确保内容不会超出卡片范围 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  .job-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    h4 {
      margin: 0;
      font-size: 16px;
      color: #303133;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .salary {
      color: #f56c6c;
      font-weight: bold;
      font-size: 15px;
      white-space: nowrap;
    }
  }

  .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    color: #909399;
    font-size: 13px;

    .location {
      display: flex;
      align-items: center;
      gap: 4px;
      position: absolute;
      bottom: 10px;
      left: 10px;
      max-width: 50%; /* 限制宽度，避免超出卡片 */
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .job-type {
      position: absolute;
      bottom: 10px;
      right: 10px;
      padding: 2px 8px;
      background-color: #f0f2f5;
      border-radius: 4px;
      max-width: 50%; /* 限制宽度，避免超出卡片 */
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}
</style>
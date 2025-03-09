<template>
  <div class="approval-management">
    <h2>审批管理</h2>
    <div class="filter-section">
      <el-radio-group
          v-model="selectedFilterLabel"
          @change="handleFilterChange"
      >
        <el-radio
            v-for="option in filterOptions"
            :key="option.label"
            :label="option"
        >
          {{ option.label }}
        </el-radio>
      </el-radio-group>
    </div>

    <el-table :data="paginatedRecords" style="width: 100%">
      <el-table-column prop="id" label="记录ID" width="60"></el-table-column>
      <el-table-column prop="createId" label="发起人ID" width="80"></el-table-column>
      <el-table-column prop="jobId" label="职位ID" width="80"></el-table-column>
      <el-table-column prop="title" label="职位标题" width="180"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column prop="approvalTime" label="审批时间" width="180"></el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="scope">
          <span :style="{ color: getStatusColor(scope.row) }">
            {{ getStatusText(scope.row) }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-button @click="viewDetail(row)" :icon="Search" circle></el-button>
          <!--          <el-button @click="approve(row)">审批</el-button>-->
          <!--          <el-button type="danger" @click="rejectRecord(row)">拒绝</el-button>-->
        </template>
      </el-table-column>
      <el-table-column prop="note" label="未通过原因" width="180"></el-table-column>
    </el-table>

    <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalRecords"
        @current-change="handlePageChange"
        layout="total, prev, pager, next, jumper"
    />
  </div>

  <JobDetailDialog
      ref="jobDetailDialog"
      :job="selectedJob"
      :jobCategories="jobCategories"
      :on-approve="approve"
      :user-role="userRole"
      :on-reject="rejectRecord"
  /> <!-- 传递审批和拒绝方法 -->
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {approveRecord, getApprovals} from "@/api/jobApproval.js";
import {Search, View} from "@element-plus/icons-vue";
import JobDetailDialog from "@/views/recruiter/manage/JobDetailDialog.vue";

const records = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(0)
const approvalStatus = ref(null) // 0: 未审核, 1: 已审核
const approvalResult = ref(null) // 0: 通过, 1: 不通过
const jobDetailDialog = ref(null);
const selectedJob = ref({})
const filterOptions = [
  {label: '全部', status: null, result: null},
  {label: '未审批', status: 0, result: null},    // 未审批：审批状态未处理
  {label: '已审批', status: 1, result: null},    // 已审批：所有已处理记录
  {label: '审批通过', status: 1, result: 1},     // 通过：status=1且result=1
  {label: '审批未通过', status: 1, result: 0}    // 未通过：status=1且result=0
];
const userRole = "admin"; // 管理员角色
const selectedFilter = ref(filterOptions[0]); // 默认选中"未审批"
// 记录状态 过滤
const handleFilterChange = (option) => {
  console.log('选中的过滤条件：', option);
  console.log('选中的过滤条件：', option.status);
  console.log('选中的过滤条件：', option.result);
  approvalStatus.value = option.status;
  approvalResult.value = option.result;
  currentPage.value = 1; // 重置分页到第一页
  fetchRecords(); // 触发数据刷新
};

// 获取审批记录
const fetchRecords = async () => {
  try {
    const response = await getApprovals(approvalStatus.value, approvalResult.value, currentPage.value, pageSize.value)

    if (response.data.code === 200) {
      records.value = response.data.data.records
      totalRecords.value = response.data.data.page.total
    } else {
      ElMessage.error(response.data.message)
    }
  } catch (error) {
    console.error('获取审批记录失败：', error)
    ElMessage.error('获取审批记录失败')
  }
}
// 分页数据
const paginatedRecords = computed(() => {
  return records.value
})
//页数改变
const handlePageChange = (page) => {
  currentPage.value = page
  fetchRecords()
}
// 查看详情
const viewDetail = (record) => {
  console.log('选中的记录：', record);
  jobDetailDialog.value.open();
  selectedJob.value = record
}
// 对应审批颜色方法
const getStatusColor = (row) => {
  if (row.approvalStatus === 0) return '#FF9900'; // 待审批：橙色
  return row.approvalResult === 1 ? '#2ecc71' : '#e74c3c'; // 通过/拒绝
};

// 获取状态文本
const getStatusText = (row) => {
  if (row.approvalStatus === 0) { // 未审批状态
    return '未审批';
  } else if (row.approvalResult === 1) { // 审批通过
    return '审批通过';
  } else if (row.approvalResult === 0) { // 审批拒绝
    return '审批未通过';
  }
  return '未知状态'; // 处理默认情况
};

// 审批通过
const approve = async (record) => {
  // 审批通过逻辑
  console.log('选中的记录：', record.id);
  const response = await approveRecord(record.id, 1, null, 1)
  console.log('审批结果：', response);
  if (response.data.code === 200) {
    ElMessage.success(`已通过审批`)
    await fetchRecords(); // 重新加载数据
  } else {
    ElMessage.error(response.data.message)
  }
}
// 审批拒绝
const rejectRecord = async (record) => {
  console.log('选中的记录：', record.id);
  const response = await approveRecord(record.id, 1, record.note, 0)
  console.log('审批结果：', response);
  if (response.data.code === 200) {
    ElMessage.success(`已拒绝成功`)
    await fetchRecords(); // 重新加载数据
  } else {
    ElMessage.error(response.data.message)
  }
}
//格式化薪资
const formatSalary = (min, max) => {
  if (!min && !max) return '薪资面议'
  if (!max) return `${min}k以上`
  return `${min}-${max}k`
}

const getJobTypeLabel = (type) => {
  const typeMap = {
    0: '全职',
    1: '兼职',
    2: '实习'
  }
  return typeMap[type] || '未知'
}

onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
.approval-management {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
}

.filter-section {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
}

</style>
<template>
  <div class="approval-management">
    <h2>审批管理</h2>
    <el-table :data="paginatedRecords" style="width: 100%">
      <el-table-column prop="id" label="记录ID" width="60"></el-table-column>
      <el-table-column prop="createId" label="发起人ID" width="80"></el-table-column>
      <el-table-column prop="jobId" label="职位ID" width="80"></el-table-column>
      <el-table-column prop="title" label="职位标题" width="180"></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      <el-table-column label="操作" width="300">
        <template #default="{ row }">
          <el-button @click="viewDetail(row)" :icon="Search" circle></el-button>
          <el-button @click="approve(row)">审批</el-button>
          <el-button type="danger" @click="rejectRecord(row)">拒绝</el-button>
        </template>
      </el-table-column>
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
      job="selectedJob"
      :jobCategories="jobCategories"
  />
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
const selectedJob = ref(null)
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
// 审批通过
const approve = async (record) => {
  // 审批通过逻辑
  console.log('选中的记录：', record.id);
  const response = await approveRecord(record.id, 0)
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
  const response = await approveRecord(record.id, 1)
  console.log('审批结果：', response);
  if (response.data.code === 200) {
    ElMessage.success(`已拒绝成功`)
    await fetchRecords(); // 重新加载数据
  } else {
    ElMessage.error(response.data.message)
  }
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
</style>
<template>
  <div class="alumni-approval">
    <h2>校友认证审批</h2>
    <div class="filter-section">
      <el-radio-group v-model="selectedFilter" @change="handleFilterChange">
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
      <el-table-column prop="id" label="申请ID" width="80"></el-table-column>
      <el-table-column prop="userId" label="申请人ID" width="100"></el-table-column>
      <el-table-column prop="applyTime" label="申请时间" width="150">
        <template #default="{ row }">
          {{ formatDate(row.applyTime) }}
        </template>
      </el-table-column>
      <el-table-column label="详细资料" width="120">
        <template #default="{ row }">
          <el-button type="text" @click="showDetailDialog(row)">查看详细资料</el-button>
        </template>
      </el-table-column>
      <el-table-column prop="approvalStatus" label="审批状态" width="120">
        <template #default="{ row }">
          <span :style="{ color: getStatusColor(row) }">
            {{ getStatusText(row) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="note" label="审批意见" width="200"></el-table-column>
      <el-table-column prop="approvalTime" label="审批时间" width="150">
        <template #default="{ row }">
          {{ formatDate(row.approvalTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button
              v-if="row.approvalStatus === 0"
              type="success"
              size="small"
              @click="approve(row)"
          >
            通过
          </el-button>
          <el-button
              v-if="row.approvalStatus === 0"
              type="danger"
              size="small"
              @click="showRejectDialog(row)"
          >
            拒绝
          </el-button>
          <span v-if="row.approvalStatus !== 0">已处理</span>
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

    <!-- 材料查看对话框 -->
    <el-dialog v-model="materialDialogVisible" title="认证材料" width="70%">
      <div class="material-preview">
        <div v-for="(material, index) in currentMaterials" :key="index" class="material-item">
          <el-image
              :src="material.url"
              :preview-src-list="currentMaterials.map(m => m.url)"
              fit="contain"
              style="width: 100%; height: 400px;"
          >
            <template #error>
              <div class="material-error">
                <span>PDF文件请下载查看</span>
                <el-button type="primary" @click="downloadMaterial(material)">下载</el-button>
              </div>
            </template>
          </el-image>
        </div>
      </div>
    </el-dialog>

    <!-- 拒绝原因输入对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="请输入拒绝原因" width="50%">
      <el-input
          v-model="rejectReason"
          type="textarea"
          :rows="4"
          placeholder="请详细说明拒绝原因，以便申请人修改后重新提交"
      ></el-input>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 详细资料对话框 -->
    <el-dialog v-model="detailDialogVisible" title="详细资料" width="50%">
      <div class="detail-preview">
        <p>姓名: {{ currentDetail.name }}</p>
        <p>学号: {{ currentDetail.studentId }}</p>
        <p>入学年份: {{ formatDate(currentDetail.yearAdmission) }}</p>
        <p>毕业年份: {{ formatDate(currentDetail.yearGraduated) }}</p>
        <p>专业: {{ currentDetail.major }}</p>
        <p>联系方式: {{ currentDetail.phone }}</p>
        <div class="material-thumbs">
          <el-image
              v-for="(img, index) in getMaterials(currentDetail)"
              :key="index"
              :src="img.url"
              :preview-src-list="getMaterials(currentDetail).map(m => m.url)"
              style="width: 100px; height: 100px; margin-right: 10px;"
          ></el-image>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import {ElMessage} from 'element-plus'
import {approveAuth, getApproval} from '@/api/auth.js'

// 模拟数据
const mockRecords = []

const records = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(0)
const filterOptions = [
  {label: '全部', status: null, result: null},
  {label: '待审批', status: 0, result: null},
  {label: '已审批', status: 1, result: null},
  {label: '审批通过', status: 1, result: 1},
  {label: '审批未通过', status: 1, result: 0}
]
const selectedFilter = ref(filterOptions[0])
const materialDialogVisible = ref(false)
const currentMaterials = ref([])
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentRejectRecord = ref(null)
const detailDialogVisible = ref(false)
const currentDetail = ref({})

// 初始化数据
const initData = async () => {
  try {
    const response = await getApproval(currentPage.value, pageSize.value);
    records.value = response.data.data.records;
    totalRecords.value = response.data.data.page.total;
  } catch (error) {
    ElMessage.error('获取认证列表失败');
  }
}

// 过滤记录
const filteredRecords = computed(() => {
  if (selectedFilter.value.status === null) {
    return records.value
  }

  return records.value.filter(record => {
    const statusMatch = selectedFilter.value.status === null ||
        record.approvalStatus === selectedFilter.value.status
    const resultMatch = selectedFilter.value.result === null ||
        record.approvalResult === selectedFilter.value.result
    return statusMatch && resultMatch
  })
})

// 分页记录
const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredRecords.value.slice(start, end)
})

// 处理过滤条件变化
const handleFilterChange = (option) => {
  selectedFilter.value = option
  currentPage.value = 1
  initData()
}

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  initData()
}

// 查看材料
const viewMaterials = (record) => {
  currentMaterials.value = [
    {name: '认证材料1', url: record.certification},
    {name: '认证材料2', url: record.certification2}
  ].filter(material => material.url);
  materialDialogVisible.value = true
}

// 下载材料
const downloadMaterial = (material) => {
  ElMessage.info(`开始下载: ${material.name}`)
  // 实际项目中这里应该是调用下载API
}

// 显示详细资料对话框
const showDetailDialog = (record) => {
  currentDetail.value = record
  detailDialogVisible.value = true
}

// 显示拒绝对话框
const showRejectDialog = (record) => {
  currentRejectRecord.value = record
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

// 确认拒绝
const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }

  const record = currentRejectRecord.value
  record.approvalResult = 0
  record.note = rejectReason.value
  record.approvalTime = new Date().toISOString()

  // 实际项目中这里应该是调用更新API
  const response = await approveAuth(record.id, record.note, record.approvalResult)
  if (response.data.code !== 200) {
    ElMessage.error('审批失败')
    return
  }
  ElMessage.success('已拒绝该申请')
  rejectDialogVisible.value = false
  initData()
}

// 审批通过
const approve = async (record) => {
  record.approvalResult = 1
  record.note = '资料齐全，符合要求'

  // 实际项目中这里应该是调用更新API

  const response = await approveAuth(record.id, record.note, record.approvalResult)
  if (response.data.code !== 200) {
    ElMessage.error('审批失败')
    return
  }
  ElMessage.success('已通过该申请')
  // 更新记录
  initData()
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
}

// 获取状态颜色
const getStatusColor = (row) => {
  if (row.approvalStatus === 0) return '#FF9900' // 待审批：橙色
  return row.approvalResult === 1 ? '#2ecc71' : '#e74c3c' // 通过/拒绝
}

// 获取状态文本
const getStatusText = (row) => {
  if (row.approvalStatus === 0) return '待审批'
  return row.approvalResult === 1 ? '已通过' : '未通过'
}

// 获取审批结果文本
const getApprovalResultText = (row) => {
  return row.approvalResult === 1 ? '通过' : '未通过'
}

// 获取材料
const getMaterials = (record) => {
  return [
    {name: '认证材料1', url: record.certification},
    {name: '认证材料2', url: record.certification2}
  ].filter(material => material.url);
}

onMounted(() => {
  initData()
})
</script>

<style scoped>
.alumni-approval {
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

.material-preview {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.material-item {
  border: 1px solid #eee;
  padding: 10px;
  border-radius: 4px;
}

.material-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 10px;
}

.material-thumbs {
  display: flex;
  flex-wrap: wrap;
}

.detail-preview {
  padding: 20px;
}
</style>

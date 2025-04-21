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
      <el-table-column prop="name" label="姓名" width="100"></el-table-column>
      <el-table-column prop="studentId" label="学号" width="120"></el-table-column>
      <el-table-column prop="enrollmentYear" label="入学年份" width="100"></el-table-column>
      <el-table-column prop="graduationYear" label="毕业年份" width="100"></el-table-column>
      <el-table-column prop="major" label="专业" width="180"></el-table-column>
      <el-table-column prop="contact" label="联系方式" width="150"></el-table-column>

      <el-table-column label="认证材料" width="120">
        <template #default="{ row }">
          <el-button @click="viewMaterials(row)" type="text">查看材料</el-button>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <span :style="{ color: getStatusColor(row) }">
            {{ getStatusText(row) }}
          </span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button
              v-if="row.status === 0"
              type="success"
              size="small"
              @click="approve(row)"
          >
            通过
          </el-button>
          <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              @click="showRejectDialog(row)"
          >
            拒绝
          </el-button>
          <span v-if="row.status !== 0">已处理</span>
        </template>
      </el-table-column>

      <el-table-column prop="rejectReason" label="拒绝原因" width="200"></el-table-column>
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
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import {ElMessage} from 'element-plus'

// 模拟数据
const mockRecords = [
  {
    id: 1,
    name: '张三',
    studentId: '2015101010',
    enrollmentYear: '2015',
    graduationYear: '2019',
    major: '计算机学院 软件工程专业',
    contact: '13800138000',
    materials: [
      {name: '学生证.jpg', url: 'https://dummyimage.com/600x400/eee/999'},
      {name: '毕业证.jpg', url: 'https://dummyimage.com/600x400/eee/999'}
    ],
    status: 0, // 0: 待审核, 1: 已审核
    result: null, // 0: 拒绝, 1: 通过
    rejectReason: ''
  },
  {
    id: 2,
    name: '李四',
    studentId: '2016102020',
    enrollmentYear: '2016',
    graduationYear: '2020',
    major: '经济学院 金融学专业',
    contact: 'li.si@example.com',
    materials: [
      {name: '学生证.pdf', url: 'https://example.com/student.pdf'},
      {name: '学位证.jpg', url: 'https://dummyimage.com/600x400/eee/999'}
    ],
    status: 1,
    result: 1,
    rejectReason: ''
  },
  {
    id: 3,
    name: '王五',
    studentId: '2017103030',
    enrollmentYear: '2017',
    graduationYear: '2021',
    major: '文学院 汉语言文学专业',
    contact: '13900139000',
    materials: [
      {name: '学生证.jpg', url: 'https://dummyimage.com/600x400/eee/999'}
    ],
    status: 1,
    result: 0,
    rejectReason: '提供的学生证照片不清晰，无法辨认学号信息'
  }
]

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

// 初始化数据
const initData = () => {
  records.value = [...mockRecords]
  totalRecords.value = records.value.length
}

// 过滤记录
const filteredRecords = computed(() => {
  if (selectedFilter.value.status === null) {
    return records.value
  }

  return records.value.filter(record => {
    const statusMatch = selectedFilter.value.status === null ||
        record.status === selectedFilter.value.status
    const resultMatch = selectedFilter.value.result === null ||
        record.result === selectedFilter.value.result
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
}

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page
}

// 查看材料
const viewMaterials = (record) => {
  currentMaterials.value = record.materials
  materialDialogVisible.value = true
}

// 下载材料
const downloadMaterial = (material) => {
  ElMessage.info(`开始下载: ${material.name}`)
  // 实际项目中这里应该是调用下载API
}

// 获取状态颜色
const getStatusColor = (row) => {
  if (row.status === 0) return '#FF9900' // 待审批：橙色
  return row.result === 1 ? '#2ecc71' : '#e74c3c' // 通过/拒绝
}

// 获取状态文本
const getStatusText = (row) => {
  if (row.status === 0) return '待审批'
  return row.result === 1 ? '已通过' : '未通过'
}

// 显示拒绝对话框
const showRejectDialog = (record) => {
  currentRejectRecord.value = record
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

// 确认拒绝
const confirmReject = () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }

  const record = currentRejectRecord.value
  record.status = 1
  record.result = 0
  record.rejectReason = rejectReason.value

  ElMessage.success('已拒绝该申请')
  rejectDialogVisible.value = false
}

// 审批通过
const approve = (record) => {
  record.status = 1
  record.result = 1
  record.rejectReason = ''

  ElMessage.success('已通过该申请')
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
</style>
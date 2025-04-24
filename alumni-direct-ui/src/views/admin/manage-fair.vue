<template>
  <div class="activity-management">
    <el-card>
      <h2>活动管理</h2>
      <el-button type="primary" @click="showAddDialog">添加活动</el-button>

      <el-dialog title="添加活动" v-model="dialogVisible" width="50%">
        <el-form :model="activityForm" :rules="rules" ref="activityFormRef" label-width="120px">
          <el-form-item label="活动类型" prop="type">
            <el-select v-model="activityForm.type" placeholder="请选择活动类型">
              <el-option label="招聘会" :value="0"/>
              <el-option label="宣讲会" :value="1"/>
            </el-select>
          </el-form-item>
          <el-form-item label="活动名称" prop="name">
            <el-input v-model="activityForm.name" placeholder="输入活动名称"></el-input>
          </el-form-item>
          <el-form-item label="主办方" prop="organizer">
            <el-input v-model="activityForm.organizer" placeholder="输入主办方信息"></el-input>
          </el-form-item>
          <el-form-item label="公司" prop="company">
            <el-input v-model="activityForm.company" placeholder="输入公司名称"></el-input>
          </el-form-item>
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker v-model="activityForm.startTime" type="datetime"
                            placeholder="选择开始时间"></el-date-picker>
          </el-form-item>
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker v-model="activityForm.endTime" type="datetime" placeholder="选择结束时间"></el-date-picker>
          </el-form-item>
          <el-form-item label="地点" prop="location">
            <el-input v-model="activityForm.location" placeholder="输入举办地点"></el-input>
          </el-form-item>
          <el-form-item label="联系人" prop="contactName">
            <el-input v-model="activityForm.contactName" placeholder="输入联系人"></el-input>
          </el-form-item>
          <el-form-item label="联系方式" prop="contactWay">
            <el-input v-model="activityForm.contactWay" placeholder="输入联系方式"></el-input>
          </el-form-item>
          <el-form-item label="活动图片" prop="imageUrl">
            <input type="file" @change="handleFileChange" accept="image/*"/>
            <div v-if="activityForm.imageUrl">
              <img :src="activityForm.imageUrl" alt="Uploaded Image"
                   style="width: 100px; height: 100px; object-fit: cover; margin-top: 10px;"/>
            </div>
          </el-form-item>
          <el-form-item label="招聘者" prop="recruiterId">
            <el-input v-model="activityForm.recruiterId" placeholder="输入招聘者ID"></el-input>
          </el-form-item>
          <el-form-item label="活动描述" prop="description">
            <el-input v-model="activityForm.description" type="textarea" placeholder="输入活动描述"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="resetForm">重置</el-button>
            <el-button type="primary" @click="handleSubmit">提交</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>

    <el-card>
      <el-table :data="activities" style="width: 100%">
        <el-table-column label="类型">
          <template #default="scope">
            {{ scope.row.type === 0 ? '招聘会' : '宣讲会' }}
          </template>
        </el-table-column>
        <el-table-column prop="organizer" label="主办方"></el-table-column>
        <el-table-column prop="company" label="公司"></el-table-column>
        <el-table-column prop="startTime" label="开始时间"></el-table-column>
        <el-table-column prop="endTime" label="结束时间"></el-table-column>
        <el-table-column prop="location" label="地点"></el-table-column>
        <el-table-column prop="contactName" label="联系人"></el-table-column>
        <el-table-column prop="contactWay" label="联系方式"></el-table-column>
        <el-table-column label="图片">
          <template #default="scope">
            <img :src="scope.row.imageUrl" alt="Activity Image" style="width: 50px; height: 50px; object-fit: cover;"/>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="editActivity(scope.row)">修改</el-button>
            <el-button size="small" type="danger" @click="deleteActivity(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="totalRecords"
        @current-change="handlePageChange"
        layout="total, prev, pager, next, jumper"
    />

    <el-dialog title="编辑活动" v-model="editDialogVisible" width="50%">
      <el-form :model="activityForm" :rules="rules" ref="activityFormRef" label-width="120px">
        <el-form-item label="活动类型" prop="type">
          <el-select v-model="activityForm.type" placeholder="请选择活动类型">
            <el-option label="招聘会" :value="0"/>
            <el-option label="宣讲会" :value="1"/>
          </el-select>
        </el-form-item>
        <el-form-item label="主办方" prop="organizer">
          <el-input v-model="activityForm.organizer" placeholder="输入主办方信息"></el-input>
        </el-form-item>
        <el-form-item label="公司" prop="company">
          <el-input v-model="activityForm.company" placeholder="输入公司名称"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="activityForm.startTime" type="datetime"
                          placeholder="选择开始时间"></el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="activityForm.endTime" type="datetime" placeholder="选择结束时间"></el-date-picker>
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="activityForm.location" placeholder="输入举办地点"></el-input>
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="activityForm.contactName" placeholder="输入联系人"></el-input>
        </el-form-item>
        <el-form-item label="联系方式" prop="contactWay">
          <el-input v-model="activityForm.contactWay" placeholder="输入联系方式"></el-input>
        </el-form-item>
        <el-form-item label="活动图片" prop="imageUrl">
          <input type="file" @change="handleFileChange" accept="image/*"/>
          <div v-if="activityForm.imageUrl">
            <img :src="activityForm.imageUrl" alt="Uploaded Image"
                 style="width: 100px; height: 100px; object-fit: cover; margin-top: 10px;"/>
          </div>
        </el-form-item>
        <el-form-item label="招聘者" prop="recruiterId">
          <el-input v-model="activityForm.recruiterId" placeholder="输入招聘者ID"></el-input>
        </el-form-item>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="activityForm.description" type="textarea" placeholder="输入活动描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelEdit">取 消</el-button>
          <el-button type="primary" @click="confirmEdit">确认修改</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import axios from "axios";
import {addFair, deleteFair, getFairList, updateFair} from "@/api/fair.js";
import {ElMessage} from "element-plus";
import {uploadFile} from "@/api/file.js";

const currentPage = ref(1);
const pageSize = ref(10);
const totalRecords = ref(0);
const activityForm = ref({
  type: null,
  organizer: "",
  company: "",
  startTime: null,
  endTime: null,
  location: "",
  contactName: "",
  contactWay: "",
  imageUrl: "",
  recruiterId: null,
  file: null, // 新增文件字段
  description: "",
  name: "",
});
const rules = {
  type: [{required: true, message: "请选择活动类型", trigger: "change"}],
  organizer: [{required: true, message: "请输入主办方", trigger: "blur"}],
  company: [{required: true, message: "请输入公司名称", trigger: "blur"}],
  startTime: [{required: true, message: "请选择开始时间", trigger: "change"}],
  endTime: [{required: true, message: "请选择结束时间", trigger: "change"}],
  location: [{required: true, message: "请输入地点", trigger: "blur"}],
  contactName: [{required: true, message: "请输入联系人", trigger: "blur"}],
  contactWay: [{required: true, message: "请输入联系方式", trigger: "blur"}],
  imageUrl: [{required: true, message: "请上传活动图片", trigger: "change"}],
  recruiterId: [{required: true, message: "请输入招聘者ID", trigger: "blur"}]
};

const fileList = ref([]);
const activities = ref([]);
const total = ref(0);
const dialogVisible = ref(false);
const editDialogVisible = ref(false);
const activityFormRef = ref(null);
const originalActivityForm = ref({}); // 用于备份原始数据

const fetchActivities = async () => {
  try {
    const response = await getFairList(currentPage.value, pageSize.value);

    if (response.data.code === 200) {
      activities.value = response.data.data.records;
      totalRecords.value = response.data.data.page.total;
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error) {
    console.error('获取活动列表失败：', error);
    ElMessage.error('获取活动列表失败');
  }
};

const handleFileChange = async (event) => {
  const file = event.target.files[0];
  console.log("选择的文件", file);
  if (file) {
    try {
      const formData = new FormData();
      formData.append('file', file);
      const uploadResponse = await uploadFile(formData);
      if (uploadResponse.data.code === 200) {
        activityForm.value.imageUrl = uploadResponse.data.data; // 更新活动表单的图片URL
        ElMessage.success("文件上传成功");
      } else {
        ElMessage.error(uploadResponse.data.message || "文件上传失败");
      }
    } catch (error) {
      console.error("文件上传失败", error);
      ElMessage.error("文件上传失败");
    }
  }
};


// 提交活动信息
const handleSubmit = async () => {
  // 开始时间和结束时间的验证
  if (new Date(activityForm.value.startTime) >= new Date(activityForm.value.endTime)) {
    ElMessage.error("结束时间必须大于开始时间");
    return;
  }

  activityForm.value.startTime = formatDate(activityForm.value.startTime);
  activityForm.value.endTime = formatDate(activityForm.value.endTime);
  console.log("活动信息", activityForm.value);

  activityFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 提交活动信息
        await addFair(activityForm.value);
        dialogVisible.value = false;
        resetForm();
        fetchActivities();
      } catch (error) {
        console.error("添加活动失败", error);
        ElMessage.error("添加活动失败");
      }
    } else {
      console.error("表单验证失败");
      ElMessage.error("表单验证失败");
    }
  });
};

// 重置表单
const resetForm = () => {
  activityForm.value = {
    type: null,
    organizer: "",
    company: "",
    startTime: null,
    endTime: null,
    location: "",
    contactName: "",
    contactWay: "",
    imageUrl: "",
    recruiterId: null,
    file: null,
    description: "",
    name: "",
  };
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  fetchActivities();
};

// 显示编辑框
const editActivity = (activity) => {
  Object.assign(originalActivityForm.value, activity); // 备份原始数据
  Object.assign(activityForm.value, activity);
  editDialogVisible.value = true;
};

// 确认编辑
const confirmEdit = async () => {
  activityFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 提交更新后的活动信息
        const response = await updateFair(activityForm.value);
        if (response.data.code === 200) {
          editDialogVisible.value = false;
          resetForm();
          ElMessage.success("活动更新成功");
          fetchActivities();
        } else {
          ElMessage.error(response.message || "活动更新失败");
        }
      } catch (error) {
        console.error("更新活动失败", error);
        ElMessage.error("更新活动失败");
      }
    } else {
      console.error("表单验证失败");
      ElMessage.error("表单验证失败");
    }
  });
};

// 取消编辑
const cancelEdit = () => {
  Object.assign(activityForm.value, originalActivityForm.value); // 恢复原始数据
  editDialogVisible.value = false;
  resetForm(); // 重置表单
};

// 删除活动
const deleteActivity = async (activity) => {
  console.log("删除活动", activity);
  if (!activity.id) {
    console.error('活动对象缺少 id 属性:', activity);
    ElMessage.error('活动对象缺少 id 属性');
    return;
  }

  try {
    const response = await deleteFair(activity.id);
    if (response.data.code === 200) {
      ElMessage.success("删除活动成功");
      fetchActivities();
    } else {
      ElMessage.error(response.message || "删除活动失败");
    }
  } catch (error) {
    console.error("删除活动失败", error);
    ElMessage.error("删除活动失败");
  }
};

// 显示添加活动对话框
const showAddDialog = () => {
  resetForm(); // 确保表单被重置
  dialogVisible.value = true;
};

onMounted(() => {
  fetchActivities();
});

// 格式化日期函数
const formatDate = (date) => {
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).replace(/\//g, '-');
};
</script>

<style scoped>
.activity-management {
  padding: 20px;
}

.el-card {
  margin-bottom: 20px;
}
</style>

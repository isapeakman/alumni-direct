<template>
  <div id="app">
    <el-form :model="form" label-width="120px" @submit.prevent="submitForm" :disabled="active === 2">
      <el-form-item label="真实姓名">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="学号">
        <el-input v-model="form.studentId"></el-input>
      </el-form-item>
      <el-form-item label="入学年份">
        <el-date-picker v-model="form.yearAdmission" type="date" placeholder="选择日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="毕业年份">
        <el-date-picker v-model="form.yearGraduated" type="date" placeholder="选择日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="专业">
        <el-input v-model="form.major"></el-input>
      </el-form-item>
      <el-form-item label="联系电话">
        <el-input v-model="form.phone" maxlength="11" show-word-limit></el-input>
      </el-form-item>
      <el-form-item label="认证材料">
        <el-upload
            list-type="picture-card"
            :disabled="active === 2 || active === 3"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
        <el-dialog :visible.sync="dialogVisible">
          <img width="100%" :src="dialogImageUrl" alt="">
        </el-dialog>
      </el-form-item>
      <el-form-item v-if="active === 1">
        <el-button type="primary" @click="submitForm">提交申请</el-button>
      </el-form-item>
      <el-form-item v-if="active === 3">
        <el-button type="primary" @click="resetAndResubmit">重新提交申请</el-button>
      </el-form-item>
    </el-form>
    <el-steps :space="200" :active="active" finish-status="success">
      <el-step title="未申请"></el-step>
      <el-step title="申请中"></el-step>
      <el-step v-if="approvalStatus === 1 && applyResult === 0" status="error" title="申请失败"
               :description="reasonMessage"></el-step>
      <el-step v-else title="申请成功"></el-step>
    </el-steps>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import {apply, getAuth} from "@/api/auth.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const form = ref({
  name: '',
  studentId: '',
  yearAdmission: null, // 初始化为 null 或 new Date()
  yearGraduated: null, // 初始化为 null 或 new Date()
  major: '',
  phone: '',
  certification: '',
  certification_2: ''
});
const active = ref(1); // 只保留这一个响应式变量
const approvalStatus = ref(0); // 申请状态 0 未审批 1 已审批
const applyResult = ref(0); // 申请结果  0 未通过 1 已通过
const reasonMessage = ref(''); // 未通过原因
const dialogImageUrl = ref('');
const dialogVisible = ref(false);

// 提交表单
const submitForm = async () => {
  // 解包 form 对象
  const formData = form.value;
  // 认证材料暂时默认
  formData.certification = 'http://localhost:8080/ad/static/avator2.png';

  // 确保日期是字符串格式
  formData.yearAdmission = formData.yearAdmission ? formData.yearAdmission.toISOString().split('T')[0] : '';
  formData.yearGraduated = formData.yearGraduated ? formData.yearGraduated.toISOString().split('T')[0] : '';

  const response = await apply(formData);
  if (response.data.code === 200) {
    ElMessage.success('申请成功');
    // 重置进度条
    active.value = 2; // 申请中
    // 重新获取认证信息状态
    await getAuthDetail();
  } else {
    ElMessage.error(response.message || '申请失败');
  }
};

// 重新提交申请
const resetAndResubmit = () => {
  active.value = 1; // 重置进度条
  approvalStatus.value = 0;
  applyResult.value = 0;
  reasonMessage.value = '';
};

// 获取认证信息
const getAuthDetail = async () => {
  const response = await getAuth();
  if (response.data.code === 200) {
    const data = response.data.data;
    if (data.approvalStatus === 0) { // 申请中
      active.value = 2;
    } else if (data.approvalStatus === 1) { // 已审核
      approvalStatus.value = 1;
      applyResult.value = data.approvalResult;
      reasonMessage.value = data.note;
      active.value = 3;
      // 如果审核通过，跳转到成功页面
      if (data.approvalResult === 1) {
        router.push('/recruitment/auth/success');
        return; // 避免继续执行后续代码
      }
    }
    form.value = {
      ...data,
      yearAdmission: data.yearAdmission ? new Date(data.yearAdmission) : null,
      yearGraduated: data.yearGraduated ? new Date(data.yearGraduated) : null
    };
  } else {
    reset();
  }
};

const reset = () => {
  form.value = {
    name: '',
    studentId: '',
    yearAdmission: null,
    yearGraduated: null,
    major: '',
    phone: '',
    certification: '',
    certification_2: ''
  };
  active.value = 1; // 重置进度条
  applyResult.value = 0;
  reasonMessage.value = '';
};

onMounted(() => {
  getAuthDetail();
});
</script>

<style>
.status {
  margin-top: 20px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.status.success {
  border-color: green;
  background-color: #e9ffe3;
}

.status.pending {
  border-color: orange;
  background-color: #fff9db;
}

.status.error {
  border-color: red;
  background-color: #ffe3e3;
}

.flowchart {
  margin-top: 20px;
  display: flex;
  align-items: center;
}

.flowchart-step {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  border: 2px solid #ccc;
  border-radius: 50%;
  margin-right: 20px;
  position: relative;
}

.flowchart-step.active {
  border-color: green;
  background-color: #e9ffe3;
}

.flowchart-step::after {
  content: '';
  position: absolute;
  right: -40px;
  top: 40px;
  width: 40px;
  height: 2px;
  background-color: #ccc;
}

.flowchart-step:last-child::after {
  display: none;
}

.flowchart-step.active::after {
  background-color: green;
}
</style>

<template>
  <div id="app">
    <el-form :model="form" label-width="120px" @submit.prevent="submitForm">
      <el-form-item label="真实姓名">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="学号">
        <el-input v-model="form.student_id"></el-input>
      </el-form-item>
      <el-form-item label="入学年份">
        <el-date-picker v-model="form.year_admission" type="date" placeholder="选择日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="毕业年份">
        <el-date-picker v-model="form.year_graduated" type="date" placeholder="选择日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="专业">
        <el-input v-model="form.major"></el-input>
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="form.phone" maxlength="11" show-word-limit></el-input>
      </el-form-item>
      <el-form-item label="认证材料">
        <el-upload
            action="https://jsonplaceholder.typicode.com/posts/"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            :file-list="form.certification"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
        <el-dialog :visible.sync="dialogVisible">
          <img width="100%" :src="dialogImageUrl" alt="">
        </el-dialog>
      </el-form-item>
      <!--      <el-form-item label="另外的认证材料">-->
      <!--        <el-upload-->
      <!--            action="https://jsonplaceholder.typicode.com/posts/"-->
      <!--            list-type="picture-card"-->
      <!--            :on-preview="handlePictureCardPreview"-->
      <!--            :on-remove="handleRemove"-->
      <!--            :before-upload="beforeUpload"-->
      <!--            :file-list="form.certification_2"-->
      <!--        >-->
      <!--          <i class="el-icon-plus"></i>-->
      <!--        </el-upload>-->
      <!--        <el-dialog :visible.sync="dialogVisible">-->
      <!--          <img width="100%" :src="dialogImageUrl" alt="">-->
      <!--        </el-dialog>-->
      <!--      </el-form-item>-->
      <el-form-item>
        <el-button type="primary" @click="submitForm">提交申请</el-button>
      </el-form-item>
    </el-form>
    <el-steps :space="200" :active="1" finish-status="success">
      <el-step title="未申请"></el-step>
      <el-step title="申请中"></el-step>
      <el-step title="申请成功"></el-step>
    </el-steps>
  </div>
</template>

<script>
import {ref, reactive} from 'vue';

export default {
  setup() {
    const form = reactive({
      name: '',
      student_id: '',
      year_admission: '',
      year_graduated: '',
      major: '',
      phone: '',
      certification: [],
      certification_2: []
    });

    const status = ref('pending');
    const statusMessage = ref('申请中');
    const statusClass = ref('pending');
    const timestamp = ref(new Date().toLocaleString());

    const step1Class = ref('active');
    const step2Class = ref('');
    const step3Class = ref('');

    const dialogImageUrl = ref('');
    const dialogVisible = ref(false);

    const handleRemove = (file, fileList) => {
      console.log(file, fileList);
    };

    const handlePictureCardPreview = (file) => {
      dialogImageUrl.value = file.url;
      dialogVisible.value = true;
    };

    const beforeUpload = (file) => {
      const isJPGorPNG = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJPGorPNG) {
        this.$message.error('上传图片只能是 JPG 或 PNG 格式!');
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 2MB!');
      }
      return isJPGorPNG && isLt2M;
    };

    const submitForm = () => {
      // 模拟提交申请
      timestamp.value = new Date().toLocaleString();
      setTimeout(() => {
        status.value = 'success';
        statusMessage.value = '申请成功';
        statusClass.value = 'success';

        step1Class.value = '';
        step2Class.value = '';
        step3Class.value = 'active';
      }, 2000);
    };

    return {
      form,
      status,
      statusMessage,
      statusClass,
      timestamp,
      step1Class,
      step2Class,
      step3Class,
      dialogImageUrl,
      dialogVisible,
      handleRemove,
      handlePictureCardPreview,
      beforeUpload,
      submitForm
    };
  }
};
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

<template>
  <div class="alumni-verify-container">
    <div class="verify-card">
      <h2 class="title">校友身份认证</h2>

      <!-- 认证状态展示 -->
      <div class="status-section" v-if="authStatus !== '未认证'">
        <el-alert
            :title="statusTitle"
            :type="statusType"
            show-icon
            :closable="false"
        >
          <span v-if="authStatus === '已认证'">
            认证通过时间：{{ formatDate(verifyTime) }}
          </span>
        </el-alert>
      </div>
      <!-- 已认证信息展示 -->
      <div class="verified-info" v-if="authStatus === '已认证'">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="姓名">{{ verifyForm.name }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ verifyForm.studentId }}</el-descriptions-item>
          <el-descriptions-item label="入学时间">{{ formatDate(verifyForm.yearAdmission) }}</el-descriptions-item>
          <el-descriptions-item label="毕业时间">{{ formatDate(verifyForm.yearGraduated) }}</el-descriptions-item>
          <el-descriptions-item label="学院专业">{{ verifyForm.major }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ verifyForm.phone }}</el-descriptions-item>
          <el-descriptions-item label="认证材料">
            <div class="material-thumbs">
              <el-image
                  v-for="(img, index) in fileList"
                  :key="index"
                  :src="img.url"
                  :preview-src-list="fileList.map(f => f.url)"
                  style="width: 100px; height: 100px; margin-right: 10px;"
              ></el-image>
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </div>
  </div>
</template>

<script>
import {ref, reactive, computed, onMounted} from 'vue';
import {ElMessage} from 'element-plus';
import {getAuth} from "@/api/auth.js";

export default {
  name: 'AlumniVerify',
  setup() {
    const authStatus = ref('未认证'); // 未认证/审核中/已认证/已拒绝
    const verifyTime = ref('2023-06-15T10:30:00');
    const rejectReason = ref('提供的学生证照片不清晰，无法辨认学号信息');
    const verifyForm = reactive({
      name: '',
      studentId: '',
      yearAdmission: '',
      yearGraduated: '',
      major: '',
      phone: ''
    });
    const fileList = ref([]);
    const dialogVisible = ref(false);
    const dialogImageUrl = ref('');
    const submitting = ref(false);

    const statusTitle = computed(() => {
      return {
        '已认证': '校友身份已认证',
        '审核中': '认证审核中',
        '已拒绝': '认证未通过'
      }[authStatus.value];
    });

    const statusType = computed(() => {
      return {
        '已认证': 'success',
        '审核中': 'info',
        '已拒绝': 'error'
      }[authStatus.value];
    });

    const graduationYearOptions = computed(() => {
      return {
        disabledDate: (time) => {
          if (!verifyForm.enrollmentYear) return true;
          const year = new Date(time).getFullYear();
          return year < parseInt(verifyForm.enrollmentYear);
        }
      };
    });

    const formatDate = (dateString) => {
      const date = new Date(dateString);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} `;
    };

    const handleUploadChange = (file, fileList) => {
      fileList.value = fileList.map(f => ({
        name: f.name,
        url: f.url || URL.createObjectURL(f.raw)
      }));
    };

    const handleRemove = (file, fileList) => {
      fileList.value = fileList;
    };

    const handlePictureCardPreview = (file) => {
      dialogImageUrl.value = file.url;
      dialogVisible.value = true;
    };

    const submitVerify = () => {
      // 假设这里有一个表单引用，需要根据实际情况调整
      // this.$refs.verifyForm.validate((valid) => {
      //   if (valid) {
      submitting.value = true;

      // 模拟API请求
      setTimeout(() => {
        authStatus.value = '审核中';
        submitting.value = false;
        ElMessage.success('认证申请已提交，请耐心等待审核');
      }, 1500);
      //   }
      // });
    };

    const resetForm = () => {
      // 假设这里有一个表单引用，需要根据实际情况调整
      // this.$refs.verifyForm.resetFields();
      verifyForm.name = '';
      verifyForm.studentId = '';
      verifyForm.enrollmentYear = '';
      verifyForm.graduationYear = '';
      verifyForm.major = '';
      verifyForm.contact = '';
      fileList.value = [];
    };

    // 加载已认证信息
    const loadVerifiedData = async () => {
      const response = await getAuth();
      if (response.data.code !== 200) {
        ElMessage.error('获取认证信息失败');
        return;
      }
      const data = response.data.data;
      if (data.approvalResult === 1) {
        verifyForm.name = data.name;
        verifyForm.studentId = data.studentId;
        verifyForm.yearAdmission = data.yearAdmission;
        verifyForm.yearGraduated = data.yearGraduated;
        verifyForm.major = data.major;
        verifyForm.phone = data.phone;
        fileList.value = [data.certification, data.certification2].map((url, index) => ({
          name: `认证材料${index + 1}`,
          url
        }));

        verifyTime.value = data.approvalTime;
      }
    };

    onMounted(() => {
      // 模拟不同认证状态
      // authStatus.value = '未认证'; // 测试表单
      // authStatus.value = '审核中'; // 测试审核中状态
      // authStatus.value = '已拒绝'; // 测试拒绝状态
      authStatus.value = '已认证'; // 测试已认证状态
      loadVerifiedData();
    });

    return {
      authStatus,
      verifyTime,
      rejectReason,
      verifyForm,
      fileList,
      dialogVisible,
      dialogImageUrl,
      submitting,
      statusTitle,
      statusType,
      graduationYearOptions,
      formatDate,
      handleUploadChange,
      handleRemove,
      handlePictureCardPreview,
      submitVerify,
      resetForm,
      loadVerifiedData
    };
  }
};
</script>

<style scoped>
.alumni-verify-container {
  display: flex;
  justify-content: center;
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.verify-card {
  width: 100%;
  max-width: 800px;
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.status-section {
  margin-bottom: 30px;
}

.verify-form {
  margin-top: 20px;
}

.upload-item {
  margin-bottom: 20px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

.verified-info {
  margin-top: 30px;
}

.material-thumbs {
  display: flex;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .verify-card {
    padding: 20px;
  }

  .verify-form {
    margin-top: 10px;
  }
}
</style>

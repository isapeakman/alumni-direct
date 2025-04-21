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
          <span v-else-if="authStatus === '审核中'">
            预计审核时间：1-3个工作日
          </span>
          <span v-else>
            拒绝原因：{{ rejectReason }}
          </span>
        </el-alert>
      </div>

      <!-- 认证表单 -->
      <el-form
          :model="verifyForm"
          :rules="rules"
          ref="verifyForm"
          label-width="120px"
          class="verify-form"
          v-if="authStatus === '未认证' || authStatus === '已拒绝'"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="verifyForm.name" placeholder="请输入真实姓名"></el-input>
        </el-form-item>

        <el-form-item label="学号" prop="studentId">
          <el-input v-model="verifyForm.studentId" placeholder="请输入在校学号"></el-input>
        </el-form-item>

        <el-form-item label="入学年份" prop="enrollmentYear">
          <el-date-picker
              v-model="verifyForm.enrollmentYear"
              type="year"
              placeholder="选择入学年份"
              format="yyyy"
              value-format="yyyy"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="毕业年份" prop="graduationYear">
          <el-date-picker
              v-model="verifyForm.graduationYear"
              type="year"
              placeholder="选择毕业年份"
              format="yyyy"
              value-format="yyyy"
              :disabled="!verifyForm.enrollmentYear"
              :picker-options="graduationYearOptions"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="学院专业" prop="major">
          <el-input v-model="verifyForm.major" placeholder="例：计算机学院 软件工程专业"></el-input>
        </el-form-item>

        <el-form-item label="证明材料" prop="credentials" class="upload-item">
          <div class="upload-tip">
            请上传能证明校友身份的材料，如学生证、毕业证、学位证等（最多5张）
          </div>
          <el-upload
              action="#"
              list-type="picture-card"
              :file-list="fileList"
              :auto-upload="false"
              :on-change="handleUploadChange"
              :on-remove="handleRemove"
              :limit="5"
              accept="image/*,.pdf"
          >
            <i class="el-icon-plus"></i>
            <div slot="file" slot-scope="{file}">
              <img class="el-upload-list__item-thumbnail" :src="file.url" alt="">
              <span class="el-upload-list__item-actions">
                <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                  <i class="el-icon-zoom-in"></i>
                </span>
              </span>
            </div>
          </el-upload>
          <el-dialog :visible.sync="dialogVisible" append-to-body>
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>

        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="verifyForm.contact" placeholder="手机号/邮箱"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              @click="submitVerify"
              :loading="submitting"
          >
            提交认证
          </el-button>
          <el-button @click="resetForm" v-if="authStatus === '未认证'">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 已认证信息展示 -->
      <div class="verified-info" v-if="authStatus === '已认证'">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="姓名">{{ verifyForm.name }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ verifyForm.studentId }}</el-descriptions-item>
          <el-descriptions-item label="入学年份">{{ verifyForm.enrollmentYear }}年</el-descriptions-item>
          <el-descriptions-item label="毕业年份">{{ verifyForm.graduationYear }}年</el-descriptions-item>
          <el-descriptions-item label="学院专业">{{ verifyForm.major }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ verifyForm.contact }}</el-descriptions-item>
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
export default {
  name: 'AlumniVerify',
  data() {
    const validateCredentials = (rule, value, callback) => {
      if (this.fileList.length === 0) {
        callback(new Error('请上传至少一份证明材料'));
      } else {
        callback();
      }
    };

    return {
      authStatus: '未认证', // 未认证/审核中/已认证/已拒绝
      verifyTime: '2023-06-15T10:30:00',
      rejectReason: '提供的学生证照片不清晰，无法辨认学号信息',
      verifyForm: {
        name: '',
        studentId: '',
        enrollmentYear: '',
        graduationYear: '',
        major: '',
        contact: ''
      },
      fileList: [],
      dialogVisible: false,
      dialogImageUrl: '',
      submitting: false,
      rules: {
        name: [
          {required: true, message: '请输入姓名', trigger: 'blur'},
          {min: 2, max: 10, message: '长度在2到10个字符', trigger: 'blur'}
        ],
        studentId: [
          {required: true, message: '请输入学号', trigger: 'blur'},
          {pattern: /^[0-9]{8,12}$/, message: '学号应为8-12位数字', trigger: 'blur'}
        ],
        enrollmentYear: [
          {required: true, message: '请选择入学年份', trigger: 'change'}
        ],
        graduationYear: [
          {required: true, message: '请选择毕业年份', trigger: 'change'}
        ],
        major: [
          {required: true, message: '请输入学院专业', trigger: 'blur'}
        ],
        contact: [
          {required: true, message: '请输入联系方式', trigger: 'blur'},
          {pattern: /^1[3-9]\d{9}$|^\w+@\w+\.\w+$/, message: '请输入正确的手机号或邮箱', trigger: 'blur'}
        ],
        credentials: [
          {validator: validateCredentials, trigger: 'change'}
        ]
      }
    };
  },
  computed: {
    statusTitle() {
      return {
        '已认证': '校友身份已认证',
        '审核中': '认证审核中',
        '已拒绝': '认证未通过'
      }[this.authStatus];
    },
    statusType() {
      return {
        '已认证': 'success',
        '审核中': 'info',
        '已拒绝': 'error'
      }[this.authStatus];
    },
    graduationYearOptions() {
      return {
        disabledDate: (time) => {
          if (!this.verifyForm.enrollmentYear) return true;
          const year = new Date(time).getFullYear();
          return year < parseInt(this.verifyForm.enrollmentYear);
        }
      };
    }
  },
  methods: {
    formatDate(dateString) {
      const date = new Date(dateString);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },

    handleUploadChange(file, fileList) {
      this.fileList = fileList.map(f => ({
        name: f.name,
        url: f.url || URL.createObjectURL(f.raw)
      }));
    },

    handleRemove(file, fileList) {
      this.fileList = fileList;
    },

    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },

    submitVerify() {
      this.$refs.verifyForm.validate((valid) => {
        if (valid) {
          this.submitting = true;

          // 模拟API请求
          setTimeout(() => {
            this.authStatus = '审核中';
            this.submitting = false;
            this.$message.success('认证申请已提交，请耐心等待审核');
          }, 1500);
        }
      });
    },

    resetForm() {
      this.$refs.verifyForm.resetFields();
      this.fileList = [];
    },

    // 模拟加载已认证数据
    loadVerifiedData() {
      if (this.authStatus === '已认证') {
        this.verifyForm = {
          name: '张三',
          studentId: '2015101010',
          enrollmentYear: '2015',
          graduationYear: '2019',
          major: '计算机学院 软件工程专业',
          contact: '13800138000'
        };
        this.fileList = [
          {name: '学生证.jpg', url: 'https://dummyimage.com/600x400/eee/999'},
          {name: '毕业证.jpg', url: 'https://dummyimage.com/600x400/eee/999'}
        ];
      }
    }
  },
  created() {
    // 模拟不同认证状态
    // this.authStatus = '未认证'; // 测试表单
    // this.authStatus = '审核中'; // 测试审核中状态
    // this.authStatus = '已拒绝'; // 测试拒绝状态
    this.authStatus = '已认证'; // 测试已认证状态
    this.loadVerifiedData();
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
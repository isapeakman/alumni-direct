<template>
  <div class="personal-center">
    <el-card class="box-card" shadow="hover">
      <!-- 头像设置 -->
      <div class="user-avatar">
        <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
        >
          <img v-if="avatarUrl" :src="avatarUrl" class="avatar"/>
          <el-icon v-else class="avatar-placeholder">
            <Plus/>
          </el-icon>
        </el-upload>
        <el-button type="primary" @click="saveAvatar">保存头像</el-button>
      </div>

      <!-- 基本信息 -->
      <el-form ref="basicForm" :model="formData" :rules="rules" label-width="120px">
        <h3>基本信息</h3>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称"/>
        </el-form-item>
        <el-form-item label="账户" prop="account">
          <el-input v-model="formData.account" disabled placeholder="账户不可修改"/>
        </el-form-item>
        <el-form-item label="密码修改" prop="password">
          <el-input
              v-model="formData.password"
              type="password"
              placeholder="新密码（留空则不修改）"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="formData.confirmPassword" type="password"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveBasicInfo">保存信息</el-button>
        </el-form-item>
      </el-form>

      <!-- 求职信息 -->
      <el-form ref="jobForm" :model="formData" label-width="120px">
        <h3>求职意愿</h3>
        <el-form-item label="学历" prop="education">
          <el-select v-model="formData.education" placeholder="请选择学历">
            <el-option label="高中及以下" value="high_school"/>
            <el-option label="大专" value="college"/>
            <el-option label="本科" value="bachelor"/>
            <el-option label="硕士" value="master"/>
            <el-option label="博士" value="phd"/>
          </el-select>
        </el-form-item>
        <el-form-item label="职位类型" prop="jobType">
          <el-radio-group v-model="formData.jobType">
            <el-radio :label="0">全职</el-radio>
            <el-radio :label="1">实习</el-radio>
            <el-radio :label="2">兼职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="薪资范围" prop="salaryRange">
          <el-input-number
              v-model="formData.minSalary"
              :min="0"
              :max="100000"
              placeholder="最低薪资"
              style="width: 48%"
          />
          <el-input-number
              v-model="formData.maxSalary"
              :min="0"
              :max="100000"
              placeholder="最高薪资"
              style="width: 48%; margin-left: 4%"
          />
        </el-form-item>
        <el-form-item label="职位分类" prop="categories">
          <el-select
              v-model="formData.categories"
              multiple
              placeholder="请选择职位分类"
          >
            <el-option
                v-for="category in jobCategories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveJobInfo">保存求职信息</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue';
import {ElMessage} from 'element-plus';
import {Plus} from '@element-plus/icons-vue';

const avatarUrl = ref(''); // 初始头像
const uploadUrl = '/api/upload'; // 模拟上传地址
const jobCategories = ref([ // 模拟职位分类数据
  {id: 1, name: '技术类'},
  {id: 2, name: '运营类'},
  {id: 3, name: '设计类'},
]);

const formData = reactive({
  nickname: '',
  account: 'user@example.com',
  password: '',
  confirmPassword: '',
  education: 'bachelor',
  jobType: 0,
  minSalary: 5000,
  maxSalary: 10000,
  categories: [1, 3],
});

const rules = {
  nickname: [
    {required: true, message: '请输入昵称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在2到20个字符', trigger: 'blur'}
  ],
  password: [
    {
      pattern: /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,20}$/,
      message: '密码需包含数字、字母和特殊字符，长度8-20位',
      trigger: 'blur'
    },
  ],
  confirmPassword: [
    {
      validator: (rule, value) => {
        return value === formData.password || '两次输入的密码不一致';
      }, trigger: 'blur'
    }
  ]
};

// 头像上传处理
const beforeAvatarUpload = (rawFile) => {
  const isJPGOrPNG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png';
  if (!isJPGOrPNG) {
    ElMessage.error('仅支持 JPG/PNG 格式');
    return false;
  }
  const isLt2M = rawFile.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB');
    return false;
  }
  return true;
};

const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === 200) {
    avatarUrl.value = response.data.url;
    ElMessage.success('头像上传成功！');
  } else {
    ElMessage.error('上传失败：' + response.message);
  }
};

// 保存方法
const saveBasicInfo = async () => {
  try {
    await basicForm.value.validate();
    // 模拟提交
    ElMessage.success('基本信息已保存');
  } catch (error) {
    console.log('表单验证失败:', error);
  }
};

const saveJobInfo = () => {
  ElMessage.success('求职信息已保存');
};
</script>

<style scoped lang="scss">
.personal-center {
  padding: 20px;

  .box-card {
    width: 80%;
    margin: 0 auto;

    .user-avatar {
      text-align: center;
      margin-bottom: 20px;

      .avatar-uploader {
        width: 150px;
        height: 150px;
        border: 1px dashed #d9d9d9;
        border-radius: 50%;

        .avatar {
          width: 100%;
          height: 100%;
          border-radius: 50%;
        }

        .avatar-placeholder {
          font-size: 40px;
          line-height: 150px;
        }
      }
    }

    h3 {
      margin: 20px 0;
      border-bottom: 1px solid #eee;
      padding-bottom: 10px;
    }
  }
}
</style>

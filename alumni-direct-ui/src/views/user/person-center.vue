<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="250px">
        <div class="sidebar">
          <div class="sidebar-header">
            <el-avatar :size="48" :src="avatarUrl" class="sidebar-avatar"/>
            <div class="sidebar-user">
              <span class="user-name">{{ formData.nickname || '用户' }}</span>
              <span class="user-role">个人中心</span>
            </div>
          </div>
          <el-menu
              class="sidebar-el-menu"
              :default-active="onRoutes"
              :background-color="sidebar.bgColor"
              :text-color="sidebar.textColor"
              router
          >
            <template v-for="item in menuData">
              <template>
                <el-menu-item :index="item.index" :key="item.index">
                  <el-icon>
                    <component :is="item.icon"></component>
                  </el-icon>
                  <template #title>{{ item.title }}</template>
                </el-menu-item>
              </template>
            </template>
          </el-menu>
        </div>
      </el-aside>
      <el-main>
        <div class="personal-center">
          <!-- 页面标题 -->
          <div class="page-header">
            <h1 class="page-title">个人中心</h1>
            <p class="page-subtitle">管理您的个人信息和求职意愿</p>
          </div>

          <!-- 基本信息卡片 -->
          <div class="info-section">
            <div class="section-header">
              <h2 class="section-title">基本信息</h2>
              <span class="section-icon">
                <el-icon><User/></el-icon>
              </span>
            </div>
            <div class="info-card">
              <div class="avatar-section">
                <div class="avatar-wrapper">
                  <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :on-success="handleAvatarSuccess"
                      :before-upload="beforeAvatarUpload"
                      :http-request="customUpload"
                  >
                    <img v-if="avatarUrl" :src="avatarUrl" class="avatar"/>
                    <el-icon v-else class="avatar-placeholder">
                      <UserPlus/>
                    </el-icon>
                  </el-upload>
                  <span class="upload-hint">点击更换头像</span>
                </div>
                <el-button type="primary" size="small" @click="saveAvatar" class="save-avatar-btn">
                  <el-icon>
                    <Save/>
                  </el-icon>
                  保存头像
                </el-button>
              </div>

              <div class="form-section">
                <el-form ref="basicForm" :model="formData" :rules="rules" label-width="110px">
                  <div class="form-row">
                    <el-form-item label="昵称" prop="nickname">
                      <el-input
                          v-model="formData.nickname"
                          placeholder="请输入昵称"
                          class="form-input"
                      />
                    </el-form-item>
                    <el-form-item label="账户" prop="account">
                      <el-input
                          v-model="formData.userAccount"
                          disabled
                          placeholder="账户不可修改"
                          class="form-input"
                      />
                    </el-form-item>
                  </div>
                  <div class="form-row">
                    <el-form-item label="生日" prop="birthday">
                      <el-date-picker
                          v-model="formData.birth"
                          type="date"
                          placeholder="选择日期"
                          class="form-input"
                      />
                    </el-form-item>
                    <el-form-item label="性别" prop="gender">
                      <el-select
                          v-model="formData.gender"
                          placeholder="请选择性别"
                          class="form-input"
                      >
                        <el-option label="男" value="0"/>
                        <el-option label="女" value="1"/>
                        <el-option label="未知" value="2"/>
                      </el-select>
                    </el-form-item>
                  </div>
                  <el-form-item label="自我介绍" prop="selfIntroduction">
                    <el-input
                        v-model="formData.introduction"
                        type="textarea"
                        :rows="4"
                        placeholder="请输入自我介绍"
                        class="form-textarea"
                    />
                  </el-form-item>
                  <el-form-item class="form-actions">
                    <el-button type="primary" @click="saveBasicInfo" class="submit-btn">
                      <el-icon>
                        <Save/>
                      </el-icon>
                      保存个人信息
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>
            </div>
          </div>

          <!-- 求职信息卡片 -->
          <div class="info-section">
            <div class="section-header">
              <h2 class="section-title">求职意愿</h2>
              <span class="section-icon">
                <el-icon><Briefcase/></el-icon>
              </span>
            </div>
            <div class="info-card">
              <el-form ref="jobForm" :model="formData" label-width="110px">
                <div class="form-row">
                  <el-form-item label="职位类型" prop="type">
                    <el-radio-group v-model="formData.type" class="radio-group">
                      <el-radio :label="0" class="radio-item">全职</el-radio>
                      <el-radio :label="1" class="radio-item">实习</el-radio>
                      <el-radio :label="2" class="radio-item">兼职</el-radio>
                    </el-radio-group>
                  </el-form-item>
                </div>
                <el-form-item label="薪资范围(单位:K)" prop="salaryRange">
                  <div class="salary-input">
                    <el-input-number
                        v-model="formData.minSalary"
                        :min="0"
                        :max="100000"
                        placeholder="最低薪资"
                        class="salary-item"
                    />
                    <span class="salary-separator">-</span>
                    <el-input-number
                        v-model="formData.maxSalary"
                        :min="0"
                        :max="100000"
                        placeholder="最高薪资"
                        class="salary-item"
                    />
                    <span class="salary-unit">K</span>
                  </div>
                </el-form-item>
                <el-form-item label="职位分类" prop="categoryId">
                  <el-tree
                      ref="treeRef"
                      :data="jobCategories"
                      show-checkbox
                      node-key="id"
                      :props="defaultProps"
                      @check="handleCategoryCheck"
                      :default-checked-keys="formData.categoryId"
                      class="category-tree"
                  />
                </el-form-item>
                <el-form-item class="form-actions">
                  <el-button type="primary" @click="saveJobInfo" class="submit-btn">
                    <el-icon>
                      <Save/>
                    </el-icon>
                    保存求职信息
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import {ElMessage} from 'element-plus';
import {Plus, User, UserPlus, Save, Briefcase} from '@element-plus/icons-vue';
import {useRoute} from "vue-router";
import {useSidebarStore} from '../../store/sidebar';
import {getIntention, getUserInfo, saveIntention, update, updateAvatar} from "@/api/user.js";
import {uploadFile} from "@/api/file.js";
import {getCategories} from "@/api/job.js"; // 引入文件上传方法

const avatarUrl = ref('http://localhost:8080/ad/static/defaultAvator.jpg'); // 初始头像
const uploadUrl = 'localhost:8080/ad/file/upload'; // 模拟上传地址
const jobCategories = ref([]); // 职位分类数据
const sidebar = useSidebarStore();
const formData = ref({
  nickname: '',
  userAccount: 'user@example.com',
  birth: null, // 生日字段
  gender: '2',     // 确保性别字段是字符串类型
  userAvatar: '',
  createTime: null,
  introduction: '',
  minSalary: null, // 添加 minSalary 属性
  maxSalary: null, // 添加 maxSalary 属性
  type: null,   // 添加 Type 属性
  categoryId: [],  // 添加 categories 属性
});
const treeRef = ref(null);

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
  birthday: [
    {required: true, message: '请选择出生日期', trigger: 'change'}
  ],
  gender: [
    {required: true, message: '请选择性别', trigger: 'change'}
  ],
  confirmPassword: [
    {
      validator: (rule, value) => {
        return value === formData.password || '两次输入的密码不一致';
      }, trigger: 'blur'
    }
  ]
};

const route = useRoute();
const onRoutes = computed(() => {
  return route.path;
});

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
    avatarUrl.value = response.data;
    formData.value.userAvatar = response.data; // 将上传地址存储到 formData
    ElMessage.success('头像上传成功！');
  } else {
    ElMessage.error('上传失败：' + response.message);
  }
};

// 自定义上传逻辑
const customUpload = async (options) => {
  try {
    const formData = new FormData();
    formData.append('file', options.file); // 将文件添加到 FormData 中
    const response = await uploadFile(formData); // 调用文件上传接口
    if (response.data.code === 200) {
      options.onSuccess(response.data); // 上传成功回调
    } else {
      options.onError(response.data.message); // 上传失败回调
    }
  } catch (error) {
    options.onError('上传失败，请稍后重试'); // 捕获异常并触发失败回调
  }
};

// 保存头像
const saveAvatar = async () => {
  try {
    console.log('保存头像:', formData.value.userAvatar);
    const response = await updateAvatar({userAvatar: formData.value.userAvatar});
    if (response.data.code === 200) {
      ElMessage.success('头像保存成功');
      await fetchUserBasicInfo(); // 刷新用户信息
    } else {
      ElMessage.error('保存失败：' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('保存失败，请稍后重试');
  }
};

// 保存用户基本信息
const saveBasicInfo = async () => {
  try {
    // 模拟提交
    const response = await update(formData.value);
    if (response.data.code !== 200) {
      ElMessage.error('保存失败：' + response.data.message);
      return;
    }
    ElMessage.success('基本信息已保存');
    await fetchUserBasicInfo();
  } catch (error) {
    console.log('表单验证失败:', error);
  }
};

// 获取求职意愿
const fetchJobInfo = async () => {
  const response = await getIntention();
  if (response.data.code !== 200) {
    ElMessage.error('获取求职意愿失败');
    return;
  }
  const intention = response.data.data[0]; // 假设返回的是一个数组，取第一个元素
  console.log('求职意愿:', intention);
  formData.value = {
    ...formData.value,
    ...intention,
    categoryId: intention.categoryId || [] // 确保 categoryId 是数组
  };
};

// 保存求职意愿
const saveJobInfo = async () => {
  const response = await saveIntention(formData.value);
  if (response.data.code !== 200) {
    ElMessage.error('保存求职意愿失败');
    return;
  }
  ElMessage.success('求职意愿已保存');
  await fetchJobInfo();
};

// 获取用户基本信息
const fetchUserBasicInfo = async () => {
  const response = await getUserInfo();
  if (response.data.code !== 200) {
    ElMessage.error('获取用户信息失败');
    return;
  }
  formData.value = {...formData.value, ...response.data.data};
  // 确保 gender 是字符串类型
  formData.value.gender = formData.value.gender.toString();
  // 头像地址
  avatarUrl.value = response.data.data.userAvatar;
  console.log('用户基本信息:', formData);
};

// 获取职位分类
const fetchCategories = async () => {
  const response = await getCategories();
  // 格式化数据，确保每个节点包含 id 和 name 字段
  jobCategories.value = response.data.data.map(category => ({
    id: category.id,
    categoryName: category.categoryName,
    children: category.children || []
  }));
  console.log('职位分类数据:', jobCategories.value);
};

const defaultProps = {
  children: 'children',
  label: 'categoryName',
};

const handleCategoryCheck = (checkedNodes) => {
  console.log('选中的节点:', checkedNodes);
  const tree = treeRef.value.getCheckedNodes();
  console.log('选中的节点:', tree);
  if (Array.isArray(tree) && tree.length > 0) {
    formData.value.categoryId = tree.map(node => node.id);
  } else {
    formData.value.categoryId = [];
  }
};

onMounted(() => {
  fetchUserBasicInfo();
  fetchJobInfo();
  fetchCategories();
});
</script>

<style scoped lang="scss">
.personal-center {
  padding: 30px;
  min-height: calc(100vh - 60px);

  .page-header {
    margin-bottom: 32px;

    .page-title {
      font-size: 28px;
      font-weight: 600;
      color: #1e293b;
      margin: 0 0 8px;
    }

    .page-subtitle {
      font-size: 14px;
      color: #64748b;
      margin: 0;
    }
  }
}

/* 侧边栏样式 */
.sidebar {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: linear-gradient(180deg, #0ea5e9 0%, #0284c7 100%);

  .sidebar-header {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);

    .sidebar-avatar {
      border: 2px solid rgba(255, 255, 255, 0.3);
    }

    .sidebar-user {
      display: flex;
      flex-direction: column;

      .user-name {
        font-size: 16px;
        font-weight: 600;
        color: #fff;
      }

      .user-role {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.7);
      }
    }
  }
}

.sidebar-el-menu {
  flex: 1;
  border-right: none;
  background: transparent;

  ::v-deep(.el-menu-item) {
    color: rgba(255, 255, 255, 0.8);
    border-radius: 0 8px 8px 0;
    margin: 4px 0;

    &:hover {
      background: rgba(255, 255, 255, 0.1);
      color: #fff;
    }

    &.is-active {
      background: rgba(255, 255, 255, 0.2);
      color: #fff;
    }
  }
}

.el-aside {
  width: 250px;
}

/* 信息区域 */
.info-section {
  margin-bottom: 24px;

  .section-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 16px;

    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
    }

    .section-icon {
      width: 36px;
      height: 36px;
      background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%);
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 18px;
    }
  }

  .info-card {
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
    padding: 24px;
    display: flex;
    gap: 32px;

    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding-right: 24px;
      border-right: 1px solid #f1f5f9;

      .avatar-wrapper {
        position: relative;
        margin-bottom: 12px;

        .avatar-uploader {
          width: 120px;
          height: 120px;
          border-radius: 50%;
          border: 2px dashed #cbd5e1;
          cursor: pointer;
          transition: all 0.3s;
          display: flex;
          align-items: center;
          justify-content: center;

          &:hover {
            border-color: #0ea5e9;
          }

          .avatar {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            object-fit: cover;
          }

          .avatar-placeholder {
            font-size: 32px;
            color: #94a3b8;
          }
        }

        .upload-hint {
          display: block;
          font-size: 12px;
          color: #94a3b8;
          text-align: center;
          margin-top: 8px;
        }
      }

      .save-avatar-btn {
        background: rgba(14, 165, 233, 0.1);
        color: #0ea5e9;
        border-color: transparent;
        padding: 8px 20px;

        &:hover:not(:disabled) {
          background: rgba(14, 165, 233, 0.2);
        }
      }
    }

    .form-section {
      flex: 1;
    }
  }
}

/* 表单样式 */
.form-row {
  display: flex;
  gap: 16px;
}

.form-input {
  width: 100%;
}

.form-textarea {
  width: 100%;
}

.form-actions {
  padding-left: 110px;

  .submit-btn {
    background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%);
    border: none;
    padding: 10px 28px;
    border-radius: 8px;

    &:hover:not(:disabled) {
      opacity: 0.9;
    }
  }
}

/* 单选按钮组 */
.radio-group {
  display: flex;
  gap: 24px;

  .radio-item {
    font-size: 14px;
    color: #334155;
  }
}

/* 薪资输入 */
.salary-input {
  display: flex;
  align-items: center;
  gap: 8px;

  .salary-item {
    width: 120px;
  }

  .salary-separator {
    color: #94a3b8;
  }

  .salary-unit {
    color: #64748b;
    font-size: 14px;
  }
}

/* 分类树 */
.category-tree {
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;

  ::v-deep(.el-tree-node) {
    margin-bottom: 4px;
  }

  ::v-deep(.el-tree-node__content) {
    padding: 6px 8px;
    border-radius: 6px;

    &:hover {
      background: rgba(14, 165, 233, 0.08);
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .personal-center {
    padding: 20px;
  }

  .info-card {
    flex-direction: column !important;
    gap: 20px !important;

    .avatar-section {
      padding-right: 0 !important;
      border-right: none !important;
      border-bottom: 1px solid #f1f5f9;
      padding-bottom: 20px;
    }
  }

  .form-row {
    flex-direction: column;
  }

  .form-actions {
    padding-left: 0;
  }

  .el-aside {
    width: 100%;
  }
}
</style>

<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <div class="sidebar">
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
          <el-card class="box-card" shadow="hover">
            <!-- 头像与基本信息左右布局 -->
            <el-row :gutter="20">
              <!-- 左侧表单项 -->
              <el-col :span="12">
                <el-form ref="basicForm" :model="formData" :rules="rules" label-width="120px">
                  <h3>基本信息</h3>
                  <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="formData.nickname" placeholder="请输入昵称"/>
                  </el-form-item>
                  <el-form-item label="账户" prop="account">
                    <el-input v-model="formData.userAccount" disabled placeholder="账户不可修改"/>
                  </el-form-item>
                  <el-form-item label="生日" prop="birthday">
                    <el-date-picker v-model="formData.birth" type="date" placeholder="选择日期"/>
                  </el-form-item>
                  <el-form-item label="性别" prop="gender">
                    <el-select v-model="formData.gender" placeholder="请选择性别">
                      <el-option label="男" value="0"/>
                      <el-option label="女" value="1"/>
                      <el-option label="未知" value="2"/>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="自我介绍" prop="selfIntroduction">
                    <el-input
                        v-model="formData.introduction"
                        type="textarea"
                        :rows="4"
                        placeholder="请输入自我介绍"
                    />
                  </el-form-item>
                </el-form>
                <el-button type="primary" @click="saveBasicInfo">保存个人信息</el-button>
              </el-col>

              <!-- 右侧头像 -->
              <el-col :span="4" class="avatar-container">
                <div class="user-avatar">
                  <el-upload
                      class="avatar-uploader"
                      :show-file-list="false"
                      :on-success="handleAvatarSuccess"
                      :before-upload="beforeAvatarUpload"
                      :http-request="customUpload"
                  >
                    <img v-if="avatarUrl" :src="avatarUrl" class="avatar"/>
                    <el-icon v-else class="avatar-placeholder">
                      <Plus/>
                    </el-icon>
                  </el-upload>
                  <el-button type="primary" @click="saveAvatar">保存头像</el-button>
                </div>
              </el-col>
            </el-row>

            <!-- 求职信息 -->
            <el-form ref="jobForm" :model="formData" label-width="120px">
              <h3>求职意愿</h3>
              <el-form-item label="职位类型" prop="type">
                <el-radio-group v-model="formData.type">
                  <el-radio :label="0">全职</el-radio>
                  <el-radio :label="1">实习</el-radio>
                  <el-radio :label="2">兼职</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="薪资范围(单位:K)" prop="salaryRange">
                <el-input-number
                    v-model="formData.minSalary"
                    :min="0"
                    :max="100000"
                    placeholder="最低薪资"
                    style="width: 38%"
                />
                <el-input-number
                    v-model="formData.maxSalary"
                    :min="0"
                    :max="100000"
                    placeholder="最高薪资"
                    style="width: 38%; margin-left: 4%"
                />
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
                    class="horizontal-tree"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveJobInfo">保存求职信息</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue';
import {ElMessage} from 'element-plus';
import {Plus} from '@element-plus/icons-vue';
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
  padding: 20px;

  .box-card {
    width: 80%;
    margin: 0 auto;

    .user-avatar {
      text-align: center;
      margin-bottom: 20px;

      .avatar-uploader {
        width: 100px;
        height: 100px;
        border: 1px dashed #0f49f6;
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

.sidebar {
  display: block;
  width: 200px; // 显式设置宽度
  overflow-y: auto;
}

.sidebar::-webkit-scrollbar {
  width: 0;
}

.el-aside {
  width: 250px; // ← 与 el-menu 的宽度一致
}

.sidebar-el-menu:not(.el-menu--collapse) {
  width: 250px;
}

.sidebar-el-menu {
  min-height: 100%;
}

/* 水平树形结构样式 */
.horizontal-tree {
  /* 强制所有节点水平排列 */
  .el-tree-node {
    display: inline-flex !important;
    margin-right: 15px;
    vertical-align: top;
  }

  /* 子节点水平排列 + 换行 */
  .el-tree-node__children {
    display: inline-flex !important;
    flex-wrap: wrap;
    margin-left: 0 !important;
  }

  /* 调整复选框样式 */
  .el-checkbox {
    margin-right: 5px;
  }
}
</style>

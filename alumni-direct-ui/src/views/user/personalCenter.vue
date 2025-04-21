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
                    <el-input v-model="formData.account" disabled placeholder="账户不可修改"/>
                  </el-form-item>
                  <el-form-item label="生日" prop="birthday">
                    <el-date-picker v-model="formData.birthday" type="date" placeholder="选择日期"/>
                  </el-form-item>
                  <el-form-item label="性别" prop="gender">
                    <el-select v-model="formData.gender" placeholder="请选择性别">
                      <el-option label="男" value="male"/>
                      <el-option label="女" value="female"/>
                      <el-option label="其他" value="other"/>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="自我介绍" prop="selfIntroduction">
                    <el-input
                        v-model="formData.selfIntroduction"
                        type="textarea"
                        :rows="4"
                        placeholder="请输入自我介绍"
                    />
                  </el-form-item>
                </el-form>
                <el-button type="primary" @click="saveJobInfo">保存个人信息</el-button>
              </el-col>

              <!-- 右侧头像 -->
              <el-col :span="4" class="avatar-container">
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
              </el-col>
            </el-row>

            <!-- 求职信息 -->
            <el-form ref="jobForm" :model="formData" label-width="120px">
              <h3>求职意愿</h3>
              <!--              <el-form-item label="学历" prop="education">-->
              <!--                <el-select v-model="formData.education" placeholder="请选择学历">-->
              <!--                  <el-option label="高中及以下" value="high_school"/>-->
              <!--                  <el-option label="大专" value="college"/>-->
              <!--                  <el-option label="本科" value="bachelor"/>-->
              <!--                  <el-option label="硕士" value="master"/>-->
              <!--                  <el-option label="博士" value="phd"/>-->
              <!--                </el-select>-->
              <!--              </el-form-item>-->
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
      </el-main>
    </el-container>
  </div>

</template>

<script setup>
import {ref, reactive, computed} from 'vue';
import {ElMessage} from 'element-plus';
import {Plus} from '@element-plus/icons-vue';
import {useRoute} from "vue-router";
import {useSidebarStore} from '../../store/sidebar';

const avatarUrl = ref('http://localhost:8080/ad/static/defaultAvator.jpg'); // 初始头像
const uploadUrl = 'localhost:8080/ad/user/update/avatar'; // 模拟上传地址
const jobCategories = ref([ // 模拟职位分类数据
  {id: 1, name: '算法'},
  {id: 2, name: '销售'},
  {id: 3, name: '设计类'},
]);
const sidebar = useSidebarStore();
const formData = reactive({
  nickname: '',
  account: 'user@example.com',
  password: '',
  confirmPassword: '',
  education: 'bachelor',
  jobType: 0,
  birthday: null, // 生日字段
  gender: '',     // 性别字段
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
const menuData = reactive([
  {
    title: '用户管理',
    index: '1',
    id: 1,
  },
  {
    title: '职位审批',
    index: '2',
    id: 2,
  },
  {
    title: '消息管理',
    index: '3',
    id: 3,
  }
])
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
  /* 移除以下定位属性 */
  // position: absolute;
  // left: 0;
  // top: 70px;
  // bottom: 0;
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
</style>

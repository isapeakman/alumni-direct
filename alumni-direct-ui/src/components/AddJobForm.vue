<template>
  <div class="add-job-form">
    <el-form :model="form" label-width="100px">
      <!-- 新增公司名称字段 -->
      <el-form-item label="公司名称">
        <el-input v-model="form.companyName" placeholder="请输入公司名称"/>
      </el-form-item>
      <el-form-item label="职位名称">
        <el-input v-model="form.title" placeholder="请输入职位名称"/>
      </el-form-item>
      <el-form-item label="职位性质">
        <el-select v-model="form.jobType" placeholder="请选择职位类型">
          <el-option label="全职" :value="0"/>
          <el-option label="实习" :value="1"/>
          <el-option label="兼职" :value="2"/>
        </el-select>
      </el-form-item>
      <el-form-item label="职位分类">
        <el-tree
            ref="treeRef"
            :data="jobCategories"
            show-checkbox
            node-key="id"
            :props="defaultProps"
            @check="handleCategoryCheck"
        />
      </el-form-item>
      <el-form-item label="工作地点">
        <el-input v-model="form.location" placeholder="请输入工作地点"/>
      </el-form-item>
      <el-form-item label="薪资范围">
        <el-input v-model="form.minSalary" placeholder="最低薪资" style="width: 48%;"/>
        <el-input v-model="form.maxSalary" placeholder="最高薪资" style="width: 48%; margin-left: 4%;"/>
      </el-form-item>
      <el-form-item label="职位描述">
        <el-input
            v-model="form.jobDesc"
            type="textarea"
            :rows="4"
            placeholder="请输入职位描述"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import {getCategories} from "@/api/job.js";

const emit = defineEmits(['submit', 'cancel']);
const jobCategories = ref([]);
const form = ref({
  companyName: '', // 新增公司名称字段
  title: '',
  jobType: null,
  location: '',
  minSalary: '',
  maxSalary: '',
  jobDesc: '',
  categoryIds: [],
});
const defaultProps = {
  children: 'children',
  label: 'categoryName',
}
let treeRef = ref(null);
onMounted(() => {
  fetchCategories();
})
// 获取职位分类
const fetchCategories = (async () => {
  const response = await getCategories();
  jobCategories.value = response.data.data;
});
// 处理职位分类选择
const handleCategoryCheck = (checkedNodes) => {
  console.log('选中的节点:', checkedNodes);
  const tree = treeRef.value.getCheckedNodes()
  if (Array.isArray(tree) && tree.length > 0) {
    // 将选中的节点 ID 存储到 categoryIds 中
    form.value.categoryIds = tree.map(node => node.id)
    console.log('选中的分类 ID:', form.value.categoryIds);
  } else {
    form.value.categoryIds = []; // 如果没有选中任何分类，设置为空数组
  }
};
// 提交表单
const handleSubmit = () => {
  emit('submit', form.value); // 将表单数据传递给父组件
  resetForm(); // 重置表单
};

// 取消表单
const handleCancel = () => {
  emit('cancel'); // 通知父组件取消操作
  resetForm(); // 重置表单
};

// 重置表单
const resetForm = () => {
  form.value = {
    title: '',
    jobType: null,
    location: '',
    minSalary: '',
    maxSalary: '',
    jobDesc: '',
    categoryIds: [],
    companyName: '', // 重置公司名称字段
  };
};
</script>

<style scoped>
.add-job-form {
  margin-top: 20px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fafafa;
}

.el-tree {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
}
</style>
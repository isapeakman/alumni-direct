<template>
  <el-dialog
      v-model="dialogVisible"
      :title="isEditMode ? '修改职位' : '职位详情'"
      width="70%"
      :before-close="handleClose"
  >
    <el-form :model="form" label-width="100px">
      <el-form-item label="职位名称">
        <el-input v-model="form.title" :disabled="!isEditMode"/>
      </el-form-item>
      <el-form-item label="职位分类">
        <el-tree
            :data="jobCategories"
            show-checkbox
            node-key="id"
            :props="defaultProps"
            @check="handleCategoryCheck"
            :class="{ 'disabled-tree': !isEditMode }"
            :default-checked-keys="form.categoryIds"
        />
      </el-form-item>
      <el-form-item label="职位类型">
        <el-select v-model="form.jobType" :disabled="!isEditMode">
          <el-option label="全职" :value="0"/>
          <el-option label="实习" :value="1"/>
          <el-option label="兼职" :value="2"/>
        </el-select>
      </el-form-item>
      <el-form-item label="工作地点">
        <el-input v-model="form.location" :disabled="!isEditMode"/>
      </el-form-item>
      <el-form-item label="薪资范围">
        <el-input v-model="form.minSalary" :disabled="!isEditMode" style="width: 48%;"/>
        <el-input v-model="form.maxSalary" :disabled="!isEditMode" style="width: 48%; margin-left: 4%;"/>
      </el-form-item>
      <el-form-item label="职位描述">
        <el-input
            v-model="form.jobDesc"
            type="textarea"
            :rows="8"
            :disabled="!isEditMode"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <!--      处于待发布/关闭的状态才可以发布-->
      <el-button v-if="!isEditMode&&(form.status===1||form.status===3)" type="success" @click="publishJob">发布
      </el-button>
      <!--      处于待发布 或者 审核未通过才可以修改-->
      <el-button v-if="!isEditMode&&(form.status===1||form.status===4)" type="primary" @click="enableEditMode">修改
      </el-button>
      <el-button v-if="isEditMode" type="primary" @click="handleSave">保存</el-button>
      <el-button v-if="!isEditMode&&(form.status===2)" type="info" @click="closeJob">关闭职位</el-button>
      <el-button v-if="!isEditMode&&(form.status===2)" type="warning" @click="cancelPublish">取消发布</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {onMounted, ref, watch} from 'vue';
import {cancel, closeJobById, getCategories, publish, update} from "@/api/job.js";
import {ElMessage} from "element-plus";

const props = defineProps({
  job: {
    type: Object,
    required: true,
  },
  jobCategories: {
    type: Array,
    required: false,
  },
});

const emit = defineEmits(['save']);

const dialogVisible = ref(false);
const isEditMode = ref(false);
const form = ref({...props.job});
const jobCategories = ref([]);
const defaultProps = {
  children: 'children',
  label: 'categoryName',
};
const getJobType = (type) => {
  const typeMap = {
    0: '全职',
    1: '实习',
    2: '兼职'
  }
  return typeMap[type] || '未知'
}
// 加载职位分类
onMounted(() => {
  fetchCategories();
});
// 获取职位分类
const fetchCategories = (async () => {
  const response = await getCategories();
  jobCategories.value = response.data.data;
});
// 监听选中职位
watch(() => props.job, (newVal) => {
  form.value = {...newVal};
}, {immediate: true});
// 打开弹窗
const open = () => {
  dialogVisible.value = true;
  isEditMode.value = false; // 默认非编辑模式
  form.value = {...props.job}; // 确保使用最新的props.job
};

// 关闭弹窗
const handleClose = () => {
  dialogVisible.value = false;
};
// 发布职位
const publishJob = async () => {
  console.log('发布的职位id:', form.value.id);
  const response = await publish(form.value.id);
  if (response.data.code === 200) {
    dialogVisible.value = false;
    ElMessage.success('发布成功');
    // 刷新列表
    emit('refresh');
  } else {
    ElMessage.error(response.message || '发布失败');
  }
};
// 进入编辑模式
const enableEditMode = () => {
  isEditMode.value = true;
};

// 保存修改
const handleSave = async () => {
  // emit('save', form.value); // 触发保存事件
  console.log('提交的职位数据：', form.value);
  // 将数组转换为逗号分隔字符串
  const payload = {
    ...form.value,
    categoryIds: form.value.categoryIds.join(",")
  };
  const response = await update(payload);
  if (response.data.code === 200) {
    dialogVisible.value = false;
    ElMessage.success('保存成功');
    // 刷新列表
    emit('refresh');
  } else {
    ElMessage.error(response.message || '保存失败');
  }
};
// 关闭职位
const closeJob = async () => {
  console.log('停止招聘的职位id:', form.value.id);
  const response = await closeJobById(form.value.id);
  if (response.data.code === 200) {
    dialogVisible.value = false;
    ElMessage.success('停止招聘成功');
    // 刷新列表
    emit('refresh');
  } else {
    ElMessage.error(response.message || '停止招聘失败');
  }
};
// 取消发布
const cancelPublish = async () => {
  console.log('取消发布的职位id:', form.value.id);
  const response = await cancel(form.value.id);
  if (response.data.code === 200) {
    dialogVisible.value = false;
    ElMessage.success('取消发布成功');
    // 刷新列表
    emit('refresh');
  } else {
    ElMessage.error(response.message || '取消发布失败');
  }
};

// 处理分类选择
const handleCategoryCheck = (checkedNodes) => {
  if (Array.isArray(checkedNodes)) {
    form.value.categoryIds = checkedNodes.map(node => node.id);
  } else {
    form.value.categoryIds = [];
  }
};

defineExpose({open});
</script>

<style scoped>
.disabled-tree :deep(.el-checkbox) {
  pointer-events: none; /* 禁用复选框的点击事件 */
  opacity: 0.6; /* 降低复选框的透明度 */
}
</style>
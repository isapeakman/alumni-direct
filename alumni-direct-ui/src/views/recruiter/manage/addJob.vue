<template>
  <div class="create-job-container">
    <h2>创建职位</h2>
    <form @submit.prevent="handleSubmit">
      <div>
        <label for="title">职位标题:</label>
        <input type="text" id="title" v-model="formData.title" required/>
      </div>
      <div>
        <label for="jobType">职位类型:</label>
        <input type="number" id="jobType" v-model="formData.jobType" required/>
      </div>
      <div>
        <label for="jobDesc">职位描述:</label>
        <textarea id="jobDesc" v-model="formData.jobDesc" required></textarea>
      </div>
      <div>
        <label for="location">工作地点:</label>
        <input type="text" id="location" v-model="formData.location" required/>
      </div>
      <div>
        <label for="minSalary">最低薪资:</label>
        <input type="number" id="minSalary" v-model="formData.minSalary" required/>
      </div>
      <div>
        <label for="maxSalary">最高薪资:</label>
        <input type="number" id="maxSalary" v-model="formData.maxSalary" required/>
      </div>
      <div>
        <label for="companyName">公司名称:</label>
        <input type="text" id="companyName" v-model="formData.companyName" required/>
      </div>
      <button type="submit">创建职位</button>
    </form>
  </div>
</template>

<script setup>
import {ref} from 'vue';
import axios from 'axios';

const formData = ref({
  title: '',
  jobType: 0,
  jobDesc: '',
  location: '',
  minSalary: 0,
  maxSalary: 0,
  companyName: ''
});

const handleSubmit = async () => {
  try {
    const response = await axios.post('/ad/job/add', formData.value);
    console.log('职位创建成功:', response.data);
    // 重置表单
    formData.value = {
      title: '',
      jobType: 0,
      jobDesc: '',
      location: '',
      minSalary: 0,
      maxSalary: 0,
      companyName: ''
    };
  } catch (error) {
    console.error('职位创建失败:', error);
  }
};
</script>

<style scoped>
.create-job-container {
  position: fixed;
  right: 0;
  top: 60px; /* 考虑到顶部导航栏的高度 */
  width: 300px;
  padding: 20px;
  background-color: #f9f9f9;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.create-job-container h2 {
  margin-bottom: 20px;
}

.create-job-container div {
  margin-bottom: 10px;
}

.create-job-container label {
  display: block;
  margin-bottom: 5px;
}

.create-job-container input,
.create-job-container textarea {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}

.create-job-container button {
  width: 100%;
  padding: 10px;
  background-color: #409EFF;
  color: white;
  border: none;
  cursor: pointer;
}

.create-job-container button:hover {
  background-color: #66b1ff;
}
</style>

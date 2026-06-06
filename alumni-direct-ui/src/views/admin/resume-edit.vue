<template>
  <div class="resume-edit-container">
    <el-card class="edit-card">
      <template #header>
        <div class="card-header">
          <span>简历信息编辑</span>
          <el-button type="primary" @click="handleGenerate" :loading="isGenerating">
            生成求职材料
          </el-button>
        </div>
      </template>

      <el-form :model="resumeForm" label-width="100px" class="resume-form">
        <!-- 基础信息 -->
        <el-divider content-position="left">基础信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="姓名">
              <el-input v-model="resumeForm.name" placeholder="请输入姓名"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="手机号">
              <el-input v-model="resumeForm.phone" placeholder="请输入手机号"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="邮箱">
              <el-input v-model="resumeForm.email" placeholder="请输入邮箱"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所在城市">
              <el-input v-model="resumeForm.location" placeholder="请输入城市"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学历">
              <el-input v-model="resumeForm.education" placeholder="如：本科、硕士"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="专业">
              <el-input v-model="resumeForm.major" placeholder="请输入专业"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="工作年限">
              <el-input v-model="resumeForm.experienceYears" placeholder="如：3年"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="期望职位" required>
              <el-input v-model="resumeForm.desiredPosition" placeholder="请输入期望职位"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="期望薪资">
              <el-input v-model="resumeForm.desiredSalary" placeholder="如：15k-20k"/>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 教育经历 -->
        <el-divider content-position="left">教育经历</el-divider>
        <div v-for="(edu, index) in resumeForm.educationExperience" :key="'edu-' + index" class="experience-item">
          <el-row :gutter="20">
            <el-col :span="10">
              <el-form-item label="学校">
                <el-input v-model="edu.school" placeholder="学校名称"/>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="学历">
                <el-input v-model="edu.degree" placeholder="学历"/>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="专业">
                <el-input v-model="edu.major" placeholder="专业"/>
              </el-form-item>
            </el-col>
            <el-col :span="2">
              <el-button type="danger" link @click="removeEducation(index)">删除</el-button>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="10">
              <el-form-item label="时间">
                <el-input v-model="edu.startDate" placeholder="开始时间"/>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="至">
                <el-input v-model="edu.endDate" placeholder="结束时间"/>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <el-button type="primary" plain @click="addEducation" class="add-btn">添加教育经历</el-button>

        <!-- 工作经历 -->
        <el-divider content-position="left">工作经历</el-divider>
        <div v-for="(work, index) in resumeForm.workExperience" :key="'work-' + index" class="experience-item">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="公司">
                <el-input v-model="work.company" placeholder="公司名称"/>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="职位">
                <el-input v-model="work.position" placeholder="职位"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="时间">
                <el-input v-model="work.startDate" placeholder="开始时间"/>
                <span>至</span>
                <el-input v-model="work.endDate" placeholder="结束时间"/>
              </el-form-item>
            </el-col>
            <el-col :span="2">
              <el-button type="danger" link @click="removeWork(index)">删除</el-button>
            </el-col>
          </el-row>
          <el-form-item label="工作描述">
            <el-input v-model="work.description" type="textarea" :rows="2" placeholder="工作内容和成果"/>
          </el-form-item>
        </div>
        <el-button type="primary" plain @click="addWork" class="add-btn">添加工作经历</el-button>

        <!-- 项目经验 -->
        <el-divider content-position="left">项目经验</el-divider>
        <div v-for="(project, index) in resumeForm.projectExperience" :key="'project-' + index" class="experience-item">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="项目名称">
                <el-input v-model="project.projectName" placeholder="项目名称"/>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="角色">
                <el-input v-model="project.role" placeholder="角色"/>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="时间">
                <el-input v-model="project.startDate" placeholder="开始时间"/>
                <span>至</span>
                <el-input v-model="project.endDate" placeholder="结束时间"/>
              </el-form-item>
            </el-col>
            <el-col :span="2">
              <el-button type="danger" link @click="removeProject(index)">删除</el-button>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="技术栈">
                <el-input v-model="project.technologies" placeholder="如：SpringBoot, MySQL"/>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="项目描述">
            <el-input v-model="project.description" type="textarea" :rows="2" placeholder="项目描述"/>
          </el-form-item>
        </div>
        <el-button type="primary" plain @click="addProject" class="add-btn">添加项目经验</el-button>

        <!-- 技能和证书 -->
        <el-divider content-position="left">技能与证书</el-divider>
        <el-form-item label="技能列表">
          <el-select v-model="resumeForm.skills" multiple filterable allow-create default-first-option
                     placeholder="输入技能后按回车添加" style="width: 100%">
            <el-option v-for="skill in resumeForm.skills" :key="skill" :label="skill" :value="skill"/>
          </el-select>
        </el-form-item>

        <el-form-item label="证书列表">
          <el-select v-model="resumeForm.certificates" multiple filterable allow-create default-first-option
                     placeholder="输入证书后按回车添加" style="width: 100%">
            <el-option v-for="cert in resumeForm.certificates" :key="cert" :label="cert" :value="cert"/>
          </el-select>
        </el-form-item>

        <!-- 自我评价 -->
        <el-divider content-position="left">自我评价</el-divider>
        <el-form-item label="自我评价">
          <el-input v-model="resumeForm.selfEvaluation" type="textarea" :rows="4" placeholder="请输入自我评价"/>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 生成结果展示 -->
    <el-dialog v-model="showResultDialog" title="生成的求职材料" width="70%" destroy-on-close>
      <div class="result-content" v-if="generatedResult">
        <pre>{{ generatedResult }}</pre>
      </div>
      <template #footer>
        <el-button @click="showResultDialog = false">关闭</el-button>
        <el-button type="primary" @click="copyResult">复制内容</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {ElMessage} from 'element-plus'
import {generateResumeMaterial} from '@/api/resume'

const props = defineProps({
  resumeData: {
    type: Object,
    default: () => ({})
  }
})

// 表单数据
const resumeForm = reactive({
  name: '',
  phone: '',
  email: '',
  location: '',
  education: '',
  major: '',
  experienceYears: '',
  desiredPosition: '',
  desiredSalary: '',
  educationExperience: [],
  workExperience: [],
  projectExperience: [],
  skills: [],
  certificates: [],
  selfEvaluation: ''
})

// 生成状态
const isGenerating = ref(false)
const showResultDialog = ref(false)
const generatedResult = ref('')

// 初始化表单数据
const initForm = (data) => {
  Object.assign(resumeForm, {
    name: data.name || '',
    phone: data.phone || '',
    email: data.email || '',
    location: data.location || '',
    education: data.education || '',
    major: data.major || '',
    experienceYears: data.experienceYears || '',
    desiredPosition: data.desiredPosition || '',
    desiredSalary: data.desiredSalary || '',
    educationExperience: data.educationExperience || [],
    workExperience: data.workExperience || [],
    projectExperience: data.projectExperience || [],
    skills: data.skills || [],
    certificates: data.certificates || [],
    selfEvaluation: data.selfEvaluation || ''
  })
}

// 添加教育经历
const addEducation = () => {
  resumeForm.educationExperience.push({
    school: '',
    degree: '',
    major: '',
    startDate: '',
    endDate: '',
    description: ''
  })
}

// 删除教育经历
const removeEducation = (index) => {
  resumeForm.educationExperience.splice(index, 1)
}

// 添加工作经历
const addWork = () => {
  resumeForm.workExperience.push({
    company: '',
    position: '',
    startDate: '',
    endDate: '',
    description: ''
  })
}

// 删除工作经历
const removeWork = (index) => {
  resumeForm.workExperience.splice(index, 1)
}

// 添加项目经验
const addProject = () => {
  resumeForm.projectExperience.push({
    projectName: '',
    role: '',
    startDate: '',
    endDate: '',
    description: '',
    technologies: ''
  })
}

// 删除项目经验
const removeProject = (index) => {
  resumeForm.projectExperience.splice(index, 1)
}

// 生成求职材料
const handleGenerate = async () => {
  if (!resumeForm.desiredPosition) {
    ElMessage.warning('请输入期望职位')
    return
  }

  try {
    isGenerating.value = true
    const response = await generateResumeMaterial(resumeForm)

    if (response.data.code === 200) {
      generatedResult.value = response.data.data
      showResultDialog.value = true
      ElMessage.success('生成成功！')
    } else {
      ElMessage.error(response.data.message || '生成失败')
    }
  } catch (error) {
    console.error('生成失败:', error)
    ElMessage.error('生成失败: ' + (error.message || '未知错误'))
  } finally {
    isGenerating.value = false
  }
}

// 复制内容
const copyResult = () => {
  navigator.clipboard.writeText(generatedResult.value)
  ElMessage.success('已复制到剪贴板')
}

// 暴露方法给父组件
defineExpose({
  initForm,
  getFormData: () => ({...resumeForm})
})

// 初始化
if (props.resumeData && Object.keys(props.resumeData).length > 0) {
  initForm(props.resumeData)
}
</script>

<style scoped lang="scss">
.resume-edit-container {
  padding: 20px;

  .edit-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .resume-form {
    .experience-item {
      background: #f5f7fa;
      padding: 15px;
      margin-bottom: 15px;
      border-radius: 4px;
    }

    .add-btn {
      margin-bottom: 20px;
    }

    .el-divider {
      margin-top: 30px;
    }
  }

  .result-content {
    background: #f5f7fa;
    padding: 20px;
    border-radius: 4px;
    max-height: 500px;
    overflow-y: auto;

    pre {
      white-space: pre-wrap;
      word-wrap: break-word;
      font-family: 'Monaco', 'Menlo', monospace;
      font-size: 14px;
      line-height: 1.6;
    }
  }
}
</style>

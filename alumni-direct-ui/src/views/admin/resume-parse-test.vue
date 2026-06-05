<template>
  <div class="resume-parse-test">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>简历解析测试</span>
        </div>
      </template>

      <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :before-upload="beforeUpload"
          accept=".pdf,.jpg,.jpeg,.png,.gif,.bmp"
      >
        <el-icon class="el-icon--upload">
          <upload-filled/>
        </el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 PDF、JPG、PNG 等格式的简历文件
          </div>
        </template>
      </el-upload>

      <div v-if="loading" class="loading-container">
        <el-progress type="circle" :percentage="100" status="success"/>
        <p>正在解析简历，请稍候...</p>
      </div>

      <div v-if="parsedResume" class="result-container">
        <h3>解析结果</h3>
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="姓名">{{ parsedResume.name || '未识别' }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ parsedResume.phone || '未识别' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ parsedResume.email || '未识别' }}</el-descriptions-item>
          <el-descriptions-item label="城市">{{ parsedResume.location || '未识别' }}</el-descriptions-item>
          <el-descriptions-item label="学历">{{ parsedResume.education || '未识别' }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ parsedResume.major || '未识别' }}</el-descriptions-item>
          <el-descriptions-item label="工作年限">{{ parsedResume.experienceYears || '未识别' }}</el-descriptions-item>
          <el-descriptions-item label="期望职位">{{ parsedResume.desiredPosition || '未识别' }}</el-descriptions-item>
          <el-descriptions-item label="期望薪资">{{ parsedResume.desiredSalary || '未识别' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="parsedResume.skills && parsedResume.skills.length > 0" class="section">
          <h4>技能列表</h4>
          <el-tag v-for="skill in parsedResume.skills" :key="skill" style="margin: 5px;">{{ skill }}</el-tag>
        </div>

        <div v-if="parsedResume.certificates && parsedResume.certificates.length > 0" class="section">
          <h4>证书列表</h4>
          <el-tag v-for="cert in parsedResume.certificates" :key="cert" type="success" style="margin: 5px;">{{
              cert
            }}
          </el-tag>
        </div>

        <div v-if="parsedResume.educationExperience && parsedResume.educationExperience.length > 0" class="section">
          <h4>教育经历</h4>
          <el-timeline>
            <el-timeline-item
                v-for="(edu, index) in parsedResume.educationExperience"
                :key="index"
                :timestamp="`${edu.startDate} - ${edu.endDate}`"
                placement="top"
            >
              <el-card>
                <h5>{{ edu.school }} - {{ edu.degree }}</h5>
                <p>专业: {{ edu.major }}</p>
                <p v-if="edu.description">{{ edu.description }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>

        <div v-if="parsedResume.workExperience && parsedResume.workExperience.length > 0" class="section">
          <h4>工作经历</h4>
          <el-timeline>
            <el-timeline-item
                v-for="(work, index) in parsedResume.workExperience"
                :key="index"
                :timestamp="`${work.startDate} - ${work.endDate}`"
                placement="top"
            >
              <el-card>
                <h5>{{ work.company }} - {{ work.position }}</h5>
                <p v-if="work.description">{{ work.description }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>

        <div v-if="parsedResume.projectExperience && parsedResume.projectExperience.length > 0" class="section">
          <h4>项目经验</h4>
          <el-timeline>
            <el-timeline-item
                v-for="(project, index) in parsedResume.projectExperience"
                :key="index"
                :timestamp="`${project.startDate} - ${project.endDate}`"
                placement="top"
            >
              <el-card>
                <h5>{{ project.projectName }} - {{ project.role }}</h5>
                <p v-if="project.technologies">技术栈: {{ project.technologies }}</p>
                <p v-if="project.description">{{ project.description }}</p>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>

        <div v-if="parsedResume.selfEvaluation" class="section">
          <h4>自我评价</h4>
          <p>{{ parsedResume.selfEvaluation }}</p>
        </div>

        <div class="raw-text-section">
          <h4>原始OCR文本（调试用）</h4>
          <pre class="raw-text">{{ parsedResume.rawText }}</pre>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {UploadFilled} from '@element-plus/icons-vue'
import {parseResume} from '@/api/file.js'
import {ElMessage} from 'element-plus'

const loading = ref(false)
const parsedResume = ref(null)
const selectedFile = ref(null)

const beforeUpload = (file) => {
  const isSupportedType =
      file.type === 'application/pdf' ||
      file.type.startsWith('image/')

  if (!isSupportedType) {
    ElMessage.error('只支持 PDF 和图片文件格式!')
    return false
  }

  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }

  return true
}

const handleFileChange = async (uploadFile) => {
  if (!uploadFile.raw) return

  selectedFile.value = uploadFile.raw
  await parseResumeFile()
}

const parseResumeFile = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }

  loading.value = true
  parsedResume.value = null

  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)

    const response = await parseResume(formData)

    if (response.data.code === 200) {
      parsedResume.value = response.data.data
      ElMessage.success('简历解析成功!')
    } else {
      ElMessage.error(response.data.message || '解析失败')
    }
  } catch (error) {
    console.error('解析失败:', error)
    ElMessage.error('解析过程中发生错误')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.resume-parse-test {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-demo {
  margin-bottom: 20px;
}

.loading-container {
  text-align: center;
  padding: 20px;
}

.result-container {
  margin-top: 20px;
}

.section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.raw-text-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.raw-text {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 300px;
  overflow-y: auto;
}
</style>
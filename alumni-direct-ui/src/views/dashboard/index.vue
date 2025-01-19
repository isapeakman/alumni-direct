<template>
  <div class="dashboard-container">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>在招职位</span>
              <el-tag type="success">实时</el-tag>
            </div>
          </template>
          <div class="card-body">
            <div class="card-value">23</div>
            <div class="card-compare">较上周 <span class="up">↑12%</span></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>简历投递</span>
              <el-tag type="warning">本周</el-tag>
            </div>
          </template>
          <div class="card-body">
            <div class="card-value">168</div>
            <div class="card-compare">较上周 <span class="up">↑8%</span></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>面试中</span>
              <el-tag type="info">进行中</el-tag>
            </div>
          </template>
          <div class="card-body">
            <div class="card-value">45</div>
            <div class="card-compare">较上周 <span class="down">↓5%</span></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>录用人数</span>
              <el-tag type="primary">本月</el-tag>
            </div>
          </template>
          <div class="card-body">
            <div class="card-value">12</div>
            <div class="card-compare">较上月 <span class="up">↑25%</span></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>简历投递趋势</span>
            </div>
          </template>
          <div ref="lineChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>职位分布</span>
            </div>
          </template>
          <div ref="pieChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import * as echarts from 'echarts'

const lineChartRef = ref(null)
const pieChartRef = ref(null)

onMounted(() => {
  // 初始化折线图
  const lineChart = echarts.init(lineChartRef.value)
  lineChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [25, 32, 28, 45, 38, 22, 30],
      type: 'line',
      smooth: true
    }]
  })

  // 初始化饼图
  const pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        {value: 8, name: '技术'},
        {value: 6, name: '产品'},
        {value: 4, name: '设计'},
        {value: 3, name: '运营'},
        {value: 2, name: '市场'}
      ]
    }]
  })

  // 监听窗口大小变化，重绘图表
  window.addEventListener('resize', () => {
    lineChart.resize()
    pieChart.resize()
  })
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;

  .el-row {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .card-body {
    text-align: center;

    .card-value {
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 10px;
    }

    .card-compare {
      font-size: 12px;
      color: #909399;

      .up {
        color: #67C23A;
      }

      .down {
        color: #F56C6C;
      }
    }
  }

  .chart-row {
    margin-top: 20px;

    .chart {
      height: 300px;
    }
  }
}
</style>
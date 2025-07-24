<template>
  <div class="career-fair-container">
    <!-- 轮播图部分 -->
    <div class="carousel-section">
      <h2 class="section-title">近期招聘会</h2>
      <el-carousel :interval="5000" height="300px" arrow="always">
        <el-carousel-item v-for="(fair, index) in upcomingFairs" :key="index">
          <div class="carousel-item">
            <div class="fair-info">
              <h3 class="fair-name">{{ fair.name }}</h3>
              <p class="fair-time">
                <i class="el-icon-time"></i>
                {{ fair.startTime }}~{{ fair.endTime }}
              </p>
              <p class="fair-location">
                <i class="el-icon-location"></i>
                {{ fair.location }}
              </p>
              <p class="fair-desc">{{ fair.description }}</p>
              <el-button
                  size="medium"
                  class="details-btn"
                  @click="viewFairDetails(fair)"
              >
                查看详情
              </el-button>
            </div>

            <div class="fair-image">
              <img :src="fair.imageUrl" :alt="fair.name"/>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 历史招聘会卡片 -->
    <div class="history-section">
      <h2 class="section-title">历史招聘会</h2>
      <div class="history-cards">
        <div
            v-for="(fair, index) in pastFairs"
            :key="index"
            class="history-card"
        >
          <div class="card-image">
            <img :src="fair.imageUrl" :alt="fair.name"/>
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ fair.name }}</h3>
            <p class="card-date">
              <i class="el-icon-date"></i>
              {{ fair.startTime }}~{{ fair.endTime }}
            </p>
            <el-button
                size="mini"
                class="details-btn"
                @click="viewFairDetails(fair)"
            >
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </div>
    <!-- 对话框 -->
    <el-dialog
        title="招聘会详情"
        v-model="dialogVisible"
        width="50%"
        :before-close="handleClose"
    >
      <div class="fair-details">
        <h3 class="fair-name">{{ selectedFair.name }}</h3>
        <p class="fair-type">
          <strong>类型:</strong> {{ selectedFair.type === 0 ? '招聘会' : '宣讲会' }}
        </p>
        <p class="fair-organizer">
          <strong>主办方:</strong> {{ selectedFair.organizer }}
        </p>
        <p class="fair-company">
          <strong>公司名:</strong> {{ selectedFair.companyName }}
        </p>
        <p class="fair-time">
          <strong>举办时间:</strong> {{ selectedFair.startTime }}~{{ selectedFair.endTime }}
        </p>
        <p class="fair-location">
          <strong>地点:</strong> {{ selectedFair.location }}
        </p>
        <p class="fair-desc">
          <strong>描述:</strong> {{ selectedFair.description }}
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {ElCarousel, ElCarouselItem, ElButton} from 'element-plus';
import {getFairCards} from "@/api/fair.js";

const upcomingFairs = ref([]);
const pastFairs = ref([]);
const dialogVisible = ref(false);
const selectedFair = ref({});
const fetchFairs = async () => {
  try {
    const response = await getFairCards() // 假设后端API路径为 /api/fairs

    if (response.data.code === 200) {
      const fairs = response.data.data.records;
      // 将前三条记录作为轮播图数据
      upcomingFairs.value = fairs.slice(0, 3);

      // 将剩余的记录作为历史记录数据
      pastFairs.value = fairs.slice(3);
    } else {
      console.error('获取招聘会列表失败:', response.data.message);
    }
  } catch (error) {
    console.error('获取招聘会列表失败:', error);
  }
};

const viewFairDetails = (fair) => {
  console.log('查看招聘会详情:', fair);
  selectedFair.value = fair;
  dialogVisible.value = true;
};
const handleClose = () => {
  dialogVisible.value = false;
};
onMounted(() => {
  fetchFairs();
});
</script>

<style scoped>
.career-fair-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.section-title {
  color: #303133;
  margin-bottom: 25px;
  font-weight: 500;
  font-size: 20px;
  border-left: 4px solid #409EFF;
  padding-left: 10px;
}

/* 轮播图样式 */
.carousel-section {
  margin-bottom: 40px;
}

.carousel-item {
  display: flex;
  height: 100%;
  background: linear-gradient(to right, #f5f7fa, #e4e7ed);
  border-radius: 8px;
  overflow: hidden;
  position: relative; /* 设置相对定位 */
}

.fair-info {
  flex: 1;
  padding: 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.fair-name {
  font-size: 24px;
  color: #303133;
  margin-bottom: 15px;
}

.fair-time,
.fair-location {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.fair-time i,
.fair-location i {
  margin-right: 8px;
  font-size: 18px;
}

.fair-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.6;
  margin: 20px 0;
}

.signup-btn {
  align-self: flex-start;
  padding: 10px 25px;
}

.fair-image {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.fair-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 历史招聘会卡片样式 */
.history-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.history-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
  display: flex;
}

.history-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
}

.card-image {
  width: 120px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.card-content {
  flex: 1;
  padding: 15px;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 16px;
  color: #303133;
  margin-bottom: 10px;
}

.card-date {
  font-size: 13px;
  color: #909399;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.card-date i {
  margin-right: 5px;
}

.card-stats {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #606266;
}

.card-stats span {
  display: flex;
  align-items: center;
}

.card-stats i {
  margin-right: 5px;
}

.details-btn {
  align-self: flex-end;
  margin-top: 10px;
}

/* 对话框样式 */
.fair-details {
  padding: 20px;
}

.fair-details h3 {
  margin-bottom: 15px;
}

.fair-details p {
  margin-bottom: 10px;
}

/* 轮播图按钮样式 */
.carousel-item .details-btn {
  position: absolute; /* 设置绝对定位 */
  bottom: 10px; /* 距离底部10px */
  left: 10px; /* 距离左侧10px */
}

/* 响应式调整 */
@media (max-width: 768px) {
  .carousel-item {
    flex-direction: column;
  }

  .fair-image {
    padding: 0 20px 20px;
  }

  .history-cards {
    grid-template-columns: 1fr;
  }

  .history-card {
    flex-direction: column;
  }

  .card-image {
    width: 100%;
    height: 120px;
  }
}
</style>


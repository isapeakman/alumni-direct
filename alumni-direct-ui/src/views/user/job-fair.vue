<template>
  <div class="job-fair-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">招聘会</h1>
      <p class="page-subtitle">参与招聘会，发现更多优质职位机会</p>
    </div>

    <!-- 轮播图区域 -->
    <div class="carousel-section">
      <div class="section-title-row">
        <h2 class="section-title">
          <el-icon class="title-icon">
            <Calendar/>
          </el-icon>
          近期招聘会
        </h2>
        <span class="section-count">{{ upcomingFairs.length }} 场活动</span>
      </div>

      <div v-if="upcomingFairs.length > 0" class="carousel-wrapper">
        <el-carousel :interval="5000" height="280px" arrow="always" indicator-position="bottom">
          <el-carousel-item v-for="(fair, index) in upcomingFairs" :key="index">
            <div class="carousel-item" @click="viewFairDetails(fair)">
              <div class="carousel-bg"
                   :style="{ backgroundImage: `url(${fair.imageUrl || '/images/default-fair.jpg'})` }">
                <div class="carousel-overlay"></div>
              </div>
              <div class="carousel-content">
                <div class="fair-badge" :class="fair.type === 0 ? 'badge-primary' : 'badge-secondary'">
                  {{ fair.type === 0 ? '招聘会' : '宣讲会' }}
                </div>
                <h3 class="fair-title">{{ fair.name }}</h3>
                <div class="fair-info">
                  <span class="info-item">
                    <el-icon><Clock/></el-icon>
                    {{ fair.startTime }} - {{ fair.endTime }}
                  </span>
                  <span class="info-item">
                    <el-icon><Location/></el-icon>
                    {{ fair.location }}
                  </span>
                </div>
                <p class="fair-desc">{{ fair.description }}</p>
                <el-button type="primary" size="small" class="detail-btn">
                  查看详情
                </el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <div v-else class="empty-state">
        <el-icon size="48" color="#94a3b8">
          <Calendar/>
        </el-icon>
        <p>暂无近期招聘会</p>
      </div>
    </div>

    <!-- 历史招聘会 -->
    <div class="section">
      <div class="section-title-row">
        <h2 class="section-title">
          <el-icon class="title-icon">
            <Collection/>
          </el-icon>
          历史招聘会
        </h2>
        <span class="section-count">{{ pastFairs.length }} 场活动</span>
      </div>

      <div v-if="pastFairs.length > 0" class="history-list">
        <div
            v-for="(fair, index) in pastFairs"
            :key="index"
            class="history-item"
            @click="viewFairDetails(fair)"
        >
          <div class="history-info">
            <h4 class="history-title">{{ fair.name }}</h4>
            <div class="history-meta">
              <span class="meta-tag">{{ fair.type === 0 ? '招聘会' : '宣讲会' }}</span>
              <span>{{ fair.startTime }} - {{ fair.endTime }}</span>
              <span>{{ fair.location }}</span>
            </div>
          </div>
          <el-button type="text" class="history-btn">
            查看详情
            <el-icon>
              <ArrowRight/>
            </el-icon>
          </el-button>
        </div>
      </div>

      <div v-else class="empty-state">
        <el-icon size="48" color="#94a3b8">
          <Collection/>
        </el-icon>
        <p>暂无历史招聘会记录</p>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
        title="招聘会详情"
        v-model="dialogVisible"
        width="500px"
        :before-close="handleClose"
    >
      <div v-if="selectedFair" class="detail-content">
        <div class="detail-header">
          <div class="detail-badge" :class="selectedFair.type === 0 ? 'badge-primary' : 'badge-secondary'">
            {{ selectedFair.type === 0 ? '招聘会' : '宣讲会' }}
          </div>
          <h3 class="detail-title">{{ selectedFair.name }}</h3>
        </div>
        <el-divider/>
        <div class="detail-info">
          <div class="info-item">
            <el-icon class="icon">
              <OfficeBuilding/>
            </el-icon>
            <span class="label">主办方</span>
            <span class="value">{{ selectedFair.organizer }}</span>
          </div>
          <div class="info-item">
            <el-icon class="icon">
              <Briefcase/>
            </el-icon>
            <span class="label">公司名</span>
            <span class="value">{{ selectedFair.companyName }}</span>
          </div>
          <div class="info-item">
            <el-icon class="icon">
              <Clock/>
            </el-icon>
            <span class="label">举办时间</span>
            <span class="value">{{ selectedFair.startTime }} ~ {{ selectedFair.endTime }}</span>
          </div>
          <div class="info-item">
            <el-icon class="icon">
              <Location/>
            </el-icon>
            <span class="label">地点</span>
            <span class="value">{{ selectedFair.location }}</span>
          </div>
        </div>
        <el-divider/>
        <div class="detail-desc">
          <span class="label">活动描述</span>
          <p>{{ selectedFair.description }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {ElCarousel, ElCarouselItem, ElButton} from 'element-plus';
import {Calendar, Location, ArrowRight, OfficeBuilding, Briefcase, Collection, Clock} from '@element-plus/icons-vue';
import {getFairCards} from "@/api/fair.js";

const upcomingFairs = ref([]);
const pastFairs = ref([]);
const dialogVisible = ref(false);
const selectedFair = ref({});

// Mock数据
const mockFairs = [
  {
    id: 1,
    name: '2024 春季大型招聘会',
    type: 0,
    imageUrl: 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=1200&h=600&fit=crop',
    startTime: '2024-04-15',
    endTime: '2024-04-16',
    location: '北京国际会议中心',
    organizer: '北京人才市场',
    companyName: '北京人才市场',
    description: '汇聚500+知名企业，提供10000+优质岗位，涵盖IT、金融、制造、教育等多个行业，是您求职的最佳选择。'
  },
  {
    id: 2,
    name: '互联网行业专场宣讲会',
    type: 1,
    imageUrl: 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=1200&h=600&fit=crop',
    startTime: '2024-04-20',
    endTime: '2024-04-20',
    location: '中关村软件园',
    organizer: '中关村软件园',
    companyName: '中关村软件园',
    description: '邀请BAT、字节跳动、美团等互联网大厂HR现场宣讲，了解最新招聘政策，提前锁定心仪岗位。'
  },
  {
    id: 3,
    name: '高校毕业生专场招聘会',
    type: 0,
    imageUrl: 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=1200&h=600&fit=crop',
    startTime: '2024-04-25',
    endTime: '2024-04-26',
    location: '清华大学体育馆',
    organizer: '清华大学就业指导中心',
    companyName: '清华大学',
    description: '面向2024届高校毕业生，提供专属就业机会，助力应届生顺利入职。'
  },
  {
    id: 4,
    name: '金融行业精英招聘会',
    type: 0,
    imageUrl: 'https://images.unsplash.com/photo-1507721999472-8ed4421c4af2?w=1200&h=600&fit=crop',
    startTime: '2024-03-20',
    endTime: '2024-03-21',
    location: '上海陆家嘴金融中心',
    organizer: '上海金融人才协会',
    companyName: '上海金融人才协会',
    description: '汇聚国内外知名金融机构，提供投行、基金、银行等高端职位。'
  },
  {
    id: 5,
    name: 'AI人工智能专场招聘会',
    type: 1,
    imageUrl: 'https://images.unsplash.com/photo-1677442136470-0a0cd421918a?w=1200&h=600&fit=crop',
    startTime: '2024-03-15',
    endTime: '2024-03-15',
    location: '杭州梦想小镇',
    organizer: '阿里云',
    companyName: '阿里巴巴',
    description: '聚焦AI领域，邀请顶尖AI企业，为算法工程师、数据科学家提供优质岗位。'
  }
];

const fetchFairs = async () => {
  try {
    const response = await getFairCards();

    if (response.data.code === 200) {
      const fairs = response.data.data.records;
      upcomingFairs.value = fairs.slice(0, 3);
      pastFairs.value = fairs.slice(3);
    } else {
      console.error('获取招聘会列表失败，使用mock数据:', response.data.message);
      upcomingFairs.value = mockFairs.slice(0, 3);
      pastFairs.value = mockFairs.slice(3);
    }
  } catch (error) {
    console.error('获取招聘会列表失败，使用mock数据:', error);
    upcomingFairs.value = mockFairs.slice(0, 3);
    pastFairs.value = mockFairs.slice(3);
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

<style lang="scss" scoped>
.job-fair-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px;

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

/* 轮播图区域 */
.carousel-section {
  margin-bottom: 40px;

  .section-title-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
      display: flex;
      align-items: center;
      gap: 8px;

      .title-icon {
        color: #0ea5e9;
      }
    }

    .section-count {
      font-size: 13px;
      color: #94a3b8;
      background: #f1f5f9;
      padding: 4px 12px;
      border-radius: 20px;
    }
  }

  .carousel-wrapper {
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  }
}

.carousel-item {
  position: relative;
  height: 280px;
  cursor: pointer;
  overflow: hidden;

  .carousel-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-size: cover;
    background-position: center;
    background-color: #1e293b;
  }

  .carousel-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(90deg, rgba(30, 41, 59, 0.9) 0%, rgba(30, 41, 59, 0.6) 50%, rgba(30, 41, 59, 0.3) 100%);
  }

  .carousel-content {
    position: relative;
    z-index: 1;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 0 60px;
    max-width: 700px;

    .fair-badge {
      display: inline-block;
      font-size: 12px;
      padding: 4px 12px;
      border-radius: 20px;
      font-weight: 500;
      margin-bottom: 16px;
      align-self: flex-start;

      &.badge-primary {
        background: rgba(14, 165, 233, 0.2);
        color: #0ea5e9;
      }

      &.badge-secondary {
        background: rgba(251, 146, 60, 0.2);
        color: #f97316;
      }
    }

    .fair-title {
      font-size: 26px;
      font-weight: 700;
      color: #fff;
      margin: 0 0 16px;
    }

    .fair-info {
      display: flex;
      gap: 24px;
      margin-bottom: 12px;

      .info-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);

        :deep(.el-icon) {
          font-size: 14px;
        }
      }
    }

    .fair-desc {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.7);
      line-height: 1.6;
      margin: 0 0 20px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .detail-btn {
      align-self: flex-start;
      background: linear-gradient(135deg, #0ea5e9, #0284c7);
      border: none;
      padding: 10px 24px;
      border-radius: 8px;
    }
  }
}

/* 区域通用样式 */
.section {
  margin-bottom: 40px;

  .section-title-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
      display: flex;
      align-items: center;
      gap: 8px;

      .title-icon {
        color: #0ea5e9;
      }
    }

    .section-count {
      font-size: 13px;
      color: #94a3b8;
      background: #f1f5f9;
      padding: 4px 12px;
      border-radius: 20px;
    }
  }
}

/* 近期招聘会卡片 */
.fair-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.fair-card {
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: none;
  transition: all 0.3s;

  &:hover {
    border-color: #0ea5e9;
    box-shadow: 0 4px 12px rgba(14, 165, 233, 0.1);
  }

  .card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;

    .card-badge {
      font-size: 12px;
      padding: 4px 12px;
      border-radius: 20px;
      font-weight: 500;

      &.badge-primary {
        background: rgba(14, 165, 233, 0.1);
        color: #0ea5e9;
      }

      &.badge-secondary {
        background: rgba(251, 146, 60, 0.1);
        color: #f97316;
      }
    }

    .card-title {
      font-size: 18px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
    }
  }

  .card-body {
    .info-row {
      display: flex;
      gap: 24px;
      margin-bottom: 12px;

      .info-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        color: #64748b;

        :deep(.el-icon) {
          font-size: 14px;
        }
      }
    }

    .card-desc {
      font-size: 14px;
      color: #64748b;
      line-height: 1.6;
      margin: 0 0 16px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .card-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 16px;
      border-top: 1px solid #f1f5f9;

      .organizer {
        font-size: 13px;
        color: #94a3b8;
      }
    }
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 40px;
  background: #fff;
  border-radius: 12px;
  border: 1px dashed #e2e8f0;

  p {
    margin-top: 12px;
    font-size: 14px;
    color: #94a3b8;
  }
}

/* 历史招聘会列表 */
.history-list {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: background 0.2s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #f8fafc;
  }

  .history-info {
    .history-title {
      font-size: 15px;
      font-weight: 500;
      color: #1e293b;
      margin: 0 0 6px;
    }

    .history-meta {
      display: flex;
      gap: 16px;
      font-size: 13px;
      color: #94a3b8;

      .meta-tag {
        color: #0ea5e9;
        font-weight: 500;
      }
    }
  }

  .history-btn {
    color: #0ea5e9;
    font-size: 13px;
    padding: 0;
  }
}

/* 详情弹窗 */
.detail-content {
  .detail-header {
    text-align: center;
    margin-bottom: 8px;

    .detail-badge {
      font-size: 12px;
      padding: 4px 12px;
      border-radius: 20px;
      font-weight: 500;
      margin-bottom: 12px;

      &.badge-primary {
        background: rgba(14, 165, 233, 0.1);
        color: #0ea5e9;
      }

      &.badge-secondary {
        background: rgba(251, 146, 60, 0.1);
        color: #f97316;
      }
    }

    .detail-title {
      font-size: 20px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
    }
  }

  .detail-info {
    .info-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 0;

      .icon {
        width: 20px;
        height: 20px;
        color: #0ea5e9;
      }

      .label {
        width: 60px;
        font-size: 13px;
        color: #94a3b8;
      }

      .value {
        font-size: 14px;
        color: #334155;
        flex: 1;
      }
    }
  }

  .detail-desc {
    .label {
      font-size: 13px;
      color: #94a3b8;
      margin-bottom: 8px;
      display: block;
    }

    p {
      font-size: 14px;
      color: #64748b;
      line-height: 1.7;
      margin: 0;
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .job-fair-page {
    padding: 20px;
  }

  .info-row {
    flex-direction: column;
    gap: 8px !important;
  }

  .history-meta {
    flex-wrap: wrap;
  }
}
</style>
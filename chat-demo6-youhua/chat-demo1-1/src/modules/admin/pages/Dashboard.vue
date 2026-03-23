Dashboard.vue
<template>
  <div class="dashboard-container">
    <!-- 图表区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
      <!-- 用户统计图表 -->
      <div class="bg-white shadow rounded-lg p-6">
        <h2 class="text-lg font-medium text-gray-900 mb-4">用户统计</h2>
        <div class="h-80">
          <canvas ref="userChartRef"></canvas>
        </div>
      </div>
      
      <!-- 社区统计图表 -->
      <div class="bg-white shadow rounded-lg p-6">
        <h2 class="text-lg font-medium text-gray-900 mb-4">社区统计</h2>
        <div class="h-80">
          <canvas ref="communityChartRef"></canvas>
        </div>
      </div>
      
      <!-- 图片生成图表 -->
      <div class="bg-white shadow rounded-lg p-6">
        <h2 class="text-lg font-medium text-gray-900 mb-4">图片生成趋势</h2>
        <div class="h-80">
          <canvas ref="imageChartRef"></canvas>
        </div>
      </div>
      
      <!-- 多模态交互图表 -->
      <div class="bg-white shadow rounded-lg p-6">
        <h2 class="text-lg font-medium text-gray-900 mb-4">多模态交互趋势</h2>
        <div class="h-80">
          <canvas ref="multimodalChartRef"></canvas>
        </div>
      </div>
    </div>
    
    <!-- 最近7天数据图表 -->
    <div class="bg-white shadow rounded-lg p-6 mb-8">
      <h2 class="text-lg font-medium text-gray-900 mb-4">最近7天数据趋势</h2>
      <div class="h-80">
        <canvas ref="recent7DaysChartRef"></canvas>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  getStatisticsOverview,
  getCommunityStatistics,
  getImageGenerationStatistics,
  getMultiModalStatistics,
  getRecent7DaysStatistics,
  getUserStatistics
} from '@/modules/admin/services/admin';
import Chart from 'chart.js/auto';

// 类型定义
type DateValueMap = {
  [key: string]: number;
};

// 统计数据
const overviewStats = ref({
  usersCount: 0,
  activeUsersCount: 0,
  adminCount: 0,
  todayRegistrations: 0,
  totalUsers: 0,
  activeUsers: 0,
  userRoleDistribution: {
    ADMIN: 0,
    USER: 0
  },
  userGrowth: {} as DateValueMap
});

const communityStats = ref({
  totalPosts: 0,
  totalComments: 0,
  totalCollections: 0,
  totalLikes: 0,
  postGrowth: {} as DateValueMap
});



const imageStats = ref({
  totalGenerations: 0,
  generationGrowth: {} as DateValueMap,
  modelUsage: {} as DateValueMap
});

const multimodalStats = ref({
  totalInteractions: 0,
  interactionGrowth: {} as DateValueMap
});

const recent7DaysStats = ref({
  dailyImageGenerations: {} as DateValueMap,
  dailyImagePosts: {} as DateValueMap,
  dailyMultiModalInteractions: {} as DateValueMap,
  dailyActiveUsers: {} as DateValueMap,
  dailyComments: {} as DateValueMap
});

// 图表实例
const userChartRef = ref<HTMLCanvasElement | null>(null);
const communityChartRef = ref<HTMLCanvasElement | null>(null);
const imageChartRef = ref<HTMLCanvasElement | null>(null);
const multimodalChartRef = ref<HTMLCanvasElement | null>(null);
const recent7DaysChartRef = ref<HTMLCanvasElement | null>(null);

// 图表实例对象
let userChart: Chart | null = null;
let communityChart: Chart | null = null;
let imageChart: Chart | null = null;
let multimodalChart: Chart | null = null;
let recent7DaysChart: Chart | null = null;

// 路由
const route = useRoute();
const router = useRouter();

// 加载统计数据
const loadData = async () => {
  try {
    console.log('开始加载仪表盘数据...');
    
    // 并行请求所有统计数据
    const [overviewResponse, communityResponse, userResponse, imageResponse, multimodalResponse, recent7DaysResponse] = await Promise.all([
      getStatisticsOverview(),
      getCommunityStatistics(),
      getUserStatistics(),
      getImageGenerationStatistics(),
      getMultiModalStatistics(),
      getRecent7DaysStatistics()
    ]);
    
    // 处理响应数据
    if (overviewResponse && overviewResponse.data) {
      overviewStats.value = overviewResponse.data;
    }
    
    if (communityResponse && communityResponse.data) {
      communityStats.value = communityResponse.data;
    }
    
    if (userResponse && userResponse.data) {
      // 合并用户统计数据到overviewStats
      overviewStats.value = {
        ...overviewStats.value,
        ...userResponse.data
      };
    }
    
    if (imageResponse && imageResponse.data) {
      imageStats.value = imageResponse.data;
    }
    
    if (multimodalResponse && multimodalResponse.data) {
      multimodalStats.value = multimodalResponse.data;
    }
    
    if (recent7DaysResponse && recent7DaysResponse.data) {
      recent7DaysStats.value = recent7DaysResponse.data;
    }

    // 日志记录
    console.log('已更新统计信息:', {
      overviewStats: overviewStats.value,
      communityStats: communityStats.value,
      imageStats: imageStats.value,
      multimodalStats: multimodalStats.value,
      recent7DaysStats: recent7DaysStats.value
    });
    
    // 更新图表
    setTimeout(() => {
      updateCharts();
    }, 100);
  } catch (error: any) {
    console.error('加载数据失败:', error);
    
    // 提供详细的错误反馈
    let errorMessage = '加载数据失败';
    
    if (error.message.includes('Network Error') || error.message.includes('Failed to fetch')) {
      errorMessage = '网络连接失败，请检查您的网络连接或后端服务是否正常运行';
    } else if (error.message.includes('404')) {
      errorMessage = 'API端点未找到，请检查后端API是否已正确部署';
    } else if (error.message.includes('401') || error.message.includes('403')) {
      errorMessage = '未授权访问，请重新登录';
      // 清除无效的认证信息并重定向到登录页
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      router.push('/login');
    } else if (error.message.includes('Unexpected token') || error.message.includes('JSON.parse')) {
      errorMessage = 'API响应格式错误，请检查后端服务是否返回有效的JSON数据';
    } else {
      errorMessage = `加载数据失败: ${error.message}`;
    }

    console.warn('用户提示信息:', errorMessage);
    alert(errorMessage);
  }
};

// 监听路由变化
watch(
  () => route.path,
  (newPath) => {
    if (newPath === '/admin' || newPath === '/admin/' || newPath === '/admin/dashboard') {
      console.log('路由变化到仪表盘页面，重新加载数据');
      loadData();
    }
  }
);

// 创建用户统计图表
const createUserChart = () => {
  if (!userChartRef.value) return;
  
  const ctx = userChartRef.value.getContext('2d');
  if (!ctx) return;
  
  // 销毁现有图表
  if (userChart) {
    userChart.destroy();
  }
  
  userChart = new Chart(ctx, {
    type: 'pie',
    data: {
      labels: ['普通用户', '管理员'],
      datasets: [{
        data: [
          overviewStats.value.userRoleDistribution?.USER || 0,
          overviewStats.value.userRoleDistribution?.ADMIN || 0
        ],
        backgroundColor: [
          'rgba(54, 162, 235, 0.6)',
          'rgba(255, 206, 86, 0.6)'
        ],
        borderColor: [
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)'
        ],
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom'
        },
        title: {
          display: true,
          text: '用户角色分布'
        }
      }
    }
  });
};

// 创建社区统计图表
const createCommunityChart = () => {
  if (!communityChartRef.value) return;
  
  const ctx = communityChartRef.value.getContext('2d');
  if (!ctx) return;
  
  // 销毁现有图表
  if (communityChart) {
    communityChart.destroy();
  }
  
  communityChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ['帖子', '评论', '收藏', '点赞'],
      datasets: [{
        label: '数量',
        data: [
          communityStats.value.totalPosts || 0,
          communityStats.value.totalComments || 0,
          communityStats.value.totalCollections || 0,
          communityStats.value.totalLikes || 0
        ],
        backgroundColor: [
          'rgba(255, 99, 132, 0.6)',
          'rgba(54, 162, 235, 0.6)',
          'rgba(255, 206, 86, 0.6)',
          'rgba(75, 192, 192, 0.6)'
        ],
        borderColor: [
          'rgba(255, 99, 132, 1)',
          'rgba(54, 162, 235, 1)',
          'rgba(255, 206, 86, 1)',
          'rgba(75, 192, 192, 1)'
        ],
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: '社区统计数据'
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
};

// 创建图片生成图表
const createImageChart = () => {
  if (!imageChartRef.value) return;
  
  const ctx = imageChartRef.value.getContext('2d');
  if (!ctx) return;
  
  // 销毁现有图表
  if (imageChart) {
    imageChart.destroy();
  }
  
  // 提取生成增长数据
  const generationGrowth = imageStats.value.generationGrowth || {};
  const dates = Object.keys(generationGrowth).sort();
  const values = dates.map(date => generationGrowth[date] || 0);
  
  imageChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: dates.map(date => date.substring(5)), // 只显示月-日
      datasets: [{
        label: '图片生成数量',
        data: values,
        borderColor: 'rgba(255, 99, 132, 1)',
        backgroundColor: 'rgba(255, 99, 132, 0.1)',
        tension: 0.1,
        fill: true
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: '图片生成趋势'
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
};

// 创建多模态交互图表
const createMultimodalChart = () => {
  if (!multimodalChartRef.value) return;
  
  const ctx = multimodalChartRef.value.getContext('2d');
  if (!ctx) return;
  
  // 销毁现有图表
  if (multimodalChart) {
    multimodalChart.destroy();
  }
  
  // 提取交互增长数据
  const interactionGrowth = multimodalStats.value.interactionGrowth || {};
  const dates = Object.keys(interactionGrowth).sort();
  const values = dates.map(date => interactionGrowth[date] || 0);
  
  multimodalChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: dates.map(date => date.substring(5)), // 只显示月-日
      datasets: [{
        label: '交互次数',
        data: values,
        borderColor: 'rgba(75, 192, 192, 1)',
        backgroundColor: 'rgba(75, 192, 192, 0.1)',
        tension: 0.1,
        fill: true
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: '多模态交互趋势'
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
};

// 创建最近7天数据图表
const createRecent7DaysChart = () => {
  if (!recent7DaysChartRef.value) return;
  
  const ctx = recent7DaysChartRef.value.getContext('2d');
  if (!ctx) return;
  
  // 销毁现有图表
  if (recent7DaysChart) {
    recent7DaysChart.destroy();
  }
  
  // 提取最近7天数据
  const dailyImagePosts = recent7DaysStats.value.dailyImagePosts || {};
  const dailyComments = recent7DaysStats.value.dailyComments || {};
  const dates = Object.keys(dailyImagePosts).sort();
  
  recent7DaysChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: dates.map(date => date.substring(5)), // 只显示月-日
      datasets: [
        {
          label: '帖子数量',
          data: dates.map(date => dailyImagePosts[date] || 0),
          borderColor: 'rgba(255, 99, 132, 1)',
          backgroundColor: 'rgba(255, 99, 132, 0.1)',
          tension: 0.1,
          fill: true
        },
        {
          label: '评论数量',
          data: dates.map(date => dailyComments[date] || 0),
          borderColor: 'rgba(54, 162, 235, 1)',
          backgroundColor: 'rgba(54, 162, 235, 0.1)',
          tension: 0.1,
          fill: true
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: '最近7天数据趋势'
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
};

// 更新所有图表
const updateCharts = () => {
  createUserChart();
  createCommunityChart();
  createImageChart();
  createMultimodalChart();
  createRecent7DaysChart();
};

// 清理图表实例
onUnmounted(() => {
  userChart?.destroy();
  communityChart?.destroy();
  imageChart?.destroy();
  multimodalChart?.destroy();
  recent7DaysChart?.destroy();
});

// 组件挂载时加载数据
onMounted(() => {
  console.log('Dashboard组件挂载');
  loadData();
});
</script>

<style scoped>
.dashboard-container {
  padding: 1rem;
}
</style>
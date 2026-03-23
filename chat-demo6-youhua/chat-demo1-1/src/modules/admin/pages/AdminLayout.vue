<template>
  <div class="min-h-screen bg-gray-50 flex">
    <!-- 侧边栏 -->
    <div class="hidden md:flex md:w-64 md:flex-col md:fixed md:inset-y-0">
      <Sidebar />
    </div>

    <div class="flex-1 md:pl-64">
      <!-- 顶部导航栏 -->
      <header class="bg-white shadow-sm">
        <div class="flex justify-between items-center h-16 px-4 sm:px-6 lg:px-8">
          <div class="flex-1 md:ml-0 md:px-6 lg:px-8">
            <h1 class="text-xl font-bold text-gray-900">管理员控制台</h1>
          </div>
          
          <div class="flex items-center">
            <div class="mr-4 text-sm text-gray-600 hidden sm:block">
              欢迎，{{ adminName }}
            </div>
            <button 
              @click="logout"
              class="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-full shadow-sm text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
            >
              退出
            </button>
          </div>
        </div>
      </header>

      <!-- 移动端侧边栏覆盖层 -->
      <div v-show="sidebarOpen" class="md:hidden fixed inset-0 z-40">
          <div v-show="sidebarOpen" class="fixed inset-0 bg-gray-600 bg-opacity-75" @click="sidebarOpen = false"></div>
        
          <div v-show="sidebarOpen" class="relative flex-1 flex h-full max-w-xs w-full bg-white">
            <Sidebar />
          </div>
      </div>

      <!-- 主要内容区域 -->
      <main class="pb-12">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <router-view :key="$route.fullPath" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import Sidebar from './Sidebar.vue';
import { 
  getUsers as apiGetUsers, 
} from '@/modules/admin/services/admin';

const router = useRouter();
const sidebarOpen = ref(false);
const loading = ref(true);

// 获取真实管理员名称
const userInfoStr = localStorage.getItem('userInfo');
let adminName = ref('Admin User');

if (userInfoStr) {
  try {
    const userInfo = JSON.parse(userInfoStr);
    adminName.value = userInfo.name || userInfo.username || 'Admin User';
  } catch (e) {
    console.error('解析用户信息失败', e);
  }
}

// 真实统计数据
const stats = ref({
  usersCount: 0,
  conversationsCount: 0,
  messagesCount: 0,
  activeUsersToday: 0
});

// 最近注册用户
const recentUsers = ref<any[]>([]);


const logout = () => {
  // 清除本地存储的认证信息
  localStorage.removeItem('token');
  localStorage.removeItem('userInfo');
  
  // 跳转到登录页面
  router.push('/login');
};

// 加载统计数据
const loadData = async () => {
  try {
    // 获取用户列表
    const usersResponse = await apiGetUsers();
    
    // 检查响应是否包含预期的数据结构
    if (!usersResponse) {
      throw new Error('API响应为空');
    }

    // 处理不同可能的响应格式
    let users: any[] = [];
    
    // 处理 { data: [...] } 格式
    if (usersResponse.data !== undefined && usersResponse.data !== null) {
      users = Array.isArray(usersResponse.data) ? usersResponse.data : [];
    } 
    // 处理直接返回数组的情况
    else if (Array.isArray(usersResponse)) {
      users = usersResponse;
    } 
    // 处理 { success: true, result: [...] } 等其他格式
    else {
      users = Object.values(usersResponse).find((value: any) => Array.isArray(value)) || [];
    }

    // 验证用户数据格式
    if (!Array.isArray(users)) {
      console.warn('用户数据不是数组，尝试转换', users);
      users = [];
    }

    // 调试信息
    console.log('成功获取用户数据:', {
      totalCount: users.length,
      sample: users.length > 0 ? users[0] : '无数据'
    });
    
    // 设置统计数据
    stats.value = {
      usersCount: users.length,
      conversationsCount: 0, // 这个需要从后端API获取
      messagesCount: 0,     // 这个需要从后端API获取
      activeUsersToday: 0      // 这个需要从后端API获取
    };
    
    // 设置最近注册的用户（按注册时间排序取前几个）
    // 先确保每个用户对象都有必要属性
    recentUsers.value = users.slice(0, 4).map(user => ({
      id: user.id || user._id || Math.random(),
      name: user.name || user.username || user.email?.split('@')[0] || '未知用户',
      email: user.email,
      username: user.username,
      isActive: user.isActive !== undefined ? user.isActive : (user.active !== undefined ? user.active : true)
    }));

    // 添加调试日志
    console.log('已更新统计信息:', stats.value);
    console.log('最近用户列表:', recentUsers.value);
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
      setTimeout(() => {
        router.push('/login');
      }, 1500);
    } else if (error.message.includes('Unexpected token') || error.message.includes('JSON.parse')) {
      errorMessage = 'API响应格式错误，请检查后端服务是否返回有效的JSON数据';
    } else if (error.message.includes('timeout')) {
      errorMessage = '请求超时，请稍后重试';
    } else {
      errorMessage = `加载数据失败: ${error.message}`;
    }

    console.warn('用户提示信息:', errorMessage);
    alert(errorMessage);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.admin-container {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
<template>
  <div class="flex h-screen relative overflow-hidden">
    <UserSidebar
      :conversations="conversations"
      :current-conversation-id="currentConversationId"
      :current-route="'canvas'"
      @switch-conversation="switchConversation"
      @create-temporary-conversation="createTemporaryConversation"
      @remove="handleRemove"
    />
    
    <div class="flex-1 flex flex-col overflow-hidden">
      <div class="absolute inset-0 bg-gradient-to-br from-emerald-50 to-green-50 dark:from-gray-900 dark:to-gray-800 z-0"></div>
      <div class="absolute inset-0 bg-grid-pattern opacity-5 z-0"></div>
      <div class="absolute top-0 left-0 w-64 h-64 bg-gradient-to-br from-emerald-200 to-green-200 rounded-full blur-3xl opacity-20 animate-pulse z-0"></div>
      <div class="absolute bottom-0 right-0 w-96 h-96 bg-gradient-to-tr from-green-200 to-emerald-200 rounded-full blur-3xl opacity-20 animate-pulse z-0"></div>

      <div class="main-content flex flex-1 relative z-10 min-h-0">
        <div class="flex-1 flex flex-col min-w-0 p-8">
          <div class="mb-8">
            <h1 class="text-3xl font-bold text-gray-800 dark:text-gray-200 mb-2">画布项目管理</h1>
            <p class="text-gray-600 dark:text-gray-400">管理您的画布项目和会话</p>
          </div>

          <div class="flex justify-between items-center mb-6">
            <button 
              @click="createNewProject"
              class="rounded-lg px-6 py-3.5 text-base font-medium transition-all duration-300 transform hover:scale-[1.02] active:scale-95 bg-gradient-to-r from-emerald-500 to-green-500 hover:from-emerald-600 hover:to-green-600 text-white shadow-md shadow-emerald-200/50 dark:shadow-emerald-900/50"
            >
              <span class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
                </svg>
                新建项目
              </span>
            </button>
          </div>

          <div v-if="loading" class="flex items-center justify-center py-16">
            <div class="animate-spin rounded-full h-12 w-12 border-t-4 border-b-4 border-emerald-500"></div>
          </div>

          <div v-else-if="canvasSessions.length === 0" class="text-center py-16">
            <svg class="w-16 h-16 text-gray-300 dark:text-gray-600 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9.75 17L9 20l-1.75-2.25M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <h4 class="text-lg font-medium text-gray-800 dark:text-gray-200 mb-2">暂无画布项目</h4>
            <p class="text-gray-600 dark:text-gray-400 mb-6">点击上方按钮创建您的第一个画布项目</p>
            <button 
              @click="createNewProject"
              class="px-6 py-3 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white transition-colors duration-200"
            >
              立即创建
            </button>
          </div>

          <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div 
              v-for="session in canvasSessions" 
              :key="session.sessionId"
              class="bg-white dark:bg-gray-800 rounded-xl shadow-lg overflow-hidden transition-all duration-300 hover:shadow-xl hover:scale-[1.02] group"
            >
              <div class="p-6">
                <div class="flex items-start justify-between mb-4">
                  <div>
                    <h3 class="text-xl font-semibold text-gray-800 dark:text-gray-200 mb-1 group-hover:text-emerald-600 dark:group-hover:text-emerald-400 transition-colors duration-200">{{ session.text }}</h3>
                    <p class="text-sm text-gray-500 dark:text-gray-400">创建于 {{ formatDate(session.createdAt) }}</p>
                  </div>
                  <div class="flex gap-2">
                    <button 
                      @click="renameSession(session)"
                      class="p-2 rounded-lg text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 hover:text-gray-600 dark:hover:text-gray-300 transition-colors duration-200"
                      title="重命名"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                      </svg>
                    </button>
                    <button 
                      @click="deleteSession(session)"
                      class="p-2 rounded-lg text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-700 hover:text-red-500 transition-colors duration-200"
                      title="删除"
                    >
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                      </svg>
                    </button>
                  </div>
                </div>
                <div class="p-4">
                  <image src = {{form.imageUrl}}></image>                  
                </div>
                <div class="flex justify-between items-center">
                  <span class="text-xs text-gray-500 dark:text-gray-400">最后编辑: {{ formatDate(session.updatedAt) }}</span>
                  <button 
                    @click="openSession(session)"
                    class="px-4 py-2 rounded-lg bg-gradient-to-r from-emerald-500 to-green-500 text-white hover:from-emerald-600 hover:to-green-600 transition-colors duration-200"
                  >
                    打开
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import UserSidebar from '@/shared/components/UserSidebar.vue';
import { Conversation } from '@/shared/types/type';

const router = useRouter();

const conversations = ref<Conversation[]>([]);
const currentConversationId = ref<string | null>(null);

// 画布会话状态
const canvasSessions = ref<any[]>([]);
const loading = ref(false);

// 接口返回的会话类型
interface CanvasSession {
  uid: number;
  sessionId: string;
  text: string;
  createdAt: string;
  updatedAt: string;
}

// 获取用户信息
const getUserInfo = () => {
  const userInfo = localStorage.getItem('userInfo');
  if (userInfo) {
    try {
      return JSON.parse(userInfo);
    } catch {
      return null;
    }
  }
  return null;
};

// 加载画布会话列表
const loadCanvasSessions = async () => {
  loading.value = true;
  try {
    const userInfo = getUserInfo();
    const userId = userInfo?.id;
    console.log('用户信息:', userInfo);
    console.log('用户ID:', userId);
    
    let url = '/canvas/session/list';
    if (userId) {
      url += `?userId=${userId}`;
    }
    console.log('请求URL:', url);
    console.log('请求token:', localStorage.getItem('token'));
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        token: localStorage.getItem('token') || ''
      }
    });
    console.log('响应状态:', response.status, response.statusText);

    if (!response.ok) {
      throw new Error(`请求失败: ${response.status} ${response.statusText}`);
    }

    const result = await response.json();
    if (result.code === 1) {
      canvasSessions.value = result.data || [];
    } else {
      console.error('获取会话列表失败:', result.msg);
    }
  } catch (error) {
    console.error('加载画布会话失败:', error);
  } finally {
    loading.value = false;
  }
};

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

// 创建新项目
const createNewProject = async () => {
  try {
    const userInfo = getUserInfo();
    const userId = userInfo?.id;
    
    if (!userId) {
      console.error('用户未登录');
      return;
    }
    
    const response = await fetch('/canvas/session', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      },
      body: JSON.stringify({ uid: userId, text: '未命名项目' })
    });
    
    const result = await response.json();
    if (result.code === 1) {
      const sessionId = result.data.sessionId;
      // 跳转到新会话的画布页面
      router.push(`/canvas/${sessionId}`);
    } else {
      console.error('创建会话失败:', result.msg);
      // 失败时跳转到创建页面
      router.push('/canvas/create');
    }
  } catch (error) {
    console.error('创建项目失败:', error);
    // 异常时跳转到创建页面
    router.push('/canvas/create');
  }
};

// 打开会话
const openSession = (session: CanvasSession) => {
  router.push(`/canvas/${session.sessionId}`);
};

// 重命名会话
const renameSession = async(session: CanvasSession) => {
  const newName = prompt('请输入新的项目名称:', session.text);
  if (newName && newName.trim() && newName !== session.text) {
    try {
      const response = await fetch('/canvas/session/update', {
        method: 'POST',
        headers: {
          token: localStorage.getItem('token') || '',
          'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `sessionId=${encodeURIComponent(session.sessionId)}&title=${encodeURIComponent(newName.trim())}`
      });
      
      if (response.ok) {
        try {
          const result = await response.json();
          console.log('会话更新结果:', result);
          if (result.code === 1) {
            // 更新本地会话名称
            const updatedSession = canvasSessions.value.find(s => s.sessionId === session.sessionId);
            if (updatedSession) {
              updatedSession.text = newName.trim();
            }
            console.log('会话标题更新成功');
          } else {
            console.error('会话标题更新失败:', result.msg);
          }
        } catch (jsonError) {
          // 如果响应不是JSON，直接更新本地状态
          const updatedSession = canvasSessions.value.find(s => s.sessionId === session.sessionId);
          if (updatedSession) {
            updatedSession.text = newName.trim();
          }
          console.log('会话标题更新成功');
        }
      } else {
        console.error('会话标题更新失败:', response.status);
      }
    } catch (error) {
      console.error('更新会话标题时出错:', error);
    }
  }
};

// 删除会话
const deleteSession = async (session: CanvasSession) => {
  if (confirm('确定要删除这个项目吗？')) {
    try {
      const response = await fetch(`/canvas/session/${session.sessionId}`, {
        method: 'DELETE',
        headers: {
          token: localStorage.getItem('token') || ''
        }
      });
      
      if (response.ok) {
        const result = await response.json();
        if (result.code === 1) {
          // 删除成功，更新本地状态
          canvasSessions.value = canvasSessions.value.filter(s => s.sessionId !== session.sessionId);
          console.log('会话删除成功');
        } else {
          console.error('删除会话失败:', result.msg);
        }
      } else {
        console.error('删除会话失败:', response.status);
      }
    } catch (error) {
      console.error('删除会话时出错:', error);
    }
  }
};

// 切换对话
const switchConversation = (id: string | number) => {
  currentConversationId.value = String(id);
};

// 创建临时对话
const createTemporaryConversation = () => {
  console.log('创建临时对话');
};

// 处理删除
const handleRemove = (payload: { sessionid: string | number }) => {
  console.log('删除对话:', payload.sessionid);
};

// 页面加载时获取会话列表
onMounted(() => {
  loadCanvasSessions();
});
</script>

<style scoped>
.bg-grid-pattern {
  background-image: linear-gradient(to right, rgba(16, 185, 129, 0.05) 1px, transparent 1px), linear-gradient(to bottom, rgba(16, 185, 129, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
}
</style>
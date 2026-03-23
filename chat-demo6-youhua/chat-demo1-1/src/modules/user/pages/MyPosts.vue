<template>
  <div class="flex h-screen relative overflow-hidden">
    <!-- 背景装饰元素 -->
    <div class="absolute inset-0 bg-gradient-to-br from-blue-50 to-indigo-50 dark:from-gray-900 dark:to-gray-800 z-0">
    </div>
    <div class="absolute inset-0 bg-grid-pattern opacity-5 z-0"></div>

    <!-- 动态装饰元素 -->
    <div
      class="absolute top-0 left-0 w-64 h-64 bg-gradient-to-br from-blue-200 to-indigo-200 rounded-full blur-3xl opacity-20 animate-pulse">
    </div>
    <div
      class="absolute bottom-0 right-0 w-96 h-96 bg-gradient-to-tr from-indigo-200 to-purple-200 rounded-full blur-3xl opacity-20 animate-pulse">
    </div>

    <!-- 左侧边栏 -->
    <UserSidebar
      mode="full"
      :conversations="[]"
      :current-conversation-id="null"
      :highlight-id="null"
      current-route="image-sharing"
      @switch-conversation="() => {}"
      @create-temporary-conversation="() => {}"
      @rename="() => {}"
      @remove="() => {}"
    />

    <!-- 主内容区域 -->
    <div class="flex-1 flex flex-col relative z-10 overflow-hidden">
      <!-- 页面内容 -->
      <div class="flex-1 overflow-y-auto p-6" ref="contentAreaRef">
        <h1 class="text-2xl font-bold mb-6">我的发布</h1>
    
    <!-- 发布统计 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div class="bg-blue-50 p-4 rounded-lg">
        <h3 class="text-lg font-semibold text-blue-800">总发布数</h3>
        <p class="text-3xl font-bold text-blue-600">{{ totalPosts }}</p>
      </div>
      <div class="bg-green-50 p-4 rounded-lg">
        <h3 class="text-lg font-semibold text-green-800">本月发布</h3>
        <p class="text-3xl font-bold text-green-600">{{ monthlyPosts }}</p>
      </div>
      <div class="bg-purple-50 p-4 rounded-lg">
        <h3 class="text-lg font-semibold text-purple-800">最近发布</h3>
        <p class="text-lg text-purple-600">{{ lastPostDate }}</p>
      </div>
    </div>
    
    <!-- 图片网格 - 横向排列布局 -->
    <div v-if="!loading && myPosts.length > 0" class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
      <div 
        v-for="post in myPosts" 
        :key="post.id"
        class="group cursor-pointer transform transition-all duration-300 hover:scale-[1.02]"
        @click="showImageDetail(post)"
      >
        <!-- 图片卡片容器 -->
        <div class="bg-white rounded-lg overflow-hidden shadow-sm hover:shadow-md transition-all duration-300 border border-gray-100 h-full flex flex-col">
          <!-- 图片容器 - 固定高度 -->
          <div class="relative overflow-hidden aspect-square">
            <img 
              :src="post.image_url" 
              :alt="post.image_title || 'My Post'"
              class="w-full h-full object-cover transition-all duration-500 ease-in-out transform-gpu"
              loading="lazy"
            />
            
            <!-- 悬停遮罩 -->
            <div class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
              <div class="text-white text-center p-2">
                <svg class="w-6 h-6 mx-auto mb-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
                <p class="text-xs font-medium">查看详情</p>
              </div>
            </div>
          </div>
          
          <!-- 图片信息 -->
          <div class="p-3 flex-1 flex flex-col justify-between">
            <div>
              <h3 class="font-semibold text-sm truncate mb-1">{{ post.image_title || '未命名图片' }}</h3>
              <p class="text-gray-600 text-xs mb-2 line-clamp-2" v-if="post.image_description">
                {{ post.image_description }}
              </p>
            </div>
            <div class="text-xs text-gray-500 flex justify-between items-center mt-auto">
              <span>{{ formatDate(post.created_at) }}</span>
              <span v-if="post.imagesize" class="bg-gray-100 px-1.5 py-0.5 rounded text-xs">{{ post.imagesize }}</span>
            </div>
          </div>
          
          <!-- 删除按钮 -->
          <div class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
            <button 
              @click.stop="confirmDelete(post)"
              class="bg-red-500 text-white p-1.5 rounded-full hover:bg-red-600 transition-colors shadow-md hover:shadow-lg"
              title="删除"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clip-rule="evenodd" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-else-if="!loading && myPosts.length === 0" class="text-center py-12">
      <p class="text-gray-500 text-lg">您还没有发布任何图片</p>
      <p class="text-gray-400 mt-2">快去文生图页面创作并分享您的作品吧！</p>
    </div>
    
    <!-- 加载状态 -->
    <div v-else-if="loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
    </div>
    
    <!-- 删除确认弹窗 -->
    <div 
      v-if="showDeleteConfirm" 
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <div class="bg-white rounded-lg p-6 max-w-md w-full mx-4 shadow-xl">
        <h3 class="text-lg font-semibold mb-4 text-gray-800">确认删除</h3>
        <p class="mb-6 text-gray-600">确定要删除这张图片吗？此操作不可撤销。</p>
        
        <div class="flex justify-end space-x-3">
          <button 
            @click="cancelDelete"
            class="px-4 py-2 border border-gray-300 rounded hover:bg-gray-100 transition-colors"
          >
            取消
          </button>
          <button 
            @click="performDelete"
            class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition-colors"
          >
            确定删除
          </button>
        </div>
      </div>
    </div>
    
    <!-- 图片详情弹窗组件 -->
    <MyPostDetailModal
      :visible="showDetailModal"
      :image-data="selectedImageForModal"
      @close="closeDetailModal"
      @delete="deleteSelectedImage"
    />
    </div>
  </div>
</div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { Imagepost, ImagePost } from '@/shared/types';
import imagepostService from '@/modules/user/services/imagepost';
import { useAuthStore } from '@/stores/auth';
import MyPostDetailModal from '@/shared/components/MyPostDetailModal.vue';
import UserSidebar from '@/shared/components/UserSidebar.vue';

const myPosts = ref<Imagepost[]>([]);
const loading = ref(false);
const showDeleteConfirm = ref(false);
const postToDelete = ref<Imagepost | null>(null);

// 图片详情弹窗相关
const showDetailModal = ref(false);
const selectedImage = ref<Imagepost | null>(null);

// 为MyPostDetailModal准备的转换数据
const selectedImageForModal = computed<ImagePost | null>(() => {
  if (!selectedImage.value) return null;
  
  // 处理日期字段
  const handleDate = (date: any): string | undefined => {
    if (!date) return undefined;
    if (typeof date === 'string') return date;
    if (date instanceof Date) return date.toISOString();
    return String(date);
  };
  
  return {
    id: selectedImage.value.id || 0,
    title: selectedImage.value.title || selectedImage.value.image_title || '未命名图片',
    image_title: selectedImage.value.title || selectedImage.value.image_title,
    image_url: selectedImage.value.imageUrl || selectedImage.value.image_url,
    imageUrl: selectedImage.value.imageUrl || selectedImage.value.image_url,
    description: selectedImage.value.caption || selectedImage.value.image_description,
    userId: selectedImage.value.userId || selectedImage.value.userid,
    createdAt: handleDate(selectedImage.value.createdAt) || handleDate(selectedImage.value.created_at),
    updatedAt: handleDate(selectedImage.value.updatedAt) || handleDate(selectedImage.value.updated_at),
    created_at: selectedImage.value.createdAt || selectedImage.value.created_at,
    updated_at: selectedImage.value.updatedAt || selectedImage.value.updated_at,
    commentCount: selectedImage.value.commentCount,
    likeCount: selectedImage.value.likeCount,
    status: selectedImage.value.status
  } as ImagePost;
});

// 使用Pinia store获取用户信息
const authStore = useAuthStore();

onMounted(async () => {
  // 确保用户信息已加载
  authStore.loadUserInfo();
  await fetchMyPosts();
});

const totalPosts = computed(() => myPosts.value.length);
const monthlyPosts = computed(() => {
  const now = new Date();
  const currentMonth = now.getMonth();
  const currentYear = now.getFullYear();
  
  return myPosts.value.filter(post => {
    if (!post.created_at) return false;
    const postDate = new Date(post.created_at);
    return postDate.getMonth() === currentMonth && postDate.getFullYear() === currentYear;
  }).length;
});

const lastPostDate = computed(() => {
  if (myPosts.value.length === 0) return '暂无';
  
  const sortedPosts = [...myPosts.value].sort((a, b) => 
    new Date(b.created_at!).getTime() - new Date(a.created_at!).getTime()
  );
  
  if (sortedPosts[0]?.created_at) {
    return formatDate(typeof sortedPosts[0].created_at === 'string' ? sortedPosts[0].created_at : new Date(sortedPosts[0].created_at!).toISOString());
  }
  return '暂无';
});

const fetchMyPosts = async () => {
  console.log('=== 开始获取我的发布 ===');
  
  // 再次确保用户信息已加载
  authStore.loadUserInfo();
  
  // 从store获取用户ID
  const userId = authStore.currentUser?.id;
  console.log('获取到的用户ID:', userId);
  console.log('当前用户信息:', authStore.currentUser);
  
  if (!userId) {
    console.log('❌ 用户未登录或信息未加载');
    console.log('详细信息:', {
      currentUser: authStore.currentUser,
      localStorageToken: !!localStorage.getItem('token'),
      localStorageUserInfo: localStorage.getItem('userInfo')
    });
    
    myPosts.value = [];
    return;
  }
  
  loading.value = true;
  
  try {
    console.log(`🚀 调用API获取用户 ${userId} 的图片列表`);
    
    // 调用获取用户图片的API
    const response = await imagepostService.getUserImageposts(userId);  
    
    // 处理API响应
    if (response) {
      if (Array.isArray(response)) {
        myPosts.value = response as Imagepost[];
        console.log(`📊 获取到 ${myPosts.value.length} 张图片`);
      } else if (typeof response === 'object' && response !== null) {
        if ('records' in response && Array.isArray((response as any).records)) {
          myPosts.value = (response as any).records;
        } else if ('userid' in response && 'image_url' in response) {
          myPosts.value = [response as unknown as Imagepost];
        } else {
          myPosts.value = [];
        }
      } else {
        myPosts.value = [];
      }
    } else {
      myPosts.value = [];
    }
    
    console.log('📋 处理结果:', {
      postsCount: myPosts.value.length,
      samplePost: myPosts.value[0]
    });

  } catch (error) {
    console.error('❌ 获取我的发布失败:', error);
    myPosts.value = [];
    alert('获取发布列表失败，请检查网络连接或稍后重试');
  } finally {
    loading.value = false;
  }
};

const confirmDelete = (post: Imagepost) => {
  postToDelete.value = post;
  showDeleteConfirm.value = true;
};

const cancelDelete = () => {
  showDeleteConfirm.value = false;
  postToDelete.value = null;
};

const performDelete = async () => {
  if (!postToDelete.value) return;
  
  try {
    await imagepostService.deleteImagepost(postToDelete.value);
    console.log('删除成功:', postToDelete.value);
    // 从本地数组中移除已删除的项目
    myPosts.value = myPosts.value.filter(post => post.id !== postToDelete.value?.id);
    // 使用非阻塞式通知
    showToast('删除成功', 'success');
  } catch (error) {
    console.error('删除失败:', error);
    showToast('删除失败，请稍后重试', 'error');
  } finally {
    cancelDelete();
  }
};

const showImageDetail = async (post: Imagepost) => {
  selectedImage.value = post;
  showDetailModal.value = true;

  // 获取用户ID
  const userId = authStore.currentUser?.id;
  if (!userId || !post.id) return;

  try {
    const response = await fetch(`/imagepost/post/${post.id}?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        token: localStorage.getItem('token') || ''
      }
    });

    if (response.ok) {
      const result = await response.json();
      console.log('获取图片详情返回数据:', result);
      if (result.data) {
        // 直接使用接口返回的数据
        const imageData = result.data;
        console.log('imageData:', imageData);
        selectedImage.value = imageData as Imagepost;
        console.log('更新后的selectedImage:', selectedImage.value);
      }
    }
  } catch (error) {
    console.error('获取图片详情失败:', error);
  }
};

const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedImage.value = null;
};

const deleteSelectedImage = async () => {
  if (!selectedImage.value) return;
  
  try {
    await imagepostService.deleteImagepost(selectedImage.value);
    // 从本地数组中移除已删除的项目
    myPosts.value = myPosts.value.filter(post => post.id !== selectedImage.value?.id);
    closeDetailModal();
    showToast('删除成功', 'success');
  } catch (error) {
    console.error('删除失败:', error);
    showToast('删除失败，请稍后重试', 'error');
  }
};


const formatDate = (dateString: string | Date | undefined) => {
  if (!dateString) return '';
  const date = typeof dateString === 'string' ? new Date(dateString) : dateString;
  return date.toLocaleDateString('zh-CN');
};

// 添加Toast通知函数
const showToast = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
  // 创建toast元素
  const toast = document.createElement('div');
  toast.className = `fixed top-4 left-1/2 transform -translate-x-1/2 px-6 py-3 rounded-lg shadow-lg z-50 transition-all duration-300 ${
    type === 'success' ? 'bg-green-500 text-white' :
    type === 'error' ? 'bg-red-500 text-white' :
    'bg-blue-500 text-white'
  }`;
  toast.textContent = message;
  
  document.body.appendChild(toast);
  
  // 动画显示
  setTimeout(() => {
    toast.style.opacity = '1';
    toast.style.transform = 'translate(-50%, 0)';
  }, 10);
  
  // 2-3秒后自动消失
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translate(-50%, -20px)';
    setTimeout(() => {
      document.body.removeChild(toast);
    }, 300);
  }, type === 'error' ? 4000 : 2500);
};
</script>

<style scoped>
/* 背景网格图案 */
.bg-grid-pattern {
  background-image:
    linear-gradient(to right, rgba(59, 130, 246, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(59, 130, 246, 0.05) 1px, transparent 1px);
  background-size: 20px 20px;
}

/* 横向网格布局样式 */
.grid {
  display: grid;
}

/* 图片卡片样式 */
.bg-white.rounded-lg.overflow-hidden {
  border-radius: 0.5rem;
  overflow: hidden;
}

/* 图片容器样式 - 固定1:1比例 */
.aspect-square {
  aspect-ratio: 1 / 1;
}

.relative.overflow-hidden {
  overflow: hidden;
  position: relative;
}

img {
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  display: block;
}

/* 悬停效果 */
.group:hover .relative.overflow-hidden img {
  transform: scale(1.05);
}

/* 删除按钮悬停效果 */
.absolute.top-2.right-2.opacity-0 {
  transition: all 0.2s ease;
}

.group:hover .absolute.top-2.right-2.opacity-0 {
  opacity: 1;
  transform: scale(1.1);
}

/* 多行文本截断 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>